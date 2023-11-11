package com.zaytsev.app.fxapplication.Controllers;

import com.zaytsev.app.fxapplication.CodeApplication;
import com.zaytsev.app.fxapplication.data.DatabaseManager;
import com.zaytsev.app.fxapplication.data.UserDto;
import com.zaytsev.app.fxapplication.exceptions.AccessDeniedException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFormController implements Initializable {

    @FXML
    private CodeArea codeWindow;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private ComboBox<String> complexityComboBox;
    @FXML
    private Button generateButton;
    @FXML
    private Button startButton;
    @FXML
    private Button myStatisticsButton;
    @FXML
    private Button adminPanelButton;
    @FXML
    private CodeArea userWindow;
    private final DatabaseManager databaseManager = new DatabaseManager();
    @FXML
    private Label timerLabel;
    private Timeline timeline;
    private Duration duration;
    private boolean codeGenerated = false;
    private Integer userId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        codeWindow.getStylesheets().add(getClass().getResource("/main.css").toExternalForm());
        userWindow.getStylesheets().add(getClass().getResource("/main.css").toExternalForm());

        userWindow.clear();
        codeWindow.clear();

        // Загрузка языков из базы данных и добавление их в ComboBox
        List<String> languages = databaseManager.getLanguages();
        comboBox.getItems().addAll(languages);

        List<String> complexityLevels = databaseManager.getComplexityLevels();
        complexityComboBox.getItems().addAll(complexityLevels);

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

        generateButton.setDisable(false);

        generateButton.setOnAction(event ->{
            if (comboBox.getValue() != null) {
                userWindow.clear();
                generateCode();
                codeGenerated = true;
            }
        });

        adminPanelButton.setOnAction(event -> {
            try {
                if (databaseManager.getUserRoleById(userId).equals("admin")) {
                    try {
                        Parent root;
                        FXMLLoader loader = new FXMLLoader(CodeApplication.class.getResource("adminPanel.fxml"));
                        root = loader.load();
                        AdminPanelController adminPanelController = loader.getController();
                        // Загрузка языков из базы данных и добавление их в ComboBox
                        adminPanelController.getLanguageComboBox().getItems().addAll(databaseManager.getLanguages());
                        adminPanelController.getComplexityComboBox().getItems().addAll(databaseManager.getComplexityLevels());

                        Stage stage = new Stage();
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/adminPanelTheme.css").toExternalForm());
                        stage.setScene(scene);
                        stage.show();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } else {
                    try {
                        throw new AccessDeniedException("Access denied: user is not an admin.");
                    } catch (AccessDeniedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        myStatisticsButton.setOnAction(event -> {
            try {
                Parent root;
                FXMLLoader loader = new FXMLLoader(CodeApplication.class.getResource("myStatistics.fxml"));
                root = loader.load();
                MyStatisticsController statisticController = loader.getController();
                statisticController.setUserStatistics(databaseManager.getUserStatistics(userId));
                statisticController.generateAllStatistic(statisticController.getUserStatistics());
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/myStatisticsDarkTheme.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            }catch (Exception exception){
                exception.printStackTrace();
            }
        });

        startButton.setOnAction(event -> {
            if (codeGenerated) {
                if (timeline.getStatus() == Animation.Status.RUNNING) {
                    userWindow.setEditable(false);
                    codeGenerated = false;
                    timeline.pause();
                    startButton.setText("start");

                    float timeInSeconds = (float) duration.toSeconds();

                    String userWindowText = userWindow.getText();
                    String[] words = userWindowText.split("\\s+"); // Разделите текст на слова
                    int wordCount = words.length;

                    String userCode = userWindow.getText();
                    String referenceCode = codeWindow.getText();
                    compareCode(userCode, referenceCode);
                    generateButton.setDisable(false);

                    try {
                        Parent root;
                        FXMLLoader loader = new FXMLLoader(CodeApplication.class.getResource("statisticForm.fxml"));
                        root = loader.load();
                        StatisticController statisticController = loader.getController();
                        statisticController.setUserCode(userWindow.getText());
                        statisticController.setGeneratedCode(codeWindow.getText());
                        statisticController.setCountWords("Количество слов: " + wordCount);
                        statisticController.setLeadTime("Ваше время: " + timeInSeconds);
                        statisticController.setMatchPercentage(String.format("Совпадение: %.2f%%",  compareCode(userCode, referenceCode)));
                        statisticController.compareAndHighlight();
                        statisticController.setUserCodeArea(userWindow);
                        statisticController.setGeneratedCodeArea(codeWindow);
                        databaseManager.saveUserStatistics(userId, userWindow.getText(), codeWindow.getText(),
                                                            timeInSeconds, wordCount , (float) compareCode(userCode, referenceCode),  LocalDateTime.now() );
                        Stage stage = new Stage();
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/mainDarkTheme.css").toExternalForm());
                        stage.setScene(scene);
                        stage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    duration = Duration.ZERO;
                    codeGenerated = true;
                    userWindow.setEditable(true);
                    timeline.play();
                    startButton.setText("stop");
                    generateButton.setDisable(true);
                }
            }
        });

        userWindow.textProperty().addListener((obs, oldText, newText) -> {
            updateHighlighting();
        });
    }

    private void generateCode() {
        String selectedLanguage = comboBox.getValue();
        String selectedComplexity = complexityComboBox.getValue();

        if (selectedLanguage != null && selectedComplexity != null) {
            // Получение рандомного кода для выбранного языка и сложности и отображение его в codeWindow
            String randomCode = databaseManager.getRandomCode(selectedLanguage, selectedComplexity);
            codeWindow.replaceText(randomCode);
            generatedCode = randomCode; // Сохраните сгенерированный код

            // Вызов метода для обновления подсветки в codeWindow
            updateHighlighting();
        }
    }

    private double compareCode(String userCode, String referenceCode) {
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

        return similarity;
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

    private String generatedCode = ""; // Добавьте поле для хранения сгенерированного кода

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private void updateHighlighting() {
        userWindow.setStyleSpans(0, computeHighlighting(userWindow.getText()));
        codeWindow.setStyleSpans(0, computeHighlighting(generatedCode)); // Подсветите сгенерированный код
    }

    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "if", "goto", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

}

