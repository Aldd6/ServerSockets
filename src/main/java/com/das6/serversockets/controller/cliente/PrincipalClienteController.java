package com.das6.serversockets.controller.cliente;

import com.das6.serversockets.WindowsUtil;
import com.das6.serversockets.server.Client;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.Node;

public class PrincipalClienteController {

    private Client client;

    @FXML
    private FontIcon btnCerrar;

    @FXML
    private FontIcon btnMinimizar;

    @FXML
    public void initialize(){
        btnCerrar.setOnMouseClicked(WindowsUtil::cerrarVentana);
        btnMinimizar.setOnMouseClicked(WindowsUtil::minimizarVenta);
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

}
