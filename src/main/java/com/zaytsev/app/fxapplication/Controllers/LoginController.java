package com.zaytsev.app.fxapplication.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import com.zaytsev.app.fxapplication.CodeApplication;
import com.zaytsev.app.fxapplication.data.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button loginButton;
    @FXML
    private TextField password;
    @FXML
    private Button singUpButton;
    @FXML
    private TextField userName;
    private Integer userId;
    @FXML
    private ToggleButton themeSwitcher;

    private final DatabaseManager databaseManager = new DatabaseManager();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.setOnAction(this::onLoginButtonClick);
        singUpButton.setOnAction(this::onSingUpButtonClick);
        themeSwitcher.setText("light");
//        // Получение ссылки на Scene или любой компонент, который уже добавлен на сцену
//        Scene scene = loginButton.getScene();
//        // Добавление файла CSS к текущей сцене
//        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());

        themeSwitcher.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Применяем темную тему
                userName.getScene().getStylesheets().clear();
                userName.getScene().getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
                themeSwitcher.setText("light");

            } else {
                // Применяем светлую тему
                userName.getScene().getStylesheets().clear();
                userName.getScene().getStylesheets().add(getClass().getResource("/light-theme.css").toExternalForm());
                themeSwitcher.setText("dark");
            }
        });

    }
    @FXML
    void onLoginButtonClick(ActionEvent event) {
        String username = userName.getText();
        String password = this.password.getText();
        if (databaseManager.checkUser(username, password)) {
            this.userId = databaseManager.getUserId(username);
            try {
                Parent root;
                FXMLLoader loader = new FXMLLoader(CodeApplication.class.getResource("mainForm.fxml"));
                root = loader.load();
                MainFormController mainFormController = loader.getController();
                mainFormController.setUserId(this.userId);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/mainDarkTheme.css").toExternalForm());

                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // вывод сообщения об ошибке
            System.out.println("Invalid username or password");
        }
    }
    @FXML
    void onSingUpButtonClick(ActionEvent event) {
        try {
            Parent root;
            FXMLLoader loader = new FXMLLoader(CodeApplication.class.getResource("singUpForm.fxml"));
            root = loader.load();
            SingIUpController singIUpController = loader.getController();
            Stage stage = (Stage) singUpButton.getScene().getWindow();
            Scene scene = new Scene(root);

            // Добавляем стиль к сцене
            scene.getStylesheets().add(getClass().getResource("/darkSingUp.css").toExternalForm());

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
