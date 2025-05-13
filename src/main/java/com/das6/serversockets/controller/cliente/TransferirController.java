package com.das6.serversockets.controller.cliente;

import com.das6.serversockets.WindowsUtil;
import com.das6.serversockets.shared.UserType;
import com.das6.serversockets.utilities.ControladorBase;
import com.das6.serversockets.utilities.VistaUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

public class TransferirController extends ControladorBase {
    @FXML
    private Label lblCodigoNuevo;
    @FXML
    private Button btnAceptar;
    @FXML
    private FontIcon btnCerrar;
    @FXML
    private FontIcon btnMinimizar;

    @FXML
    public void initialize() {
        btnCerrar.setOnMouseClicked(WindowsUtil::cerrarVentana);
        btnMinimizar.setOnMouseClicked(WindowsUtil::minimizarVenta);
        btnAceptar.setOnAction(event -> {
            Stage stage = (Stage) btnAceptar.getScene().getWindow();
            stage.close();
        });
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        WindowsUtil.onMousePressed(event);
    }

    @FXML
    private void onMouseDragged(MouseEvent event) {
        WindowsUtil.onMouseDragged(event);
    }

    public void setLblCodigoNuevo(String lblCodigoNuevo) {
        this.lblCodigoNuevo.setText(lblCodigoNuevo);
    }
}
