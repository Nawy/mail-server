FROM openjdk:8-jre-alpine

ENV PROFILE="container"
ENV JAVA_MEM="-Xms256m -Xmx1024m"

COPY build/libs/mail-server.jar .
ENTRYPOINT exec java -Dspring.profiles.active=${PROFILE} ${JAVA_MEM}  -jar mail-server.jar