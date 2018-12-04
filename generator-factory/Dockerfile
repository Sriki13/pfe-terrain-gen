FROM maven:latest

COPY ./src ./src
COPY ./pom.xml ./pom.xml

CMD mvn clean install
EXPOSE 9090
ENTRYPOINT mvn exec:java

