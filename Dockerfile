FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENV USE_PROFILE main
ENV JAVA_OPTS="-Xmx512m"
ENTRYPOINT ["java","-Dspring.profiles.active=${USE_PROFILE}","${JAVA_OPTS}","-jar","/app.jar"]
