module ru.vsu.cs.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires guru.nidi.graphviz;
    requires java.desktop;


    opens ru.vsu.cs.demo to javafx.fxml;
    exports ru.vsu.cs.demo;
}