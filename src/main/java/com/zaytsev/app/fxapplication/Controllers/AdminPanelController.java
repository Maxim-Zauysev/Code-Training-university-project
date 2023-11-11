package com.zaytsev.app.fxapplication.Controllers;


import com.zaytsev.app.fxapplication.data.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zaytsev.app.fxapplication.Controllers.MainFormController.computeHighlighting;

public class AdminPanelController implements Initializable {

    @FXML
    private Button addButton;
    @FXML
    private ComboBox<String> complexityComboBox;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private TextField languageField;
    @FXML
    private CodeArea codeWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        codeWindow.getStylesheets().add(getClass().getResource("/main.css").toExternalForm());
        codeWindow.textProperty().addListener((obs, oldText, newText) -> {
            updateHighlighting();
        });

        addButton.setOnAction(this::handleAddButtonClick);

    }

    @FXML
    public void handleAddButtonClick(ActionEvent event) {
        String selectedLanguage = languageComboBox.getSelectionModel().getSelectedItem();
        String languageFieldText = languageField.getText();
        String codeText = codeWindow.getText();
        int complexityId = complexityComboBox.getSelectionModel().getSelectedIndex() + 1; // предполагается, что id начинаются с 1

        DatabaseManager dbManager = new DatabaseManager();

        try {
            if (selectedLanguage == null && !languageFieldText.isEmpty()) {
                // Добавляем новый язык и получаем его id
                int newLanguageId = dbManager.addLanguage(languageFieldText);
                dbManager.addCode(newLanguageId, complexityId, codeText);
            } else if (selectedLanguage != null) {
                int languageId = dbManager.getLanguageIdByName(selectedLanguage);
                dbManager.addCode(languageId, complexityId, codeText);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void updateHighlighting() {
        codeWindow.setStyleSpans(0, computeHighlighting(codeWindow.getText()));
    }

    public Button getAddButton() {
        return addButton;
    }

    public void setAddButton(Button addButton) {
        this.addButton = addButton;
    }

    public ComboBox<String> getComplexityComboBox() {
        return complexityComboBox;
    }

    public void setComplexityComboBox(ComboBox<String> complexityComboBox) {
        this.complexityComboBox = complexityComboBox;
    }

    public ComboBox<String> getLanguageComboBox() {
        return languageComboBox;
    }

    public void setLanguageComboBox(ComboBox<String> languageComboBox) {
        this.languageComboBox = languageComboBox;
    }

    public TextField getLanguageField() {
        return languageField;
    }

    public void setLanguageField(TextField languageField) {
        this.languageField = languageField;
    }

    public CodeArea getCodeWindow() {
        return codeWindow;
    }

    public void setCodeWindow(CodeArea codeWindow) {
        this.codeWindow = codeWindow;
    }
}