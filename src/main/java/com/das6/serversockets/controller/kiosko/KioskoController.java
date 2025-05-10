package com.das6.serversockets.controller.kiosko;

import com.das6.serversockets.WindowsUtil;
import com.das6.serversockets.server.Client;
import com.das6.serversockets.shared.SocketJsonUtil;
import com.das6.serversockets.utilities.ControladorBase;
import com.das6.serversockets.utilities.VistaUtil;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class KioskoController extends ControladorBase {

    private Client client;

    @FXML
    private FontIcon btnCerrar;

    @FXML
    private FontIcon btnMinimizar;

    @FXML
    private Button btnCaja;

    @FXML
    private Button btnServicio;

    @FXML
    public void initialize() {
        btnCerrar.setOnMouseClicked(WindowsUtil::cerrarVentana);
        btnMinimizar.setOnMouseClicked(WindowsUtil::minimizarVenta);
    }

    public void setClient(Client client) {
        this.client = client;
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
    private void generarTicketCaja(ActionEvent event) {
        System.out.println("Se hizo clic en el botón de CAJA");

        if (client != null) {
            client.generarTicket("CHECKOUT");
            mostrarVistaExitoTemporal();
        }
    }

    private void mostrarVistaExitoTemporal() {
        new Thread(() -> {

            JSONObject ticket = client.getTicket();

            System.out.println(ticket);

            String codigo = (ticket != null) ? ticket.getString("code") : "???";

            Platform.runLater(() -> {
                Stage stage = (Stage) btnCaja.getScene().getWindow();

                ExitoKioskoController controller = VistaUtil.cambiar(
                        stage,
                        "/com/das6/serversockets/Kiosko/exitoKiosko.fxml",
                        1080,
                        720,
                        "EXITO"
                );

                controller.setNumeroTicket(codigo);

                PauseTransition pausa = new PauseTransition(Duration.seconds(3));
                pausa.setOnFinished(e -> {
                    VistaUtil.cambiar(stage, "/com/das6/serversockets/Kiosko/kiosko.fxml", 1080, 720, "KIOSK");
                });
                pausa.play();
            });
        }).start();
    }

    @FXML
    private void generarTicketServicio(ActionEvent event) {
        System.out.println("Se hizo clic en el botón de SERVICIO AL CLIENTE");

        if (client != null) {
            client.generarTicket("SERVICE");
        }
    }

}
