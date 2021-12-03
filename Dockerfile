FROM alpine/git:latest AS gitter
ADD "https://www.random.org/cgi-bin/randbyte?nbytes=10&format=h" skipcache
RUN git clone https://github.com/erdnando/coltrans-ms-incrementa.git /opt/cicd/java_ms_incrementa


FROM maven:3.6.0-jdk-11-slim AS build

COPY --from=gitter /opt/cicd/java_ms_incrementa  /opt/java_src
WORKDIR /opt/java_src

RUN mvn clean install 
#mvn package  && mvn spring-boot:run
#automatic build


# Stage 1, based on open-jdk, to have only the compiled app, ready for production
FROM openjdk:11-jre-slim

#Copy jar
COPY --from=build /opt/java_src/target/ms-incrementa-0.0.1-SNAPSHOT.jar  /usr/local/lib/ms-incrementa.jar


#docker rmi image erdnando/coltrans-ms-incrementa
#build
#docker build -t erdnando/coltrans-ms-incrementa:1.0 .
#local test
#docker run -itd -p 10001:8080 --net=host --name coltrans-ms-incrementa erdnando/coltrans-ms-incrementa:1.0
#push
#docker push erdnando/coltrans-ms-incrementa:1.0


EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/ms-incrementa.jar"]
