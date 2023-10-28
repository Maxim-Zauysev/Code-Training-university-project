package com.zaytsev.app.fxapplication.Controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController {

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

    @FXML
    void initialize() {
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'loginForm.fxml'.";
        assert password != null : "fx:id=\"password\" was not injected: check your FXML file 'loginForm.fxml'.";
        assert singUpButton != null : "fx:id=\"singUpButton\" was not injected: check your FXML file 'loginForm.fxml'.";
        assert userName != null : "fx:id=\"userName\" was not injected: check your FXML file 'loginForm.fxml'.";

    }

}
