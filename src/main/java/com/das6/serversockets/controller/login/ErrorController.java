package com.das6.serversockets.controller.login;

import com.das6.serversockets.WindowsUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class ErrorController {
    @FXML
    private FontIcon btnCerrar;

    @FXML
    private FontIcon btnMinimizar;

    @FXML
    private Button btnAceptar;

    @FXML
    public void initialize() {
        btnCerrar.setOnMouseClicked(WindowsUtil::cerrarVentana);
        btnMinimizar.setOnMouseClicked(WindowsUtil::minimizarVenta);
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        WindowsUtil.onMousePressed(event);
    }

    @FXML
    private void onMouseDragged(MouseEvent event) {
        WindowsUtil.onMouseDragged(event);
    }

    @FXML
    private void Aceptar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/das6/serversockets/Login/login-login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            // Centrar la ventana en pantalla
            Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();
            double ancho = 366;
            double alto = 251;
            stage.setX((pantalla.getWidth() - ancho) / 2);
            stage.setY((pantalla.getHeight() - alto) / 2);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

            // Redondear ventana si deseas
            Rectangle clip = new Rectangle(ancho, alto);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            root.setClip(clip);

            // Cerrar la ventana actual (Error)
            Stage actual = (Stage) btnAceptar.getScene().getWindow();
            actual.close();

        } catch (IOException e) {
            System.out.println("Error al cargar la vista de login");
            e.printStackTrace();
        }
    }

}
