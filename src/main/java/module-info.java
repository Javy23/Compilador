module com.example.lexico {
    requires javafx.controls;
    requires javafx.fxml;
    requires jflex.full;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;


    opens com.example.lexico to javafx.fxml;
    exports com.example.lexico;
    exports com.example.lexico.Controller;
    opens com.example.lexico.Controller to javafx.fxml;
}