package com.fitmate.walletservice.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fitmate.walletservice.exception.NotExpectResultException
import io.netty.channel.ChannelOption
import io.netty.handler.logging.LogLevel
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.io.StringReader
import java.time.Duration
import java.util.function.Consumer

@Component
class SenderUtils(
    private var webClientBuilder: WebClient.Builder,
    private var objectMapper: ObjectMapper
) {

    companion object {
        val logger: Logger? = LoggerFactory.getLogger(SenderUtils::class.java)
    }

    fun <T> send(
        method: HttpMethod,
        uri: String,
        header: Map<String, String>?,
        jsonData: Any?,
        responseClass: ParameterizedTypeReference<T>
    ): ResponseEntity<T> {
        return retrieve(method, uri, jsonData, makeHeader(header), responseClass)
    }


    private fun makeHeader(headerMap: Map<String, String>?): Consumer<HttpHeaders> {
        return Consumer<HttpHeaders> { headers: HttpHeaders ->
            if (headerMap != null) {
                for (key in headerMap.keys) {
                    headers.add(key, headerMap[key])
                }
            }
        }
    }

    private fun <T> retrieve(
        method: HttpMethod, uri: String, jsonData: Any?, headers: Consumer<HttpHeaders>?,
        responseClass: ParameterizedTypeReference<T>
    ): ResponseEntity<T> {
        val beforeTime = System.currentTimeMillis()
        var afterTime: Long
        var theSec: Long

        try {
            val webClient: WebClient = getWebClient()

            val request: WebClient.RequestHeadersSpec<*> = webClient
                .method(method)
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(jsonData ?: "")

            if (headers != null) {
                request.headers(headers)
            }

            logger?.info(
                "WebClient Request Start - Request Time : {}, Request Uri = {}, Request Data [{}]",
                beforeTime,
                uri,
                objectMapper.writeValueAsString(jsonData)
            )

            val responseEntity: ResponseEntity<T>? = request
                .retrieve()
                .bodyToMono(String::class.java)
                .map { body ->
                    logger?.info("WebClient Response body: {}", body)

                    val jsonParser = objectMapper.factory.createParser(StringReader(body))

                    val transformedBody: T =
                        objectMapper.readValue(jsonParser, objectMapper.constructType(responseClass.type))
                    ResponseEntity.ok().body(transformedBody)
                }
                .block()

            afterTime = System.currentTimeMillis()
            theSec = (afterTime - beforeTime) / 1000

            logger?.info("WebClient Response [ Processing time : $theSec End Time : $afterTime ]")

            if (responseEntity == null) throw NotExpectResultException("WebClientRequest Exception response entity is null")

            responseEntity.let {
                if (responseEntity.body != null) {
                    logger?.info("response = [" + objectMapper.writeValueAsString(responseEntity.body) + "]")
                }
            }

            return responseEntity
        } catch (wre: WebClientResponseException) {
            logger?.warn("WebClientResponseException", wre)

            afterTime = System.currentTimeMillis()
            theSec = (afterTime - beforeTime) / 1000

            logger?.info("WebClient Exception Response [ Processing time : $theSec End Time : $afterTime ]")

            throw wre
        } catch (e: Exception) {
            logger?.error("<ALARM_ERROR>WebClient Exception - [ Request time : $beforeTime ]$e")

            throw NotExpectResultException("WebClientRequest Exception$e")
        }
    }

    @Synchronized
    private fun getWebClient(): WebClient {
        val httpClient: HttpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .responseTimeout(Duration.ofMillis(8000))
            .doOnConnected { connection ->
                connection.addHandlerLast(ReadTimeoutHandler(5000))
                    .addHandlerLast(WriteTimeoutHandler(5000))
            }
            .wiretap(this.javaClass.canonicalName, LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
        val conn: ClientHttpConnector = ReactorClientHttpConnector(httpClient)

        return webClientBuilder
            .codecs { clientDefaultCodecsConfigurer ->
                clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(
                    Jackson2JsonEncoder(
                        getObjectMapper(),
                        MediaType.APPLICATION_JSON
                    )
                )
                clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(
                    Jackson2JsonDecoder(
                        getObjectMapper(),
                        MediaType.APPLICATION_JSON
                    )
                )
            }
            .clientConnector(conn)
            .build()
    }

    private fun getObjectMapper(): ObjectMapper {
        val om = ObjectMapper()
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return om
    }
}