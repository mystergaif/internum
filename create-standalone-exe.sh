#!/bin/bash

# Скрипт для создания самодостаточного EXE-файла для проекта InterNum

echo "=== InterNum: Создание самодостаточного EXE-файла ==="

# Проверяем, существует ли JAR-файл
if [ ! -f "target/internum-app-1.0-SNAPSHOT.jar" ]; then
    echo "JAR-файл не найден. Запускаем сборку..."
    ./build.sh
fi

# Проверяем наличие NSIS
if ! command -v makensis &> /dev/null; then
    echo "ОШИБКА: NSIS не установлен. Установите NSIS для создания самодостаточного EXE-файла."
    echo "Для Ubuntu/Debian: sudo apt-get install nsis"
    echo "Для Windows: Скачайте и установите NSIS с https://nsis.sourceforge.io/Download"
    exit 1
fi

# Проверяем наличие JRE
if [ ! -d "jre/windows/jdk-17.0.10+7-jre" ]; then
    echo "JRE не найдена. Скачиваем..."
    mkdir -p jre/windows
    wget -q --show-progress https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B7/OpenJDK17U-jre_x64_windows_hotspot_17.0.10_7.zip -O jre.zip
    echo "Распаковываем JRE..."
    unzip -q jre.zip -d jre/windows
    rm jre.zip
    echo "JRE успешно установлена."
fi

# Создаем самодостаточный EXE-файл с помощью NSIS
echo "Создаем самодостаточный EXE-файл с помощью NSIS..."
makensis installer.nsi

# Проверяем, успешно ли создан EXE-файл
if [ -f "InterNumApp_Standalone.exe" ]; then
    echo "Самодостаточный EXE-файл успешно создан: InterNumApp_Standalone.exe"
    echo "Размер файла: $(du -h InterNumApp_Standalone.exe | cut -f1)"
    
    # Копируем в директорию релизов
    cp InterNumApp_Standalone.exe releases/
    echo "Файл скопирован в директорию releases/"
else
    echo "ОШИБКА: Не удалось создать самодостаточный EXE-файл"
    exit 1
fi

# Создаем отладочную версию
echo "Создаем отладочную версию EXE-файла..."
makensis installer_with_error_handling.nsi

# Проверяем, успешно ли создана отладочная версия
if [ -f "InterNumApp_Standalone_Debug.exe" ]; then
    echo "Отладочная версия EXE-файла успешно создана: InterNumApp_Standalone_Debug.exe"
    echo "Размер файла: $(du -h InterNumApp_Standalone_Debug.exe | cut -f1)"
    
    # Копируем в директорию релизов
    cp InterNumApp_Standalone_Debug.exe releases/
    echo "Файл скопирован в директорию releases/"
else
    echo "ОШИБКА: Не удалось создать отладочную версию EXE-файла"
fi

echo "=== Готово! ==="
echo "Вы можете найти созданные файлы в директории releases/"
