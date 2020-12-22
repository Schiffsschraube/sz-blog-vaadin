FROM maven:3.6.3-adoptopenjdk-11 as builder

WORKDIR /app
COPY . /app
RUN mvn clean package -Pproduction

FROM maven:3.6.3-adoptopenjdk-11
WORKDIR /app

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
COPY --from=builder /app/target/sz-blog-vaadin-1.0-SNAPSHOT.jar /app/app.jar