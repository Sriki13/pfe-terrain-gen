FROM maven:ibmjava

COPY ./src ./src
COPY ./pom.xml ./pom.xml

RUN ls

RUN mvn clean install
EXPOSE 9090
ENTRYPOINT mvn exec:java

