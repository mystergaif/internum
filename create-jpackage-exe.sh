#!/bin/bash

# Скрипт для создания самодостаточного EXE-файла с помощью jpackage

echo "Создание самодостаточного EXE-файла с помощью jpackage..."

# Проверяем, существует ли JAR-файл
if [ ! -f "target/internum-app-1.0-SNAPSHOT.jar" ]; then
    echo "JAR-файл не найден. Запускаем сборку..."
    ./build.sh
fi

# Создаем временную директорию для входных файлов
mkdir -p input
cp target/internum-app-1.0-SNAPSHOT.jar input/

# Создаем самодостаточный EXE-файл
jpackage \
  --input input \
  --name InterNumApp \
  --main-jar internum-app-1.0-SNAPSHOT.jar \
  --main-class com.internum.AppLauncher \
  --type app-image \
  --dest output \
  --java-options "-Xms128m -Xmx512m" \
  --description "InterNum 3D Cube Application" \
  --vendor "Your Name" \
  --copyright "Copyright (c) 2024 Your Name" \
  --app-version 1.0.0

# Проверяем, успешно ли создан EXE-файл
if [ -d "output/InterNumApp" ]; then
    echo "Самодостаточное приложение успешно создано: output/InterNumApp"
    echo "Размер директории: $(du -sh output/InterNumApp | cut -f1)"
else
    echo "Ошибка при создании самодостаточного приложения"
    exit 1
fi

# Создаем ZIP-архив с самодостаточным приложением
echo "Создание ZIP-архива с самодостаточным приложением..."
cd output
zip -r InterNumApp_JPackage.zip InterNumApp
cd ..

echo "ZIP-архив успешно создан: output/InterNumApp_JPackage.zip"
echo "Готово!"
