package com.zaytsev.app.fxapplication.data;

import java.time.LocalDateTime;

public class UserStatisticsDto {
    private String userCode;
    private String generatedCode;
    private LocalDateTime dateTesting;
    private Float leadTime;
    private Integer countWords;
    private Float matchPercentage;
    private String codeComplexity;

    // Конструктор, геттеры и сеттеры
    public UserStatisticsDto(String userCode, String generatedCode, LocalDateTime dateTesting, Float leadTime, Integer countWords, Float matchPercentage, String codeComplexity) {
        this.userCode = userCode;
        this.generatedCode = generatedCode;
        this.dateTesting = dateTesting;
        this.leadTime = leadTime;
        this.countWords = countWords;
        this.matchPercentage = matchPercentage;
        this.codeComplexity = codeComplexity;
    }

    public String getCodeComplexity() {
        return codeComplexity;
    }

    public void setCodeComplexity(String codeComplexity) {
        this.codeComplexity = codeComplexity;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getGeneratedCode() {
        return generatedCode;
    }

    public void setGeneratedCode(String generatedCode) {
        this.generatedCode = generatedCode;
    }

    public LocalDateTime getDateTesting() {
        return dateTesting;
    }

    public void setDateTesting(LocalDateTime dateTesting) {
        this.dateTesting = dateTesting;
    }

    public Float getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Float leadTime) {
        this.leadTime = leadTime;
    }

    public Integer getCountWords() {
        return countWords;
    }

    public void setCountWords(Integer countWords) {
        this.countWords = countWords;
    }

    public Float getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(Float matchPercentage) {
        this.matchPercentage = matchPercentage;
    }
}
