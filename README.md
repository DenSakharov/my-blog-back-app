# My Blog Backend

Backend-приложение для блога на Java и Spring Boot.

## Технологии

- Java 21
- Spring Boot 3.4.4
- Gradle
- Spring Web
- Spring Data JDBC
- Spring Boot Actuator
- Spring Validation
- PostgreSQL
- Flyway
- H2 для тестов
- JUnit 5
- Mockito
- MockMvc

## Требования

- Java 21 или выше
- Gradle Wrapper
- PostgreSQL для запуска приложения

## Сборка проекта

Для сборки проекта выполните:

```bash
./gradlew clean build
```

## Запуск тестов 
```bash
./gradlew clean test
```

## Сборка Executable JAR
Для создания исполняемого JAR-файла:

```bash
./gradlew clean bootJar
```
Готовый файл появится в директории:

```text
build/libs/
```
Запуск приложения
```bash
java -jar build/libs/my-blog-backend-0.0.1-SNAPSHOT.jar
```