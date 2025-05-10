package com.das6.serversockets.controller.cliente;

import com.das6.serversockets.TicketInfo;
import com.das6.serversockets.WindowsUtil;
import com.das6.serversockets.server.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.Node;

public class PrincipalClienteController {

    private Client client;

    @FXML
    private FontIcon btnCerrar;

    @FXML
    private FontIcon btnMinimizar;

    @FXML private TableView<TicketInfo> tableView;
    @FXML private TableColumn<TicketInfo, String> columnCode;
    @FXML private TableColumn<TicketInfo, String> columnDateCreated;
    @FXML private TableColumn<TicketInfo, String> columntype;

    @FXML
    public void initialize(){
        btnCerrar.setOnMouseClicked(WindowsUtil::cerrarVentana);
        btnMinimizar.setOnMouseClicked(WindowsUtil::minimizarVenta);

        columnCode.setCellValueFactory(cell -> cell.getValue().codeProperty());
        columnDateCreated.setCellValueFactory(cell -> cell.getValue().dateCreatedProperty());
        columntype.setCellValueFactory(cell -> cell.getValue().statusProperty());
    }

    public void setClient(Client client){
        this.client = client;
    }

    @FXML
    private void onMousePressed(MouseEvent event){
        WindowsUtil.onMousePressed(event);
    }

    @FXML
    private  void onMouseDragged(MouseEvent event){
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
    }


}
