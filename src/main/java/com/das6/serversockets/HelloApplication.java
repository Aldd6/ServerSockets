package com.das6.serversockets;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/das6/serversockets/Cliente/principal-cliente.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);


        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();
        double anchoVentana = 366;
        double altoVentana = 251;
        double posicionX = pantalla.getMaxX() - anchoVentana;
        double posicionY = pantalla.getMinY();

        stage.setX(posicionX);
        stage.setY(posicionY);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        Rectangle clip = new Rectangle(anchoVentana, altoVentana);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        root.setClip(clip);
    }

    public static void main(String[] args) {
        launch();
    }
}
