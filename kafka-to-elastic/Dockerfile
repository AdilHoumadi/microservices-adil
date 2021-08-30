FROM adoptopenjdk/openjdk11:alpine
RUN apk add bash curl
ADD bin/wait-for-it.sh /app/wait-for-it.sh
ADD bin/run.sh /app/run.sh
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} /app/app.jar
EXPOSE 8080
CMD ["/app/run.sh"]