# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests -B

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8010
# JVM: tez start (C1 compiler, Serial GC, kichik heap)
ENV JAVA_OPTS="-XX:TieredStopAtLevel=1 -XX:+UseSerialGC -Xmx256m -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
