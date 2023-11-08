package com.zaytsev.app.fxapplication.Controllers;

import com.zaytsev.app.fxapplication.data.UserDto;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CompareController implements Initializable {
    @FXML
    private ScrollPane codeScrollPane; // Ссылка на ScrollPane в FXML
    @FXML
    private VBox codeVBoxContainer; // Ссылка на VBox внутри ScrollPane в FXML
    private List<UserDto> users; // Список пользователей с их кодами
    public void generateAllStatistic(List<UserDto> users){
        // Итерация по списку пользователей и создание интерфейса для каждого
        for (UserDto user : users) {
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
            Label userNameLabel = new Label("User: " + user.getName()); // Предполагаем, что есть метод getName

            // Добавляем Label и SplitPane в контейнер VBox
            VBox containerVBox = new VBox(userNameLabel, splitPane);
            containerVBox.setSpacing(10); // Устанавливаем интервал между элементами

            // Добавляем контейнер в codeVBoxContainer
            codeVBoxContainer.getChildren().add(containerVBox);
        }

        // Устанавливаем, чтобы ScrollPane подгонялся по ширине содержимого
        codeVBoxContainer.setFillWidth(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    // Геттеры и сеттеры
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
    public List<UserDto> getUsers() {
        return users;
    }
    public void setUsers(List<UserDto> users) {
        this.users = users; // Установка списка пользователей
    }
}
