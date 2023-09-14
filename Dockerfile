# For Java 11, try this
FROM adoptopenjdk/openjdk11:alpine-jre

# Refer to Maven build -> finalName
ARG JAR_FILE=./app/target/authentication-service-0.0.1-SNAPSHOT.jar

# cd /opt/app
WORKDIR /opt/app

# cp app/target/authentication-service-0.0.1-SNAPSHOT.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]

#FROM openjdk:19-jdk-alpine3.16
#COPY target/authentication-service-0.0.1-SNAPSHOT.jar authentication-service-0.0.1-SNAPSHOT.jar
#ENTRYPOINT ["java","-jar","/authentication-service-0.0.1-SNAPSHOT.jar"]