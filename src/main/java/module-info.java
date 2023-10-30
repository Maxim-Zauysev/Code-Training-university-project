module com.zaytsev.app.fxapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires java.sql;
    requires org.flywaydb.core;
    requires jbcrypt;


    opens com.zaytsev.app.fxapplication to javafx.fxml;
    exports com.zaytsev.app.fxapplication;
    exports com.zaytsev.app.fxapplication.Controllers;
    opens com.zaytsev.app.fxapplication.Controllers to javafx.fxml;
}