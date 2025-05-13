package com.das6.serversockets.controller.cliente;

import com.das6.serversockets.TicketInfo;
import com.das6.serversockets.WindowsUtil;
import com.das6.serversockets.server.Client;
import com.das6.serversockets.shared.UserType;
import com.das6.serversockets.utilities.ControladorBase;
import com.das6.serversockets.utilities.VistaUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.Node;
import org.w3c.dom.events.Event;

import java.io.IOException;
import java.sql.Time;

public class PrincipalClienteController extends ControladorBase {

    private Client client;

    @FXML
    private FontIcon btnCerrar;

    @FXML
    private FontIcon btnMinimizar;

    @FXML
    private TableView<TicketInfo> tableView;
    @FXML
    private TableColumn<TicketInfo, String> columnCode;
    @FXML
    private TableColumn<TicketInfo, String> columnDateCreated;
    @FXML
    private TableColumn<TicketInfo, String> columntype;

    @FXML
    private Button btnSiguiente;

    @FXML
    private Button btnTransferir;

    @FXML
    private Button btnFinalizar;

    @FXML
    private Label lbNumTicket;

    @FXML
    private Label lbTiempoTranscurrido;

    // cronometro
    private Timeline timer;
    private long startTimeMillis;

    @FXML
    public void initialize() {
        btnCerrar.setOnMouseClicked(event -> {
            if (client != null) {
                client.shutDown();
            }
            WindowsUtil.cerrarVentana(event);
        });
        btnMinimizar.setOnMouseClicked(WindowsUtil::minimizarVenta);

        columnCode.setCellValueFactory(cell -> cell.getValue().codeProperty());
        columnDateCreated.setCellValueFactory(cell -> cell.getValue().dateCreatedProperty());
        columntype.setCellValueFactory(cell -> cell.getValue().statusProperty());

        btnTransferir.setDisable(true);
        btnSiguiente.setDisable(true);
        btnFinalizar.setDisable(true);

        lbTiempoTranscurrido.setText("00:00:00");
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

    public void actualizarTabla(JSONArray tickets) {
        ObservableList<TicketInfo> data = FXCollections.observableArrayList();

        for (int i = 0; i < tickets.length(); i++) {
            JSONObject t = tickets.getJSONObject(i);
            data.add(new TicketInfo(
                    t.getString("code"),
                    t.getString("dateCreated").substring(11, 19),
                    t.getString("type")
            ));
        }

        tableView.setItems(data);

        boolean hayTickets = !data.isEmpty();
        btnTransferir.setDisable(!hayTickets);
        btnSiguiente.setDisable(!hayTickets);
    }

    @FXML
    private void transferirTicket(ActionEvent event) {
        System.out.println("Transfirió un ticket el cajero");
        client.transferirTicket();

        client.solicitarTicket();
    }

    @FXML
    private void terminarTicket(ActionEvent event) {
        System.out.println("Se termino un ticket");
        client.finalizarTicket(client.getTicket().getString("code"));
        client.setTicket(null);
        lbNumTicket.setText("Ticket");
        lbTiempoTranscurrido.setText("00:00:00");
    }

    @FXML
    private void siguienteTicket(ActionEvent event) {

        if (client.getTicket() != null) {
            System.out.println("Termino un ticket el cajero");
            client.finalizarTicket(client.getTicket().getString("code"));
        }

        System.out.println("Solicitó Ticket");
        client.solicitarTicket();
    }

    @FXML
    private void finalizarTicket(ActionEvent event){

        if (client.getTicket() != null){
            System.out.println("Se finaliza ticket");
            terminarTicket();
        }
    }

    @FXML
    public void mostrarTicket(JSONObject ticket) {
        lbNumTicket.setText(ticket.getString("code"));

        // Iniciar cronometro
        startTimer();
    }

    public void mostrarTicketTransferido(String ticket) {
        try {
            FXMLLoader loader = new FXMLLoader(PrincipalClienteController.class.getResource("/com/das6/serversockets/Cliente/transferir-cliente.fxml"));
            Parent root = loader.load();
            TransferirController controller = loader.getController();
            controller.setLblCodigoNuevo(ticket);
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            // Centrar la ventana en pantalla
            Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();
            double ancho = 366;
            double alto = 251;
            stage.setX(pantalla.getMaxX() - ancho);
            stage.setY(pantalla.getMinY());

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

            // Redondear ventana si deseas
            Rectangle clip = new Rectangle(ancho, alto);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            root.setClip(clip);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }

        // inicio
        startTimeMillis = System.currentTimeMillis();

        // se crea el timeline que cada segundo llama al updateTimer
        timer = new Timeline(
                new KeyFrame(Duration.ZERO, e -> updateTimer()),
                new KeyFrame(Duration.seconds(1))
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimer() {
        long elapsed = System.currentTimeMillis() - startTimeMillis;
        long hours = elapsed / 3_600_000;
        long minutes = (elapsed % 3_600_000) / 60_000;
        long seconds = (elapsed % 60_000) / 1_000;
        String text = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        lbTiempoTranscurrido.setText(text);
    }

}
