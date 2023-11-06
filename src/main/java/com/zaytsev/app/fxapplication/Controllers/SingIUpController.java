package com.zaytsev.app.fxapplication.Controllers;

import com.zaytsev.app.fxapplication.CodeApplication;
import com.zaytsev.app.fxapplication.data.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SingIUpController implements Initializable {

    @FXML
    private Button logInButton;

    @FXML
    private TextField password;

    @FXML
    private Button singUpButton;

    @FXML
    private TextField username;

    @FXML
    private ToggleButton themeSwitcher;
    private Scene scene; // Ссылка на сцену


    private final DatabaseManager databaseManager = new DatabaseManager();
    @FXML
    void onSingUpButtonClick(ActionEvent event) {
        String username = this.username.getText();
        String password = this.password.getText();
        if (databaseManager.registerUser(username, password)) {
            // переход на MainForm
            try {
                Parent root;
                FXMLLoader loader = new FXMLLoader(CodeApplication.class.getResource("mainForm.fxml"));
                root = loader.load();
                Stage stage = (Stage) singUpButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // вывод сообщения об ошибке
            System.out.println("Registration failed");
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        singUpButton.setOnAction(this::onSingUpButtonClick);
        logInButton.setOnAction(this::handleLoginButtonAction);
        scene = username.getScene();

        // Добавляем слушателя к переключателю тем
        themeSwitcher.selectedProperty().addListener((observable, oldValue, newValue) -> {
            switchTheme(newValue);
        });
    }

    // Метод для переключения темы
    private void switchTheme(boolean darkTheme) {
        // Удаляем предыдущую тему

        themeSwitcher.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Применяем темную тему
                singUpButton.getScene().getStylesheets().clear();
                singUpButton.getScene().getStylesheets().add(getClass().getResource("/darkSingUp.css").toExternalForm());
                themeSwitcher.setText("light");

            } else {
                // Применяем светлую тему
                singUpButton.getScene().getStylesheets().clear();
                singUpButton.getScene().getStylesheets().add(getClass().getResource("/lightSingUp.css").toExternalForm());
                themeSwitcher.setText("dark");
            }
        });

    }

    @FXML
    void handleLoginButtonAction(ActionEvent event) {
        try {
            Parent root;
            FXMLLoader loader = new FXMLLoader(CodeApplication.class.getResource("loginForm.fxml"));

            root = loader.load();
            LoginController loginController = loader.getController();

            Stage stage = (Stage) logInButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            // Обработайте исключение, как считаете нужным
        }
    }
}
