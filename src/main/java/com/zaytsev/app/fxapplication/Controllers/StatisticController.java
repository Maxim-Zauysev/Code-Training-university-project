package com.zaytsev.app.fxapplication.Controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class StatisticController {

    @FXML
    private Label countWords;

    @FXML
    private TextArea generatedCode;

    @FXML
    private Label leadTime;

    @FXML
    private Label matchPercentage;

    @FXML
    private TextArea userCode;

    public TextArea getGeneratedCode() {
        return generatedCode;
    }

    public void setGeneratedCode(String generatedCode) {
        this.generatedCode.setText(generatedCode);
    }

    public TextArea getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode.setText(userCode);
    }

    public Label getCountWords() {
        return countWords;
    }

    public void setCountWords(String countWords) {
        this.countWords.setText(countWords);
    }

    public void setGeneratedCode(TextArea generatedCode) {
        this.generatedCode = generatedCode;
    }

    public Label getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(String leadTime) {
        this.leadTime.setText(leadTime);
    }

    public Label getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(String matchPercentage) {
        this.matchPercentage.setText(matchPercentage);
    }

    public void setUserCode(TextArea userCode) {
        this.userCode = userCode;
    }
}