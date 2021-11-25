FROM alpine/git:latest AS gitter
ADD "https://www.random.org/cgi-bin/randbyte?nbytes=10&format=h" skipcache
RUN git clone https://github.com/erdnando/coltrans-ms-incrementa.git /opt/java_src


FROM maven:3.6.0-jdk-11-slim AS build

COPY --from=gitter /opt/java_src  /opt/java_src
WORKDIR /opt/java_src

RUN mvn package && mvn spring-boot:run


# Stage 1, based on Nginx, to have only the compiled app, ready for production with Nginx
#FROM openjdk:11-jre-slim

#Copy ci-dashboard-dist
#COPY --from=build /opt/java_src/target/ms-incrementa-0.0.1-SNAPSHOT.jar  /usr/local/lib/ms-incrementa.jar



#docker rmi image erdnando/coltrans-vuejs-websocket
#build
#docker build -t erdnando/coltrans-vuejs-websocket:1.0 .
#local test
#docker run -itd -p 8080:8080 --net=host --name coltrans-vuejs-websocket erdnando/coltrans-vuejs-websocket:1.0
#push
#docker push erdnando/coltrans-vuejs-websocket:1.0


EXPOSE 8080
#ENTRYPOINT ["java","-jar","/usr/local/lib/ms-incrementa.jar"]

#mvn clean package && mvn spring-boot:run
