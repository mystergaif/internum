package com.internum;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Простое приложение с графическим интерфейсом Swing
 */
public class ConsoleApp {
    private static PrintStream logStream;
    private static JTextArea textArea;

    static {
        try {
            // Создаем файл для логирования
            File logFile = new File("internum_app_log.txt");
            logStream = new PrintStream(new FileOutputStream(logFile, true));
            logStream.println("=== Application started at " + new Date() + " ===");
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

            // Устанавливаем системный Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Запускаем GUI в потоке обработки событий
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    createAndShowGUI();
                }
            });

        } catch (Exception e) {
            log("Error in main method: " + e.getMessage());
            e.printStackTrace(logStream);
        }
    }

    /**
     * Создает и отображает графический интерфейс
     */
    private static void createAndShowGUI() {
        try {
            // Создаем главное окно
            JFrame frame = new JFrame("InterNum App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);
            frame.setLocationRelativeTo(null);

            // Создаем текстовую область для вывода
            textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            textArea.setBackground(Color.BLACK);
            textArea.setForeground(Color.WHITE);

            // Добавляем прокрутку
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane, BorderLayout.CENTER);

            // Создаем панель с кнопками
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Кнопка "Показать информацию"
            JButton infoButton = new JButton("Показать информацию");
            infoButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showInfo();
                }
            });
            buttonPanel.add(infoButton);

            // Кнопка "Нарисовать куб"
            JButton cubeButton = new JButton("Нарисовать 3D куб");
            cubeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    drawCube();
                }
            });
            buttonPanel.add(cubeButton);

            // Кнопка "Выход"
            JButton exitButton = new JButton("Выход");
            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log("Application exiting by user request");
                    if (logStream != null) {
                        logStream.println("=== Application stopped at " + new Date() + " ===");
                        logStream.println();
                        logStream.close();
                    }
                    System.exit(0);
                }
            });
            buttonPanel.add(exitButton);

            // Добавляем панель с кнопками внизу окна
            frame.add(buttonPanel, BorderLayout.SOUTH);

            // Отображаем окно
            frame.setVisible(true);

            // Показываем приветственное сообщение
            appendText("╔════════════════════════════════════════════╗\n");
            appendText("║            InterNum App                    ║\n");
            appendText("╠════════════════════════════════════════════╣\n");
            appendText("║ Добро пожаловать в InterNum App!           ║\n");
            appendText("║ Используйте кнопки ниже для навигации.     ║\n");
            appendText("╚════════════════════════════════════════════╝\n\n");

            log("GUI created successfully");
        } catch (Exception e) {
            log("Error in createAndShowGUI: " + e.getMessage());
            e.printStackTrace(logStream);
        }
    }

    /**
     * Добавляет текст в текстовую область
     */
    private static void appendText(String text) {
        if (textArea != null) {
            textArea.append(text);
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    /**
     * Очищает текстовую область
     */
    private static void clearText() {
        if (textArea != null) {
            textArea.setText("");
        }
    }

    /**
     * Показывает информацию о приложении
     */
    private static void showInfo() {
        log("Showing application info");
        clearText();
        appendText("Информация о приложении:\n");
        appendText("------------------------\n");
        appendText("Название: InterNum App\n");
        appendText("Версия: 1.0\n");
        appendText("Описание: Приложение с графическим интерфейсом Swing\n");
        appendText("Дата: " + new Date() + "\n");
        appendText("------------------------\n\n");
    }

    /**
     * Рисует ASCII-куб
     */
    private static void drawCube() {
        log("Drawing 3D cube");
        clearText();
        appendText("3D Куб:\n\n");
        appendText("    +--------+\n");
        appendText("   /|       /|\n");
        appendText("  / |      / |\n");
        appendText(" +--------+  |\n");
        appendText(" |  |     |  |\n");
        appendText(" |  +-----|--+\n");
        appendText(" | /      | /\n");
        appendText(" |/       |/\n");
        appendText(" +--------+\n\n");
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
