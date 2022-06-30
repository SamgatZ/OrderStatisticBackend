FROM maven:3.5-jdk-11 AS build
MAINTAINER Zhaishylyk Samgat
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package
EXPOSE 8082
ENTRYPOINT ["java","-jar","/home/app/target/OrderStatisticBackend.jar"]