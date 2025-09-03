# 1단계: 빌드용 (Gradle Wrapper 이용해서 JAR 만들기)
FROM openjdk:17-alpine AS builder
WORKDIR /app

# Gradle Wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 실행 권한 부여 후 빌드
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# 2단계: 실행용 (가벼운 이미지)
FROM openjdk:17-alpine
WORKDIR /app

EXPOSE 8080

# builder에서 만든 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Spring Boot 실행
ENTRYPOINT ["java", "-jar", "app.jar"]