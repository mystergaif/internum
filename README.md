# InterNum - Самодостаточные Java-приложения

![InterNum Logo](docs/images/internum-logo.png)

InterNum - это демонстрационный проект, показывающий, как создавать полностью самодостаточные исполняемые файлы из Java-приложений, которые можно запускать на Windows без установки Java и без распаковки дополнительных файлов.

## 🚀 Возможности

- **Полная независимость** - не требует установки Java на компьютере пользователя
- **Один файл** - не требует распаковки дополнительных файлов перед запуском
- **Без следов** - не оставляет временных файлов после завершения работы
- **Простота использования** - запускается одним щелчком мыши
- **Кроссплатформенность** - поддерживает Windows, Linux и macOS (с разными методами упаковки)

## 📦 Готовые сборки

В разделе [Releases](https://github.com/mystergaif/internum/releases) вы найдете готовые сборки:

- **InterNumApp_Standalone.exe** (28 МБ) - полностью самодостаточный EXE-файл для Windows
- **InterNumApp_Standalone_Debug.exe** (28 МБ) - отладочная версия с логированием
- **internum-app-1.0-SNAPSHOT.jar** (2.5 МБ) - JAR-файл для запуска на системах с установленной Java 17

## 🖥️ Демонстрационное приложение

Проект включает простое 3D-приложение с вращающимся кубом, использующее LWJGL (Lightweight Java Game Library) и OpenGL:

![InterNum 3D Cube](docs/images/internum-screenshot.png)

Куб имеет разноцветные грани:
- Передняя грань - красная
- Задняя грань - зеленая
- Верхняя грань - синяя
- Нижняя грань - желтая
- Правая грань - пурпурная
- Левая грань - голубая

## 🛠️ Технологии упаковки

Проект демонстрирует несколько методов создания самодостаточных приложений:

### 1. NSIS (Nullsoft Scriptable Install System)

Создает EXE-файл, который:
- Распаковывает приложение и JRE во временную директорию
- Запускает приложение
- После закрытия приложения удаляет все временные файлы

### 2. Launch4j

Создает EXE-файл, который:
- Запускает JAR-файл с помощью указанной JRE
- Поддерживает настройку параметров запуска
- Показывает заставку и сообщения об ошибках

### 3. Maven Shade Plugin

Создает "fat JAR" - JAR-файл, который:
- Содержит все необходимые зависимости
- Может быть запущен с помощью команды `java -jar`
- Требует наличия установленной Java на компьютере пользователя

## 🔧 Сборка проекта

### Требования

- Java Development Kit (JDK) 17 или выше
- Apache Maven 3.6.0 или выше
- Launch4j (для создания EXE-файлов)
- NSIS (для создания самодостаточных EXE-файлов)

### Шаги сборки

1. Клонировать репозиторий:
   ```bash
   git clone https://github.com/mystergaif/internum.git
   cd internum
   ```

2. Собрать JAR-файл:
   ```bash
   ./build.sh
   ```

3. Создать самодостаточный EXE-файл:
   ```bash
   ./create-standalone-exe.sh
   ```

## 📚 Руководство по созданию EXE из вашего JAR-файла

Вы можете использовать этот проект как шаблон для создания самодостаточных EXE-файлов из ваших собственных JAR-файлов. Ниже приведены подробные инструкции для каждого метода.

### Метод 1: Создание самодостаточного EXE с помощью NSIS

NSIS позволяет создать EXE-файл, который включает в себя JAR-файл и JRE, распаковывает их во временную директорию, запускает приложение и затем удаляет временные файлы.

1. **Установите NSIS**:
   - Windows: Скачайте и установите NSIS с [официального сайта](https://nsis.sourceforge.io/Download)
   - Linux: `sudo apt-get install nsis` (Ubuntu/Debian) или `sudo dnf install nsis` (Fedora)

2. **Подготовьте JAR-файл**:
   - Убедитесь, что ваш JAR-файл содержит все необходимые зависимости (fat JAR)
   - Проверьте, что JAR-файл правильно указывает главный класс в манифесте

3. **Скачайте JRE**:
   ```bash
   mkdir -p jre/windows
   wget https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B7/OpenJDK17U-jre_x64_windows_hotspot_17.0.10_7.zip -O jre.zip
   unzip jre.zip -d jre/windows
   rm jre.zip
   ```

4. **Создайте скрипт NSIS**:
   Создайте файл `your-app.nsi` со следующим содержимым (замените пути и имена на свои):

   ```nsis
   !include "MUI2.nsh"
   !include "FileFunc.nsh"

   Name "YourAppName"
   OutFile "YourApp_Standalone.exe"
   Unicode True

   ; Временная директория для распаковки
   Var TempDir

   ; Интерфейс
   !define MUI_ICON "path/to/your/icon.ico"
   !define MUI_WELCOMEFINISHPAGE_BITMAP "path/to/your/splash.bmp"

   ; Страницы
   !insertmacro MUI_PAGE_INSTFILES
   !insertmacro MUI_LANGUAGE "Russian"

   Section "Main"
       ; Создаем временную директорию
       GetTempFileName $TempDir
       Delete $TempDir
       CreateDirectory $TempDir

       ; Распаковываем JRE
       SetOutPath "$TempDir\jre"
       File /r "jre\windows\*"

       ; Распаковываем JAR
       SetOutPath "$TempDir"
       File "path/to/your/app.jar"

       ; Запускаем приложение
       ExecWait '"$TempDir\jre\bin\javaw.exe" -jar "$TempDir\app.jar"'

       ; Удаляем временные файлы
       RMDir /r "$TempDir"
   SectionEnd
   ```

5. **Скомпилируйте EXE-файл**:
   ```bash
   makensis your-app.nsi
   ```

6. **Проверьте результат**:
   - Запустите созданный EXE-файл
   - Убедитесь, что приложение работает корректно
   - Проверьте, что временные файлы удаляются после закрытия приложения

### Метод 2: Создание EXE с помощью Launch4j

Launch4j создает EXE-файл, который запускает JAR-файл с помощью указанной JRE. Этот метод не включает JRE в EXE-файл, но позволяет указать путь к JRE или использовать системную Java.

1. **Установите Launch4j**:
   - Windows: Скачайте и установите Launch4j с [официального сайта](http://launch4j.sourceforge.net/)
   - Linux: Скачайте архив и распакуйте его:
     ```bash
     wget https://sourceforge.net/projects/launch4j/files/launch4j-3/3.50/launch4j-3.50-linux.tgz
     tar -xzf launch4j-3.50-linux.tgz
     ```

2. **Создайте конфигурационный XML-файл**:
   Создайте файл `your-app.xml` со следующим содержимым (замените пути и имена на свои):

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <launch4jConfig>
     <dontWrapJar>false</dontWrapJar>
     <headerType>gui</headerType>
     <jar>path/to/your/app.jar</jar>
     <outfile>YourApp.exe</outfile>
     <errTitle>Ошибка приложения</errTitle>
     <cmdLine></cmdLine>
     <chdir>.</chdir>
     <priority>normal</priority>
     <downloadUrl>https://adoptium.net/</downloadUrl>
     <supportUrl></supportUrl>
     <stayAlive>false</stayAlive>
     <restartOnCrash>false</restartOnCrash>
     <manifest></manifest>
     <icon>path/to/your/icon.ico</icon>
     <jre>
       <path>%JAVA_HOME%;%PATH%</path>
       <bundledJre64Bit>false</bundledJre64Bit>
       <bundledJreAsFallback>false</bundledJreAsFallback>
       <minVersion>17</minVersion>
       <maxVersion></maxVersion>
       <jdkPreference>preferJre</jdkPreference>
       <runtimeBits>64/32</runtimeBits>
     </jre>
     <splash>
       <file>path/to/your/splash.bmp</file>
       <waitForWindow>true</waitForWindow>
       <timeout>5</timeout>
       <timeoutErr>true</timeoutErr>
     </splash>
     <versionInfo>
       <fileVersion>1.0.0.0</fileVersion>
       <txtFileVersion>1.0.0</txtFileVersion>
       <fileDescription>Your Application Description</fileDescription>
       <copyright>Your Copyright</copyright>
       <productVersion>1.0.0.0</productVersion>
       <txtProductVersion>1.0.0</txtProductVersion>
       <productName>Your Application Name</productName>
       <companyName>Your Company</companyName>
       <internalName>YourApp</internalName>
       <originalFilename>YourApp.exe</originalFilename>
     </versionInfo>
   </launch4jConfig>
   ```

3. **Создайте EXE-файл**:
   ```bash
   # Windows
   launch4j.exe your-app.xml

   # Linux
   ./launch4j/launch4j your-app.xml
   ```

4. **Для включения JRE в EXE-файл**:
   Измените секцию `<jre>` в XML-файле:
   ```xml
   <jre>
     <path>jre</path>
     <bundledJre64Bit>true</bundledJre64Bit>
     <bundledJreAsFallback>true</bundledJreAsFallback>
     <minVersion></minVersion>
     <maxVersion></maxVersion>
     <jdkPreference>jreOnly</jdkPreference>
     <runtimeBits>64</runtimeBits>
   </jre>
   ```
   И поместите JRE в директорию `jre` рядом с EXE-файлом.

### Метод 3: Создание самодостаточного JAR с помощью Maven Shade Plugin

Этот метод создает "fat JAR" - JAR-файл, который содержит все необходимые зависимости. Он не создает EXE-файл, но может быть использован как основа для методов 1 и 2.

1. **Добавьте Maven Shade Plugin в ваш pom.xml**:
   ```xml
   <build>
     <plugins>
       <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-shade-plugin</artifactId>
         <version>3.4.1</version>
         <executions>
           <execution>
             <phase>package</phase>
             <goals>
               <goal>shade</goal>
             </goals>
             <configuration>
               <transformers>
                 <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                   <mainClass>com.yourcompany.yourapp.MainClass</mainClass>
                 </transformer>
               </transformers>
             </configuration>
           </execution>
         </executions>
       </plugin>
     </plugins>
   </build>
   ```

2. **Соберите JAR-файл**:
   ```bash
   mvn clean package
   ```

3. **Проверьте результат**:
   ```bash
   java -jar target/your-app-1.0-SNAPSHOT.jar
   ```

### Метод 4: Использование jpackage (Java 14+)

jpackage - это инструмент, включенный в JDK 14 и выше, который позволяет создавать нативные установщики для различных платформ.

1. **Подготовьте JAR-файл**:
   - Создайте fat JAR с помощью Maven Shade Plugin (см. Метод 3)

2. **Создайте EXE-файл с помощью jpackage**:
   ```bash
   jpackage --input target/ \
     --name YourApp \
     --main-jar your-app-1.0-SNAPSHOT.jar \
     --main-class com.yourcompany.yourapp.MainClass \
     --type app-image \
     --icon path/to/your/icon.ico \
     --app-version 1.0.0 \
     --vendor "Your Company" \
     --copyright "Copyright © 2024 Your Company" \
     --description "Your Application Description"
   ```

3. **Для создания установщика**:
   ```bash
   jpackage --input target/ \
     --name YourApp \
     --main-jar your-app-1.0-SNAPSHOT.jar \
     --main-class com.yourcompany.yourapp.MainClass \
     --type exe \
     --icon path/to/your/icon.ico \
     --app-version 1.0.0 \
     --vendor "Your Company" \
     --copyright "Copyright © 2024 Your Company" \
     --description "Your Application Description" \
     --win-shortcut \
     --win-menu
   ```

### Советы и рекомендации

1. **Тестирование**:
   - Всегда тестируйте созданные EXE-файлы на чистой системе без установленной Java
   - Проверяйте работу на разных версиях Windows

2. **Размер файла**:
   - NSIS и Launch4j с включенной JRE создают большие файлы (20-30 МБ)
   - Для уменьшения размера можно использовать компактную JRE, созданную с помощью jlink

3. **Логирование**:
   - Добавьте логирование в ваше приложение для отладки проблем
   - Создайте отдельную отладочную версию EXE-файла, которая сохраняет логи

4. **Иконки и заставки**:
   - Используйте качественные иконки и заставки для профессионального вида
   - Иконки должны быть в формате .ico, заставки - в формате .bmp

5. **Обработка ошибок**:
   - Добавьте обработку ошибок в скрипты NSIS и конфигурацию Launch4j
   - Показывайте понятные сообщения об ошибках пользователю

## 📋 Использование

### Windows

1. Скачайте `InterNumApp_Standalone.exe` из раздела [Releases](https://github.com/mystergaif/internum/releases)
2. Запустите файл двойным щелчком
3. Наслаждайтесь приложением!

### Linux/macOS

1. Убедитесь, что у вас установлена Java 17 или выше
2. Скачайте `internum-app-1.0-SNAPSHOT.jar` из раздела [Releases](https://github.com/mystergaif/internum/releases)
3. Запустите файл командой:
   ```bash
   java -jar internum-app-1.0-SNAPSHOT.jar
   ```

## 🤝 Вклад в проект

Мы приветствуем вклад в проект! Если у вас есть идеи по улучшению:

1. Форкните репозиторий
2. Создайте ветку для вашей функции (`git checkout -b feature/amazing-feature`)
3. Зафиксируйте изменения (`git commit -m 'Add some amazing feature'`)
4. Отправьте изменения в ваш форк (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## 📄 Лицензия

Этот проект распространяется под лицензией MIT. См. файл [LICENSE](LICENSE) для получения дополнительной информации.

## 📞 Контакты

MisterGaif - [GitHub](https://github.com/mystergaif)

Ссылка на проект: [https://github.com/mystergaif/internum](https://github.com/mystergaif/internum)
