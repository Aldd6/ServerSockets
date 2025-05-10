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

    public static <T> T cambiar(Stage stage, String rutaFXML, double anchoVentana, double altoVentana, String tipo) {
        try {
            FXMLLoader loader = new FXMLLoader(VistaUtil.class.getResource(rutaFXML));
            Parent root = loader.load();

            T controller = loader.getController();

            if (controller instanceof ControladorBase) {
                ((ControladorBase) controller).setStage(stage);
            }

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

            stage.close();

            if (!stage.isShowing() && stage.getStyle() == StageStyle.DECORATED) {
                stage.initStyle(StageStyle.TRANSPARENT);
            }

            stage.setScene(nuevaEscena);
            stage.setX(posicionX);
            stage.setY(posicionY);
            stage.show();

            Rectangle clip = new Rectangle(anchoVentana, altoVentana);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            root.setClip(clip);

            return loader.getController();

        } catch (IOException e) {
            System.out.println("Error al cambiar vista a: " + rutaFXML);
            e.printStackTrace();
            return null;
        }
    }
}

