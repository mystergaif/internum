#!/bin/bash

# Скрипт для создания самодостаточного EXE-файла с встроенной JRE

echo "Создание самодостаточного EXE-файла с встроенной JRE..."

# Проверяем, существует ли JAR-файл
if [ ! -f "target/internum-app-1.0-SNAPSHOT.jar" ]; then
    echo "JAR-файл не найден. Запускаем сборку..."
    ./build.sh
fi

# Проверяем, существует ли JRE
if [ ! -d "jre/windows/jdk-17.0.10+7-jre" ]; then
    echo "JRE не найдена. Проверьте, что JRE распакована в директорию jre/windows/jdk-17.0.10+7-jre"
    exit 1
fi

# Запускаем Launch4j для создания EXE-файла
echo "Запускаем Launch4j для создания EXE-файла..."
./launch4j/launch4j internum-app.xml

# Проверяем, успешно ли создан EXE-файл
if [ -f "InterNumApp.exe" ]; then
    echo "Самодостаточный EXE-файл с встроенной JRE успешно создан: InterNumApp.exe"
    echo "Размер файла: $(du -h InterNumApp.exe | cut -f1)"
else
    echo "Ошибка при создании самодостаточного EXE-файла"
    exit 1
fi

echo "Готово!"
