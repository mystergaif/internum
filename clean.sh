#!/bin/bash

# Скрипт для очистки проекта от ненужных файлов перед публикацией на GitHub

echo "Очистка проекта от ненужных файлов..."

# Удаление скомпилированных файлов
rm -rf target/

# Удаление JRE
rm -rf jre/
rm -rf compact_jre/

# Удаление сгенерированных приложений
rm -rf InterNumApp/
rm -rf InterNumApp_GUI/
rm -rf InterNumApp_Standalone/

# Удаление архивов и исполняемых файлов
rm -f *.jar
rm -f *.exe
rm -f *.zip
rm -f *.7z
rm -f *.deb

# Удаление логов
rm -f *.log
rm -f internum_3d_log.txt
rm -f internum_app_log.txt

# Удаление Launch4j (можно скачать отдельно)
rm -rf launch4j/

# Удаление временных файлов
rm -f 7zsd_All_x64.sfx
rm -f jsmooth.zip
rm -f OpenJDK17U-jre_x64_windows_hotspot_17.0.10_7.zip

echo "Очистка завершена!"
