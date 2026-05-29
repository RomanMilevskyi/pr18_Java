# 1. Беремо офіційний базовий образ з Java 17
FROM eclipse-temurin:17-jdk-alpine

# 2. Створюємо робочу директорію всередині контейнера
WORKDIR /app

# 3. Копіюємо згенерований jar-файл із папки target в образ
COPY target/pr13-0.0.1-SNAPSHOT.jar app.jar

# 4. Вказуємо команду для запуску додатка всередині контейнера
ENTRYPOINT ["java", "-jar", "app.jar"]