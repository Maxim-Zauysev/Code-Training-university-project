package com.zaytsev.app.fxapplication.Controllers;

import com.zaytsev.app.fxapplication.data.DatabaseManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MainFormController implements Initializable {

    @FXML
    private TextArea codeWindow;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private Button generateButton;
    @FXML
    private Button startButton;
    @FXML
    private TextArea userWindow;
    private final DatabaseManager databaseManager = new DatabaseManager();

    @FXML
    private Label timerLabel;
    private Timeline timeline;
    private Duration duration;
    private boolean codeGenerated = false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Загрузка языков из базы данных и добавление их в ComboBox
        List<String> languages = databaseManager.getLanguages();
        comboBox.getItems().addAll(languages);

        codeWindow.setEditable(false);
        codeWindow.setWrapText(true);

        userWindow.setEditable(false);
        codeWindow.setWrapText(true);

        timerLabel.setText("00:00:00");
        timeline = new Timeline(
                new KeyFrame(Duration.millis(100), event -> updateTimer())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        duration = Duration.ZERO;

        generateButton.setOnAction(event ->{
            if (comboBox.getValue() != null) {
                userWindow.setText("");
                generateCode();
                codeGenerated = true;
            }
        });

        startButton.setOnAction(event -> {
            if (codeGenerated) {
                if (timeline.getStatus() == Animation.Status.RUNNING) {
                    userWindow.setEditable(false);
                    codeGenerated = false;
                    timeline.pause();
                    startButton.setText("start");

                    double timeInSeconds = duration.toSeconds();

                    String userWindowText = userWindow.getText();
                    String[] words = userWindowText.split("\\s+"); // Разделите текст на слова
                    int wordCount = words.length;

                    String message = "Ваше время: " + timeInSeconds + " секунд, количество слов: " + wordCount;
                    System.out.println(message); // Выведите сообщение в консоль, вы можете использовать другой способ отображения

                    String userCode = userWindow.getText();
                    String referenceCode = codeWindow.getText();
                    compareCode(userCode, referenceCode);
                } else {
                    duration = Duration.ZERO;
                    codeGenerated = true;
                    userWindow.setEditable(true);
                    timeline.play();
                    startButton.setText("stop");
                }
            }
        });
    }
    private void compareCode(String userCode, String referenceCode) {
        // Простейший алгоритм Левенштейна
        int[][] dp = new int[userCode.length() + 1][referenceCode.length() + 1];
        for (int i = 0; i <= userCode.length(); i++) {
            for (int j = 0; j <= referenceCode.length(); j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                } else if (userCode.charAt(i - 1) == referenceCode.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        int maxMatch = dp[userCode.length()][referenceCode.length()];
        double similarity = (double) maxMatch / referenceCode.length() * 100.0;

        String message = String.format("Совпадение: %.2f%%", similarity);
        System.out.println(message); // Замените на свой способ вывода сообщения
    }

    private void updateTimer() {
        duration = duration.add(Duration.millis(100));
        String formattedTime = formatDuration(duration);
        timerLabel.setText(formattedTime);
    }

    private String formatDuration(Duration duration) {
        long millis = (long) duration.toMillis();
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    private void generateCode() {
        String selectedLanguage = comboBox.getValue();
        if (selectedLanguage != null) {
            // Получение рандомного кода для выбранного языка и отображение его в codeWindow
            String randomCode = databaseManager.getRandomCode(selectedLanguage);
            codeWindow.setText(randomCode);
        }
    }

}

