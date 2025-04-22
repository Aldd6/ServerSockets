package com.das6.serversockets;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/das6/serversockets/Cliente/principal-cliente.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();

        double anchoVentana = 366;
        double altoVentana = 251;

        double posicionX = pantalla.getMaxX() - anchoVentana;
        double posicionY = pantalla.getMinY();

        stage.setX(posicionX);
        stage.setY(posicionY);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}