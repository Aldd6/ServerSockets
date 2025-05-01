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
        // Cargamos el archivo FXML que define la interfaz de usuario
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/das6/serversockets/Login/login-login.fxml"));
        AnchorPane root = fxmlLoader.load();

        // Creamos una nueva escena para mostrar la ventana y la hacemos transparente
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        double anchoVentana = 366;
        double altoVentana = 251;

        // obtenemos los limites de la ventana
        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();

        // esto lo usamos para calcular y posiciona la ventana en la izquierda superior
        /*
        double posicionX = pantalla.getMaxX() - anchoVentana;
        double posicionY = pantalla.getMinY();
        */

        // calculo para centrar
        double posicionX = (pantalla.getWidth() - anchoVentana )/ 2;
        double posicionY = (pantalla.getHeight() - altoVentana) / 2;

        // Posiciona la pantalla
        stage.setX(posicionX);
        stage.setY(posicionY);

        // Eliminamos los botones de la ventana
        stage.initStyle(StageStyle.TRANSPARENT);

        // Pasamos la escena a la ventana del stage y mostramos la ventana
        stage.setScene(scene);
        stage.show();

        // Redondeamos  la ventana.
        Rectangle clip = new Rectangle(anchoVentana, altoVentana);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        root.setClip(clip);
    }

    public static void main(String[] args) {
        launch();
    }
}
