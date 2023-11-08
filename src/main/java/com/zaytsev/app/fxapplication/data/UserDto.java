package com.zaytsev.app.fxapplication.data;

public class UserDto {
    String name;
    String userCode;
    String generatedCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "UserDto{" +
                "name='" + name + '\'' +
                ", userCode='" + userCode + '\'' +
                ", generatedCode='" + generatedCode + '\'' +
                '}';
    }
}
