package com.das6.serversockets.controller.Presentacion;

import com.das6.serversockets.WindowsUtil;
import com.das6.serversockets.server.Client;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;
import javax.sound.sampled.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PresentacionController {
    private Client client;

    private AudioInputStream audioStream;
    private Clip clip;
    private boolean audioHasBeenPlayed = false;

    @FXML
    private FontIcon btnCerrar;
    @FXML
    private FontIcon btnMinimizar;
    @FXML
    private VBox vLastCalled;
    @FXML
    private Label lblTicket;
    @FXML
    private Label lblCaja;
    @FXML
    private VBox txtTicketCalled;

    private ArrayList<Node> items;

    @FXML
    public void initialize() {
        btnCerrar.setOnMouseClicked(WindowsUtil::cerrarVentana);
        btnMinimizar.setOnMouseClicked(WindowsUtil::minimizarVenta);
        items = new ArrayList<>(4);

        try {
            File file = new File("src/main/resources/nextTicketSound.wav");
            audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();

            clip.open(audioStream);
            clip.setMicrosecondPosition(TimeUnit.SECONDS.toMillis(1));
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setClient(Client client) { this.client = client;}

    @FXML
    private void OnMousePressed(MouseEvent event) { WindowsUtil.onMousePressed(event); }

    @FXML
    private  void onMouseDragged(MouseEvent event){
        WindowsUtil.onMouseDragged(event);
    }

    public void actualizarCola(JSONArray tickets) {
        Platform.runLater(() -> {
            Node item;
            ItemController itemController;
            FXMLLoader loader;

            JSONObject ticket;
            String ticketCode;
            String caja;

            int counterFlag = 0;

            items.clear();

            if(!tickets.isEmpty()) {

                for(int i = 0; i < tickets.length(); i++) {
                    try {
                        loader = new FXMLLoader(getClass().getResource("/com/das6/serversockets/Presentacion/itemLista.fxml"));
                        item = loader.load();
                        itemController = loader.getController();

                        ticket = tickets.getJSONObject(i);
                        ticketCode = ticket.getString("code");
//                      caja = ticket.getString("caja");

                        itemController.setLblTicketItem(ticketCode);

                        items.add(item);
                        counterFlag++;

                        if(counterFlag == 4) { break; }
                    }catch (IOException e){
                        System.out.println(e.getMessage());
                    }
                }

            }


            if(!items.isEmpty()) {

                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.4));
                fadeOut.setNode(vLastCalled);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> {
                    vLastCalled.getChildren().clear();
                    vLastCalled.getChildren().addAll(items);
                    FadeTransition fadeInNew = new FadeTransition(Duration.seconds(0.4));
                    fadeInNew.setNode(vLastCalled);
                    fadeInNew.setFromValue(0.0);
                    fadeInNew.setToValue(1.0);
                    fadeInNew.play();
                });
                fadeOut.play();
            }
        });
    }

    public void actualizarTicket(JSONObject ticket) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.4));
        fadeOut.setNode(txtTicketCalled);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            lblTicket.setText(ticket.getString("code"));
            FadeTransition fadeInNew = new FadeTransition(Duration.seconds(0.4));
            fadeInNew.setNode(txtTicketCalled);
            fadeInNew.setFromValue(0.0);
            fadeInNew.setToValue(1.0);
            fadeInNew.setOnFinished(e -> {
                if(audioHasBeenPlayed) {
                    clip.setMicrosecondPosition(TimeUnit.SECONDS.toMillis(1));
                }else {
                    audioHasBeenPlayed = true;
                }
                clip.start();
            });
            fadeInNew.play();
        });

        clip.stop();
        fadeOut.play();
    }
}
