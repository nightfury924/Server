module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens com.example.demo to com.google.gson , javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires java.sql;
    requires com.google.gson;

//    opens com.example.demo to javafx.fxml;
    exports com.example.demo;

//    requires javafx.controls;
//    requires javafx.fxml;
//    opens application to javafx.graphics, javafx.fxml;
//    exports gui to javafx.fxml;
//    opens gui to javafx.fxml;
}