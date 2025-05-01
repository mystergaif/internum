# InterNum - Демонстрационный проект для создания самодостаточных Java-приложений

Этот проект демонстрирует различные способы создания самодостаточных исполняемых файлов из Java-приложения, которые можно распространять без необходимости установки Java на компьютер пользователя.

## Описание

Проект содержит простое 3D-приложение с вращающимся кубом, использующее LWJGL (Lightweight Java Game Library) и OpenGL. Основная цель проекта - продемонстрировать различные методы упаковки Java-приложения в самодостаточные исполняемые файлы.

Куб имеет разноцветные грани:
- Передняя грань - красная
- Задняя грань - зеленая
- Верхняя грань - синяя
- Нижняя грань - желтая
- Правая грань - пурпурная
- Левая грань - голубая

## Возможности проекта

- Создание JAR-файла со всеми зависимостями (fat JAR)
- Создание автономного приложения с включенной JRE с помощью packr
- Создание EXE-файла с помощью Launch4j
- Создание установщика с помощью NSIS

## Требования для сборки

- Java Development Kit (JDK) 17 или выше
- Apache Maven 3.6.0 или выше
- Launch4j для создания EXE-файла (опционально)
- NSIS для создания установщика (опционально)
- Интегрированная среда разработки (IDE) с поддержкой Java (рекомендуется IntelliJ IDEA или Eclipse)

## Команды для сборки

### 1. Клонирование репозитория (если используете Git)

```bash
# Клонировать репозиторий
git clone <url-репозитория>
cd internum-app
```

### 2. Сборка JAR-файла

```bash
# Перейти в директорию проекта
cd internum-app

# Очистить и собрать проект
mvn clean package
```

Эта команда создаст JAR-файл `target/internum-app-1.0-SNAPSHOT.jar` со всеми зависимостями.

### 3. Создание автономного приложения с помощью packr и NSIS

Мы можем создать автономное приложение с помощью packr, а затем создать установщик с помощью NSIS, который будет распаковывать приложение во временную директорию, запускать его, а затем удалять временные файлы.

#### Шаг 1: Создание автономного приложения с помощью packr

[Packr](https://github.com/libgdx/packr) - это инструмент, который создает нативные исполняемые файлы для Java-приложений, включая минимизированную JRE.

```bash
# Скачать packr
wget https://github.com/libgdx/packr/releases/download/4.0.0/packr-all-4.0.0.jar

# Создать конфигурационный файл packr
cat > packr-config.json << EOF
{
    "platform": "windows64",
    "jdk": "jre",
    "executable": "InterNumApp",
    "classpath": [
        "target/internum-app-1.0-SNAPSHOT.jar"
    ],
    "mainclass": "com.internum.AppLauncher",
    "vmargs": [
        "-Xms128m",
        "-Xmx512m"
    ],
    "minimizejre": "soft",
    "output": "InterNumApp_Standalone"
}
EOF

# Запустить packr
java -jar packr-all-4.0.0.jar packr-config.json
```

Эта команда создаст директорию `InterNumApp_Standalone` с автономным приложением, включающим минимизированную JRE.

#### Шаг 2: Создание установщика с помощью NSIS

[NSIS](https://nsis.sourceforge.io/) - это система для создания установщиков Windows.

```bash
# Создать скрипт NSIS
cat > installer.nsi << EOF
; Installer script for InterNum 3D Cube Application

; Define the name of the installer
Name "InterNum 3D Cube Application"
OutFile "InterNumApp_Installer.exe"

; Default installation directory
InstallDir "\$TEMP\\InterNumApp"

; Request application privileges
RequestExecutionLevel user

; Set compression
SetCompressor /SOLID lzma

; Silent installer
SilentInstall silent
AutoCloseWindow true
ShowInstDetails hide

; Modern UI
!include "MUI2.nsh"

; No pages
!insertmacro MUI_PAGE_INSTFILES

; Language
!insertmacro MUI_LANGUAGE "Russian"

; The stuff to install
Section "Main Application" SecMain
  ; Set cursor to wait
  System::Call 'user32::SetCursor(i \$HWNDPARENT)'

  ; Create directory
  SetOutPath "\$INSTDIR"

  ; Copy all files from the InterNumApp_Standalone directory
  File /r "InterNumApp_Standalone\\*.*"

  ; Run the application
  ExecWait '"\$INSTDIR\\InterNumApp.exe"'

  ; Clean up after the application is closed
  RMDir /r "\$INSTDIR"
SectionEnd

; Set cursor back to normal on exit
Function .onGUIEnd
  System::Call 'user32::SetCursor(i 0)'
FunctionEnd
EOF

# Создать установщик
makensis installer.nsi
```

Эта команда создаст установщик `InterNumApp_Installer.exe`, который при запуске будет показывать только анимацию загрузки (курсор в виде песочных часов), распаковывать все необходимые файлы во временную директорию, запускать приложение, а после закрытия приложения удалять временные файлы.

### 4. Альтернативный способ: Создание EXE-файла с помощью Launch4j

Если вы предпочитаете использовать Launch4j, вы можете создать EXE-файл следующим образом:

#### Для Windows:

```bash
# Запустить Launch4j с конфигурационным файлом
launch4j.exe internum-app.xml
```

#### Для Linux:

```bash
# Запустить Launch4j с конфигурационным файлом
./launch4j/launch4j internum-app.xml
```

Эта команда создаст EXE-файл `InterNumApp.exe` в корневой директории проекта. Обратите внимание, что этот EXE-файл будет искать JRE в папке `jre`, которая должна находиться в той же директории.

## Запуск приложения

### Запуск из исходного кода

```bash
# Запустить приложение с помощью Maven
mvn exec:java -Dexec.mainClass="com.internum.Cube3DApp"
```

### Запуск JAR-файла

```bash
# Запустить JAR-файл
java -jar target/internum-app-1.0-SNAPSHOT.jar
```

### Запуск приложения

#### Запуск установщика, созданного с помощью NSIS

Если вы создали установщик с помощью NSIS, просто запустите `InterNumApp_Installer.exe`. При запуске установщика:

1. Курсор мыши изменится на "песочные часы" (анимация загрузки).
2. Установщик автоматически распакует все необходимые файлы во временную директорию (`%TEMP%\InterNumApp`).
3. Запустится приложение с 3D кубом.
4. После закрытия приложения все временные файлы будут автоматически удалены.

Это самый удобный способ запуска приложения, так как пользователю нужно только запустить один EXE-файл, и все остальное будет сделано автоматически без отображения каких-либо диалоговых окон или запросов.

#### Запуск приложения, созданного с помощью packr

Если вы создали приложение с помощью packr, просто распакуйте архив `InterNumApp_Standalone.zip` и запустите `InterNumApp.exe`. Все необходимые файлы уже включены в архив, и вам не нужно устанавливать Java или распаковывать дополнительные файлы.

Структура директорий после распаковки архива:

```
InterNumApp_Standalone/
├── InterNumApp.exe
├── internum-app-1.0-SNAPSHOT.jar
├── InterNumApp.json
└── jre/
    ├── bin/
    ├── conf/
    ├── lib/
    └── ...
```

#### Запуск приложения, созданного с помощью Launch4j

Если вы создали приложение с помощью Launch4j, вам нужно убедиться, что папка `jre` находится в той же директории, что и EXE-файл. Структура директорий должна выглядеть следующим образом:

```
InterNumApp/
├── InterNumApp.exe
└── jre/
    └── windows/
        ├── bin/
        ├── conf/
        ├── lib/
        └── ...
```

Если вы распаковали архив `InterNumApp_with_JRE.zip`, то структура директорий уже будет правильной.

## Структура проекта

```
internum-app/
├── src/                           # Исходный код
│   └── main/java/com/internum/    # Пакет с классами
│       ├── AppLauncher.java       # Точка входа в приложение
│       ├── Cube3DApp.java         # Основной класс 3D приложения
│       └── ConsoleApp.java        # Альтернативная версия с Swing UI
│   └── module-info.java           # Описание модуля Java
├── pom.xml                        # Конфигурация Maven
├── internum-app.xml               # Конфигурация Launch4j
├── installer.nsi                  # Скрипт NSIS
└── README.md                      # Этот файл
```

## Как это работает

### 1. Maven Shade Plugin

Maven Shade Plugin создает "fat JAR" - JAR-файл, который содержит все необходимые зависимости и может быть запущен с помощью команды `java -jar`. Это самый простой способ распространения Java-приложения, но требует наличия установленной Java на компьютере пользователя.

### 2. Packr

Packr создает автономное приложение, которое включает минимизированную JRE. Это позволяет пользователям запускать приложение без установки Java. Packr поддерживает Windows, Linux и macOS.

### 3. Launch4j

Launch4j создает EXE-файл, который запускает JAR-файл с помощью указанной JRE. Это позволяет пользователям запускать приложение как обычное Windows-приложение.

### 4. NSIS

NSIS создает установщик, который распаковывает приложение во временную директорию, запускает его, а затем удаляет временные файлы. Это самый удобный способ распространения приложения для пользователей Windows.

## Архитектура 3D движка

### Основные компоненты

1. **Инициализация GLFW и OpenGL**
   ```java
   // Инициализация GLFW
   if (!glfwInit()) {
       throw new IllegalStateException("Unable to initialize GLFW");
   }

   // Настройка GLFW
   glfwDefaultWindowHints();
   glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
   glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
   glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
   glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
   glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
   glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

   // Создание окна
   window = glfwCreateWindow(800, 600, "InterNum 3D Cube", NULL, NULL);
   ```

2. **Создание 3D объектов**
   ```java
   // Создание VAO (Vertex Array Object)
   vaoId = glGenVertexArrays();
   glBindVertexArray(vaoId);

   // Создание VBO (Vertex Buffer Object) для вершин
   vboId = glGenBuffers();
   glBindBuffer(GL_ARRAY_BUFFER, vboId);

   // Создание буфера для вершин
   FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
   verticesBuffer.put(vertices);
   verticesBuffer.flip();

   glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
   ```

3. **Шейдеры**
   ```java
   // Вершинный шейдер
   String vertexShaderSource =
       "#version 330 core\n" +
       "layout (location = 0) in vec3 position;\n" +
       "layout (location = 1) in vec3 color;\n" +
       "out vec3 fragColor;\n" +
       "uniform mat4 model;\n" +
       "uniform mat4 view;\n" +
       "uniform mat4 projection;\n" +
       "void main() {\n" +
       "    gl_Position = projection * view * model * vec4(position, 1.0);\n" +
       "    fragColor = color;\n" +
       "}\n";

   // Фрагментный шейдер
   String fragmentShaderSource =
       "#version 330 core\n" +
       "in vec3 fragColor;\n" +
       "out vec4 outColor;\n" +
       "void main() {\n" +
       "    outColor = vec4(fragColor, 1.0);\n" +
       "}\n";
   ```

4. **Цикл отрисовки**
   ```java
   // Цикл отрисовки
   while (!glfwWindowShouldClose(window)) {
       // Очистка буферов
       glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

       // Обновление угла вращения
       angle += 0.01f;

       // Создание модельной матрицы (вращение куба)
       float[] model = createRotationMatrix(angle, 0.5f, 1.0f, 0.0f);

       // Установка модельной матрицы
       glUniformMatrix4fv(modelLoc, false, model);

       // Привязка VAO
       glBindVertexArray(vaoId);

       // Отрисовка куба
       glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);

       // Отвязка VAO
       glBindVertexArray(0);

       // Обмен буферов
       glfwSwapBuffers(window);

       // Обработка событий
       glfwPollEvents();
   }
   ```

### Матрицы преобразования

1. **Модельная матрица** - преобразует координаты объекта из локальной системы координат в мировую.
2. **Матрица вида** - преобразует координаты из мировой системы координат в систему координат камеры.
3. **Матрица проекции** - преобразует координаты из системы координат камеры в нормализованные координаты устройства.

```java
// Создание матрицы проекции (перспектива)
float aspectRatio = 800.0f / 600.0f;
float[] projection = createPerspectiveMatrix(45.0f, aspectRatio, 0.1f, 100.0f);

// Создание матрицы вида (камера)
float[] view = createLookAtMatrix(
    0.0f, 0.0f, 3.0f,  // Позиция камеры
    0.0f, 0.0f, 0.0f,  // Точка, на которую смотрит камера
    0.0f, 1.0f, 0.0f   // Вектор "вверх"
);
```

## Логирование

Приложение создает файл `internum_3d_log.txt` в директории запуска. Этот файл содержит информацию о запуске приложения и возможных ошибках.

```java
// Пример логирования
private static void log(String message) {
    if (logStream != null) {
        logStream.println("[" + new Date() + "] " + message);
        logStream.flush();
    }
}
```

## Управление

- **Escape** - закрыть приложение
- Куб автоматически вращается вокруг диагональной оси

## Расширение проекта для создания игры

### 1. Добавление игровых объектов

Для добавления новых 3D объектов в сцену, создайте класс для каждого типа объекта:

```java
public class GameObject {
    private float[] vertices;
    private int[] indices;
    private int vaoId;
    private int vboId;
    private int vboiId;
    private float[] position;
    private float[] rotation;
    private float[] scale;

    // Конструктор, методы для инициализации и отрисовки
}
```

### 2. Создание игрового цикла

```java
// Игровой цикл
while (!glfwWindowShouldClose(window)) {
    // Вычисление времени между кадрами
    float currentTime = (float) glfwGetTime();
    float deltaTime = currentTime - lastTime;
    lastTime = currentTime;

    // Обработка ввода
    processInput(window, deltaTime);

    // Обновление игровой логики
    updateGame(deltaTime);

    // Отрисовка
    render();

    // Обмен буферов
    glfwSwapBuffers(window);

    // Обработка событий
    glfwPollEvents();
}
```

### 3. Обработка ввода

```java
private void processInput(long window, float deltaTime) {
    // Проверка нажатия клавиш
    if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
        // Движение вперед
        cameraPosition[2] -= cameraSpeed * deltaTime;
    }
    if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
        // Движение назад
        cameraPosition[2] += cameraSpeed * deltaTime;
    }
    // и т.д.
}
```

### 4. Добавление физики

Для простой физики можно использовать библиотеку JBullet или реализовать простую систему обнаружения столкновений:

```java
private boolean checkCollision(GameObject obj1, GameObject obj2) {
    // Простая проверка столкновения по ограничивающим сферам
    float distance = distance(obj1.getPosition(), obj2.getPosition());
    return distance < (obj1.getRadius() + obj2.getRadius());
}
```

### 5. Добавление звука

LWJGL включает OpenAL для работы со звуком:

```java
// Инициализация OpenAL
ALContext context = ALContext.create();
context.makeCurrent();

// Загрузка звукового файла
int buffer = alGenBuffers();
WaveData waveFile = WaveData.create("sound.wav");
alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
waveFile.dispose();

// Создание источника звука
int source = alGenSources();
alSourcei(source, AL_BUFFER, buffer);

// Воспроизведение звука
alSourcePlay(source);
```

### 6. Добавление пользовательского интерфейса

Для создания пользовательского интерфейса можно использовать библиотеку NanoVG или создать простой интерфейс с помощью OpenGL:

```java
private void renderUI() {
    // Отрисовка текста
    textRenderer.renderText("Score: " + score, 10, 10, 1.0f);

    // Отрисовка полосы здоровья
    renderHealthBar(10, 30, health / maxHealth);
}
```

## Технические детали

Приложение использует:
- **LWJGL (Lightweight Java Game Library)** - Java-библиотека для доступа к нативным API, таким как OpenGL, OpenAL и OpenCL.
- **OpenGL** - кроссплатформенный API для отрисовки 2D и 3D графики.
- **GLFW** - библиотека для создания окон, контекстов OpenGL и обработки ввода.
- **Шейдеры** - программы, выполняемые на GPU, которые определяют, как отрисовываются вершины и пиксели.
- **Maven** - инструмент для управления зависимостями и сборки проекта.
- **Launch4j** - инструмент для создания EXE-файлов из JAR-файлов.

## Оптимизация производительности

### 1. Использование VAO и VBO

Vertex Array Objects (VAO) и Vertex Buffer Objects (VBO) позволяют эффективно передавать данные о вершинах на GPU:

```java
// Создание VAO
vaoId = glGenVertexArrays();
glBindVertexArray(vaoId);

// Создание VBO
vboId = glGenBuffers();
glBindBuffer(GL_ARRAY_BUFFER, vboId);
glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
```

### 2. Использование индексов

Индексы позволяют повторно использовать вершины, что уменьшает объем данных, передаваемых на GPU:

```java
// Создание VBO для индексов
vboiId = glGenBuffers();
glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
```

### 3. Фрустум каллинг

Отрисовка только тех объектов, которые находятся в поле зрения камеры:

```java
private boolean isInFrustum(GameObject obj) {
    // Проверка, находится ли объект в поле зрения камеры
    // ...
    return true; // или false
}
```

### 4. Уровни детализации (LOD)

Использование разных моделей с разным уровнем детализации в зависимости от расстояния до камеры:

```java
private void updateLOD(GameObject obj) {
    float distance = distance(cameraPosition, obj.getPosition());
    if (distance < 10.0f) {
        obj.setModel(highDetailModel);
    } else if (distance < 50.0f) {
        obj.setModel(mediumDetailModel);
    } else {
        obj.setModel(lowDetailModel);
    }
}
```

## Ресурсы для изучения

- [LWJGL Wiki](https://github.com/LWJGL/lwjgl3-wiki/wiki)
- [OpenGL Tutorial](https://learnopengl.com/)
- [GLFW Documentation](https://www.glfw.org/docs/latest/)
- [Java Game Development with LWJGL](https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/)
- [Maven Documentation](https://maven.apache.org/guides/index.html)

## Лицензия

Этот проект распространяется под лицензией MIT. См. файл LICENSE для получения дополнительной информации.
