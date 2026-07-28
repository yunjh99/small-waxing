FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY settings.gradle build.gradle ./
COPY src src

RUN chmod +x gradlew \
    && ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN groupadd --system waxing \
    && useradd --system --gid waxing --create-home waxing \
    && mkdir -p /data/waxing/uploads \
    && chown -R waxing:waxing /app /data/waxing

COPY --from=builder --chown=waxing:waxing /workspace/build/libs/*.jar app.jar

USER waxing

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
