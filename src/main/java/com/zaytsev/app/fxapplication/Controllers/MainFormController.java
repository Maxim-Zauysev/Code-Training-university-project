package com.zaytsev.app.fxapplication.Controllers;

import com.zaytsev.app.fxapplication.data.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Загрузка языков из базы данных и добавление их в ComboBox
        List<String> languages = databaseManager.getLanguages();
        comboBox.getItems().addAll(languages);

        codeWindow.setEditable(false);
        codeWindow.setWrapText(true);

        generateButton.setOnAction(event -> generateCode());
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

