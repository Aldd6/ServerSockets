package com.das6.serversockets.controller.cliente;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.Node;

public class PrincipalClienteController {

    @FXML
    private FontIcon btnCerrar;

    @FXML
    private FontIcon btnMinimizar;

    @FXML
    public void initialize(){
        btnCerrar.setOnMouseClicked(event -> cerrarVentana(event));
        btnMinimizar.setOnMouseClicked(event -> minimizarVentana(event));
    }

    private void minimizarVentana(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.isIconified();
    }

    private void cerrarVentana(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


}
