package com.internum;

import org.lwjgl.Version;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Date;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Приложение с 3D кубом, использующее LWJGL (OpenGL)
 */
public class Cube3DApp {

    private static PrintStream logStream;

    // Идентификатор окна
    private long window;

    // Идентификаторы OpenGL объектов
    private int vaoId;
    private int vboId;
    private int vboiId;
    private int indicesCount;

    // Угол вращения куба
    private float angle = 0.0f;

    static {
        try {
            // Создаем файл для логирования
            File logFile = new File("internum_3d_log.txt");
            logStream = new PrintStream(new FileOutputStream(logFile, true));
            logStream.println("=== 3D Application started at " + new Date() + " ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Главный метод приложения
     */
    public static void main(String[] args) {
        try {
            log("Application started");
            log("LWJGL Version: " + Version.getVersion());

            new Cube3DApp().run();

            log("Application finished successfully");
        } catch (Exception e) {
            log("Error in main method: " + e.getMessage());
            e.printStackTrace(logStream);
        } finally {
            if (logStream != null) {
                logStream.println("=== Application stopped at " + new Date() + " ===");
                logStream.println();
                logStream.close();
            }
        }
    }

    /**
     * Запускает приложение
     */
    public void run() {
        log("Initializing application");

        init();
        loop();

        // Освобождаем ресурсы
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Завершаем GLFW
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        log("Application terminated");
    }

    /**
     * Инициализирует GLFW и OpenGL
     */
    private void init() {
        log("Initializing GLFW");

        // Настраиваем обработчик ошибок GLFW
        GLFWErrorCallback.createPrint(System.err).set();

        // Инициализируем GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Настраиваем GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Создаем окно
        window = glfwCreateWindow(800, 600, "InterNum 3D Cube", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Настраиваем обработчики событий
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        // Получаем разрешение экрана
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            // Получаем разрешение основного монитора
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Центрируем окно
            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        // Делаем контекст OpenGL текущим
        glfwMakeContextCurrent(window);

        // Включаем вертикальную синхронизацию
        glfwSwapInterval(1);

        // Показываем окно
        glfwShowWindow(window);

        // Инициализируем OpenGL
        GL.createCapabilities();

        log("OpenGL Version: " + glGetString(GL_VERSION));

        // Настраиваем OpenGL
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        // Создаем куб
        createCube();
    }

    /**
     * Создает вершины и индексы куба
     */
    private void createCube() {
        log("Creating cube");

        // Вершины куба (x, y, z, r, g, b)
        float[] vertices = {
            // Передняя грань (красная)
            -0.5f, -0.5f,  0.5f, 1.0f, 0.0f, 0.0f,
             0.5f, -0.5f,  0.5f, 1.0f, 0.0f, 0.0f,
             0.5f,  0.5f,  0.5f, 1.0f, 0.0f, 0.0f,
            -0.5f,  0.5f,  0.5f, 1.0f, 0.0f, 0.0f,

            // Задняя грань (зеленая)
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
             0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
             0.5f,  0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f,  0.5f, -0.5f, 0.0f, 1.0f, 0.0f,

            // Верхняя грань (синяя)
            -0.5f,  0.5f,  0.5f, 0.0f, 0.0f, 1.0f,
             0.5f,  0.5f,  0.5f, 0.0f, 0.0f, 1.0f,
             0.5f,  0.5f, -0.5f, 0.0f, 0.0f, 1.0f,
            -0.5f,  0.5f, -0.5f, 0.0f, 0.0f, 1.0f,

            // Нижняя грань (желтая)
            -0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 0.0f,
             0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 0.0f,
             0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 0.0f,

            // Правая грань (пурпурная)
             0.5f, -0.5f,  0.5f, 1.0f, 0.0f, 1.0f,
             0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 1.0f,
             0.5f,  0.5f, -0.5f, 1.0f, 0.0f, 1.0f,
             0.5f,  0.5f,  0.5f, 1.0f, 0.0f, 1.0f,

            // Левая грань (голубая)
            -0.5f, -0.5f,  0.5f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 1.0f,
            -0.5f,  0.5f, -0.5f, 0.0f, 1.0f, 1.0f,
            -0.5f,  0.5f,  0.5f, 0.0f, 1.0f, 1.0f
        };

        // Индексы вершин для отрисовки граней
        int[] indices = {
            // Передняя грань
            0, 1, 2,
            2, 3, 0,

            // Задняя грань
            4, 5, 6,
            6, 7, 4,

            // Верхняя грань
            8, 9, 10,
            10, 11, 8,

            // Нижняя грань
            12, 13, 14,
            14, 15, 12,

            // Правая грань
            16, 17, 18,
            18, 19, 16,

            // Левая грань
            20, 21, 22,
            22, 23, 20
        };

        indicesCount = indices.length;

        // Создаем VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Создаем VBO для вершин
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        // Создаем буфер для вершин
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        // Позиция вершин
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * 4, 0);
        glEnableVertexAttribArray(0);

        // Цвет вершин
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * 4, 3 * 4);
        glEnableVertexAttribArray(1);

        // Создаем VBO для индексов
        vboiId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);

        // Создаем буфер для индексов
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        // Отвязываем VAO
        glBindVertexArray(0);

        // Создаем шейдеры
        createShaders();
    }

    /**
     * Создает и компилирует шейдеры
     */
    private void createShaders() {
        log("Creating shaders");

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

        // Компилируем вершинный шейдер
        int vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderId, vertexShaderSource);
        glCompileShader(vertexShaderId);

        // Проверяем ошибки компиляции
        if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(vertexShaderId);
            throw new RuntimeException("Failed to compile vertex shader: " + log);
        }

        // Компилируем фрагментный шейдер
        int fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderId, fragmentShaderSource);
        glCompileShader(fragmentShaderId);

        // Проверяем ошибки компиляции
        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(fragmentShaderId);
            throw new RuntimeException("Failed to compile fragment shader: " + log);
        }

        // Создаем программу
        int programId = glCreateProgram();
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);

        // Проверяем ошибки линковки
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(programId);
            throw new RuntimeException("Failed to link shader program: " + log);
        }

        // Удаляем шейдеры, так как они уже связаны с программой
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        // Используем программу
        glUseProgram(programId);

        // Получаем местоположение uniform-переменных
        int modelLoc = glGetUniformLocation(programId, "model");
        int viewLoc = glGetUniformLocation(programId, "view");
        int projectionLoc = glGetUniformLocation(programId, "projection");

        // Создаем матрицу проекции (перспектива)
        float aspectRatio = 800.0f / 600.0f;
        float[] projection = createPerspectiveMatrix(45.0f, aspectRatio, 0.1f, 100.0f);

        // Создаем матрицу вида (камера)
        float[] view = createLookAtMatrix(
            0.0f, 0.0f, 3.0f,  // Позиция камеры
            0.0f, 0.0f, 0.0f,  // Точка, на которую смотрит камера
            0.0f, 1.0f, 0.0f   // Вектор "вверх"
        );

        // Устанавливаем значения uniform-переменных
        glUniformMatrix4fv(viewLoc, false, view);
        glUniformMatrix4fv(projectionLoc, false, projection);

        // Сохраняем местоположение модельной матрицы для использования в цикле отрисовки
        this.modelLoc = modelLoc;
    }

    // Местоположение uniform-переменной модельной матрицы
    private int modelLoc;

    /**
     * Основной цикл отрисовки
     */
    private void loop() {
        log("Starting render loop");

        // Цикл отрисовки, пока окно не закроется
        while (!glfwWindowShouldClose(window)) {
            // Очищаем буферы
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Обновляем угол вращения
            angle += 0.01f;

            // Создаем модельную матрицу (вращение куба)
            float[] model = createRotationMatrix(angle, 0.5f, 1.0f, 0.0f);

            // Устанавливаем модельную матрицу
            glUniformMatrix4fv(modelLoc, false, model);

            // Привязываем VAO
            glBindVertexArray(vaoId);

            // Отрисовываем куб
            glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);

            // Отвязываем VAO
            glBindVertexArray(0);

            // Обмениваем буферы
            glfwSwapBuffers(window);

            // Обрабатываем события
            glfwPollEvents();
        }
    }

    /**
     * Создает матрицу перспективной проекции
     */
    private float[] createPerspectiveMatrix(float fov, float aspectRatio, float near, float far) {
        float[] matrix = new float[16];

        float tanHalfFov = (float) Math.tan(Math.toRadians(fov / 2));
        float range = near - far;

        matrix[0] = 1.0f / (aspectRatio * tanHalfFov);
        matrix[5] = 1.0f / tanHalfFov;
        matrix[10] = (far + near) / range;
        matrix[11] = -1.0f;
        matrix[14] = (2 * far * near) / range;

        return matrix;
    }

    /**
     * Создает матрицу вида (камеры)
     */
    private float[] createLookAtMatrix(float eyeX, float eyeY, float eyeZ,
                                      float centerX, float centerY, float centerZ,
                                      float upX, float upY, float upZ) {
        float[] matrix = new float[16];

        // Вычисляем векторы для системы координат камеры
        float[] forward = normalize(
            centerX - eyeX,
            centerY - eyeY,
            centerZ - eyeZ
        );

        float[] up = { upX, upY, upZ };

        float[] right = cross(forward, up);
        up = cross(right, forward);

        // Нормализуем векторы
        right = normalize(right[0], right[1], right[2]);
        up = normalize(up[0], up[1], up[2]);

        // Инвертируем направление вектора forward для матрицы вида
        forward[0] = -forward[0];
        forward[1] = -forward[1];
        forward[2] = -forward[2];

        // Заполняем матрицу
        matrix[0] = right[0];
        matrix[1] = up[0];
        matrix[2] = forward[0];
        matrix[3] = 0.0f;

        matrix[4] = right[1];
        matrix[5] = up[1];
        matrix[6] = forward[1];
        matrix[7] = 0.0f;

        matrix[8] = right[2];
        matrix[9] = up[2];
        matrix[10] = forward[2];
        matrix[11] = 0.0f;

        matrix[12] = -dot(right, eyeX, eyeY, eyeZ);
        matrix[13] = -dot(up, eyeX, eyeY, eyeZ);
        matrix[14] = -dot(forward, eyeX, eyeY, eyeZ);
        matrix[15] = 1.0f;

        return matrix;
    }

    /**
     * Создает матрицу вращения
     */
    private float[] createRotationMatrix(float angle, float x, float y, float z) {
        float[] matrix = new float[16];

        // Нормализуем ось вращения
        float[] axis = normalize(x, y, z);
        x = axis[0];
        y = axis[1];
        z = axis[2];

        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        float t = 1.0f - c;

        matrix[0] = t * x * x + c;
        matrix[1] = t * x * y + s * z;
        matrix[2] = t * x * z - s * y;
        matrix[3] = 0.0f;

        matrix[4] = t * x * y - s * z;
        matrix[5] = t * y * y + c;
        matrix[6] = t * y * z + s * x;
        matrix[7] = 0.0f;

        matrix[8] = t * x * z + s * y;
        matrix[9] = t * y * z - s * x;
        matrix[10] = t * z * z + c;
        matrix[11] = 0.0f;

        matrix[12] = 0.0f;
        matrix[13] = 0.0f;
        matrix[14] = 0.0f;
        matrix[15] = 1.0f;

        return matrix;
    }

    /**
     * Нормализует вектор
     */
    private float[] normalize(float x, float y, float z) {
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        return new float[] { x / length, y / length, z / length };
    }

    /**
     * Вычисляет векторное произведение
     */
    private float[] cross(float[] a, float[] b) {
        return new float[] {
            a[1] * b[2] - a[2] * b[1],
            a[2] * b[0] - a[0] * b[2],
            a[0] * b[1] - a[1] * b[0]
        };
    }

    /**
     * Вычисляет скалярное произведение
     */
    private float dot(float[] v, float x, float y, float z) {
        return v[0] * x + v[1] * y + v[2] * z;
    }

    /**
     * Записывает сообщение в лог
     */
    private static void log(String message) {
        if (logStream != null) {
            logStream.println("[" + new Date() + "] " + message);
            logStream.flush();
        }
    }
}
