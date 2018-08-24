FROM openjdk:8
ADD BNS*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
