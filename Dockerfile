# 0) Build
FROM gradle:8.10.1-jdk21 AS builder
WORKDIR /workspace

# gradle wrapper & settings 먼저
COPY build.gradle.kts settings.gradle.kts gradle.properties gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew
RUN ./gradlew --version

# (선택) 의존성 워밍업 - 멀티모듈이면 범위를 좁히는 게 안전
RUN ./gradlew :main-runner:dependencies --no-daemon || true

# 소스 복사
COPY . .

# bootJar
RUN chmod +x gradlew
RUN ./gradlew :main-runner:bootJar --no-daemon


# 1) Run
FROM eclipse-temurin:21-jre
WORKDIR /app

# builder 산출물 복사 (고정 파일명 대신 와일드카드가 안전)
COPY --from=builder /workspace/monolith/main-runner/build/libs/*.jar /app/app.jar

EXPOSE 8080

# Railway 8GB 기준
ENV JAVA_OPTS="\
-Xms512m \
-Xmx6g \
-XX:MaxMetaspaceSize=512m \
-XX:+UseG1GC \
-XX:MaxGCPauseMillis=200 \
-XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
