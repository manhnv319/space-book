FROM maven:3.9.11-eclipse-temurin-21-alpine AS builder
WORKDIR /app

COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app
ENV JAVA_OPTS=""
LABEL org.opencontainers.image.source="https://github.com/manhnv319/space-book"

RUN apk add --no-cache libwebp-tools && addgroup -S spring && adduser -S spring -G spring
COPY --from=builder /app/target/*.jar app.jar

USER spring
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
