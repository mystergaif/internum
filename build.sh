#!/bin/bash

# Скрипт для быстрой сборки проекта

echo "Сборка проекта..."

# Сборка JAR-файла
mvn clean package

# Проверка успешности сборки
if [ $? -eq 0 ]; then
    echo "JAR-файл успешно создан: target/internum-app-1.0-SNAPSHOT.jar"
    echo "Вы можете запустить его командой: java -jar target/internum-app-1.0-SNAPSHOT.jar"
else
    echo "Ошибка при сборке проекта!"
    exit 1
fi
