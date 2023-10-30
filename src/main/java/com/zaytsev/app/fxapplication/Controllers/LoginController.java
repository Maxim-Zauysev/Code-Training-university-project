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

    private final DatabaseManager databaseManager = new DatabaseManager();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.setOnAction(this::onLoginButtonClick);
        singUpButton.setOnAction(this::onSingUpButtonClick);
    }
    @FXML
    void onLoginButtonClick(ActionEvent event) {
        String username = userName.getText();
        String password = this.password.getText();
        if (databaseManager.checkUser(username, password)) {
            // переход на MainForm
            try {
                Parent root;
                FXMLLoader loader = new FXMLLoader(CodeApplication.class.getResource("mainForm.fxml"));
                root = loader.load();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root));
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
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
