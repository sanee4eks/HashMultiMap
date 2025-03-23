package ru.vsu.cs.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("/ru/vsu/cs/demo/hello-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("HashMultiMap Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
