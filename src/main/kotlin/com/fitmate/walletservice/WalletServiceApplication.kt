package com.fitmate.walletservice

import com.fitmate.walletservice.common.GlobalStatus
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class WalletServiceApplication

fun main(args: Array<String>) {
    runApplication<WalletServiceApplication>(*args) {
        val active = System.getProperty(GlobalStatus.SPRING_PROFILES_ACTIVE)
        if (active == null) {
            System.setProperty(GlobalStatus.SPRING_PROFILES_ACTIVE, GlobalStatus.SPRING_PROFILES_ACTIVE_DEFAULT)
        }
        System.setProperty(
            GlobalStatus.SPRING_PROFILES_ACTIVE,
            System.getProperty(GlobalStatus.SPRING_PROFILES_ACTIVE, GlobalStatus.SPRING_PROFILES_ACTIVE_DEFAULT)
        )
    }
}
