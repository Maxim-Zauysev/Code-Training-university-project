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
    }
}
