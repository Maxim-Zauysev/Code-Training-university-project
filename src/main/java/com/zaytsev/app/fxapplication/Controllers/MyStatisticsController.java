package com.zaytsev.app.fxapplication.Controllers;

import com.zaytsev.app.fxapplication.data.DatabaseManager;
import com.zaytsev.app.fxapplication.data.UserDto;
import com.zaytsev.app.fxapplication.data.UserStatisticsDto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyStatisticsController {

    @FXML
    private ScrollPane codeScrollPane;

    @FXML
    private VBox codeVBoxContainer;
    private List<UserStatisticsDto> userStatistics; // Список пользователей с их кодами
    private final DatabaseManager databaseManager = new DatabaseManager();
    public void generateAllStatistic(List<UserStatisticsDto> users){
        // Итерация по списку пользователей и создание интерфейса для каждого
        for (UserStatisticsDto user : users) {
            // Создаем TextArea для кода пользователя
            TextArea userCodeTextArea = new TextArea(user.getUserCode());
            userCodeTextArea.setPrefHeight(300);
            userCodeTextArea.setEditable(false); // Запретить редактирование

            // Создаем TextArea для сгенерированного кода
            TextArea generatedCodeTextArea = new TextArea(user.getGeneratedCode());
            generatedCodeTextArea.setPrefHeight(300);
            generatedCodeTextArea.setEditable(false); // Запретить редактирование

            // Создаем SplitPane и добавляем в него TextArea для кодов
            SplitPane splitPane = new SplitPane(userCodeTextArea, generatedCodeTextArea);
            splitPane.setDividerPositions(0.5); // Устанавливаем разделитель пополам

            // Создаем Label для отображения имени пользователя или другой информации


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(formatter); // Преобразование текущего времени в строку с использованием форматтера

            Label statistics = new Label("Date of testing: " + formattedDateTime +
                                            "\ncount words: " + user.getCountWords() +
                                            "\nlead time: " + user.getLeadTime() + " c" +
                                            "\nmatch percentage: " + user.getMatchPercentage() + "%" +
                                            "\ncomplexity: " + user.getCodeComplexity());

            // Добавляем Label и SplitPane в контейнер VBox
            VBox containerVBox = new VBox(statistics, splitPane);
            containerVBox.setSpacing(10); // Устанавливаем интервал между элементами

            // Добавляем контейнер в codeVBoxContainer
            codeVBoxContainer.getChildren().add(containerVBox);
        }

        // Устанавливаем, чтобы ScrollPane подгонялся по ширине содержимого
        codeVBoxContainer.setFillWidth(true);
    }

    public ScrollPane getCodeScrollPane() {
        return codeScrollPane;
    }

    public void setCodeScrollPane(ScrollPane codeScrollPane) {
        this.codeScrollPane = codeScrollPane;
    }

    public VBox getCodeVBoxContainer() {
        return codeVBoxContainer;
    }

    public void setCodeVBoxContainer(VBox codeVBoxContainer) {
        this.codeVBoxContainer = codeVBoxContainer;
    }

    public List<UserStatisticsDto> getUserStatistics() {
        return userStatistics;
    }

    public void setUserStatistics(List<UserStatisticsDto> userStatistics) {
        this.userStatistics = userStatistics;
    }
}
