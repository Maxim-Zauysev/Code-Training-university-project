package com.zaytsev.app.fxapplication.Controllers;

import com.zaytsev.app.fxapplication.CodeApplication;
import com.zaytsev.app.fxapplication.data.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.CornerRadii;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatisticController implements Initializable {

    @FXML
    private Label countWords;
    @FXML
    private Label leadTime;
    @FXML
    private Label matchPercentage;
    @FXML
    private VBox generatedCodeContainer;
    @FXML
    private Button compareButton;
    @FXML
    private VBox userCodeContainer;

    private CodeArea userCode;
    private CodeArea generatedCode;
    private final DatabaseManager databaseManager = new DatabaseManager();

    @FXML
    private void onCompareButtonAction(ActionEvent event) {
        try {
            Parent root;
            FXMLLoader loader = new FXMLLoader(CodeApplication.class.getResource("сomparisonResultForm.fxml"));
            root = loader.load();

            CompareController compareController = loader.getController();
            compareController.setUsers(databaseManager.getUsersByGeneratedCode(generatedCode.getText()));
            Stage stage = (Stage) compareButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/allStatisticsDarkTheme.css").toExternalForm());
            compareController.generateAllStatistic(compareController.getUsers());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserCode(String code) {
        userCodeContainer.getChildren().clear(); // Очистить предыдущий текст

        String[] userCodeLines = code.split("\n");
        for (String line : userCodeLines) {
            // Вычисление подсветки синтаксиса для данной строки
            StyleSpans<Collection<String>> styleSpans = computeHighlighting(line);

            // Создание TextFlow и применение стилей
            TextFlow textFlow = new TextFlow();
            setTextFlowStyleSpans(textFlow, styleSpans, line + "\n");

            userCodeContainer.getChildren().add(textFlow); // Добавить TextFlow в VBox
        }

    }

    public void setGeneratedCode(String code) {
        generatedCodeContainer.getChildren().clear(); // Очистить предыдущий текст

        String[] generatedCodeLines = code.split("\n");
        for (String line : generatedCodeLines) {
            // Вычисление подсветки синтаксиса для данной строки
            StyleSpans<Collection<String>> styleSpans = computeHighlighting(line);

            // Создание TextFlow и применение стилей
            TextFlow textFlow = new TextFlow();
            setTextFlowStyleSpans(textFlow, styleSpans, line + "\n");

            highlightTextFlow(textFlow, Color.LIGHTGREEN); // Устанавливаем зеленый фон для каждой строки
            generatedCodeContainer.getChildren().add(textFlow); // Добавить TextFlow в VBox
        }

    }

    private void setTextFlowStyleSpans(TextFlow textFlow, StyleSpans<Collection<String>> styleSpans, String text) {
        textFlow.getChildren().clear();
        int currentIndex = 0;

        for (StyleSpan<Collection<String>> span : styleSpans) {
            Text spanNode = new Text(text.substring(currentIndex, currentIndex + span.getLength()));

            StringJoiner styleJoiner = new StringJoiner(";");
            span.getStyle().forEach(styleClass -> {
                String css = convertStyleClassToCss(styleClass);
                if (css != null && !css.isEmpty()) {
                    styleJoiner.add(css);
                }
            });

            // Применяем стиль, если он есть
            if (styleJoiner.length() > 0) {
                spanNode.setStyle(styleJoiner.toString());
            }

            textFlow.getChildren().add(spanNode);
            currentIndex += span.getLength();
        }
    }

    private String convertStyleClassToCss(String styleClass) {
        // Здесь вы должны преобразовать ваш класс стиля в CSS стиль
        // Например, если styleClass == "keyword", то может быть "fill: blue;"
        switch (styleClass) {
            case "keyword":
                return "-fx-fill: blue;";
            case "paren":
            case "brace":
            case "bracket":
                return "-fx-fill: black;";
            case "semicolon":
                return "-fx-fill: orange;";
            case "string":
                return "-fx-fill: green;";
            case "comment":
                return "-fx-fill: gray;";
            default:
                return "-fx-fill: white;"; // Изменил на белый цвет для неопределённого стиля
        }
    }


    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "if", "goto", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    // Переопределите метод для вычисления подсветки синтаксиса для TextFlow
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    public void setCountWords(String words) {
        countWords.setText(words);
    }

    public void setLeadTime(String time) {
        leadTime.setText(time);
    }

    public void setMatchPercentage(String percentage) {
        matchPercentage.setText(percentage);
    }


    // Метод для сравнения и выделения текста
    public void compareAndHighlight() {
        for (int i = 0; i < userCodeContainer.getChildren().size(); i++) {
            TextFlow userTextFlow = (TextFlow) userCodeContainer.getChildren().get(i);
            TextFlow generatedTextFlow = (TextFlow) generatedCodeContainer.getChildren().get(i);

            String userText = ((Text) userTextFlow.getChildren().get(0)).getText().trim();
            String generatedText = ((Text) generatedTextFlow.getChildren().get(0)).getText().trim();

            if (userText.equals(generatedText)) {
                // Совпадение - выделить зеленым
                highlightTextFlow(userTextFlow, Color.LIGHTGREEN);
            } else {
                // Не совпадение - выделить красным
                highlightTextFlow(userTextFlow, Color.SALMON);
            }
        }
    }

    // Вспомогательный метод для выделения TextFlow заданным цветом
    private void highlightTextFlow(TextFlow textFlow, Color color) {
        // Задаем фон для TextFlow
        BackgroundFill backgroundFill = new BackgroundFill(color, new CornerRadii(0), Insets.EMPTY);
        Background background = new Background(backgroundFill);
        textFlow.setBackground(background);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        compareButton.setOnAction(this::onCompareButtonAction);
    }



    public CodeArea getUserCode() {
        return userCode;
    }

    public void setUserCodeArea(CodeArea userCode) {
        this.userCode = userCode;
    }

    public CodeArea getGeneratedCode() {
        return generatedCode;
    }

    public void setGeneratedCodeArea(CodeArea generatedCode) {
        this.generatedCode = generatedCode;
    }
}
