package com.das6.serversockets.utilities;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class VistaUtil {
    public static void cambiar(Stage stageActual, String rutaFXML, double anchoVentana, double altoVentana, String tipo) {
        try {
            FXMLLoader loader = new FXMLLoader(VistaUtil.class.getResource(rutaFXML));
            Parent root = loader.load();

            Stage nuevoStage = new Stage();

            Object controller = loader.getController();
            if (controller instanceof ControladorBase) {
                ((ControladorBase) controller).setStage(nuevoStage);
            }

            nuevoStage.initStyle(StageStyle.TRANSPARENT);

            Scene nuevaEscena = new Scene(root);
            nuevaEscena.setFill(Color.TRANSPARENT);

            Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();
            double posicionX, posicionY;

            if (tipo.equals("CHECKOUT") || tipo.equals("SERVICES")) {
                posicionX = pantalla.getMaxX() - anchoVentana;
                posicionY = pantalla.getMinY();
            } else {
                posicionX = (pantalla.getWidth() - anchoVentana) / 2;
                posicionY = (pantalla.getHeight() - altoVentana) / 2;
            }

            nuevoStage.setScene(nuevaEscena);
            nuevoStage.setX(posicionX);
            nuevoStage.setY(posicionY);
            nuevoStage.show();

            // Redondear
            Rectangle clip = new Rectangle(anchoVentana, altoVentana);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            root.setClip(clip);

            // Cerrar la ventana anterior
            stageActual.close();

        } catch (IOException e) {
            System.out.println("Error al cargar la vista: " + rutaFXML);
            e.printStackTrace();
        }
    }

}
