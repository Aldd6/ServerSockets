package com.das6.serversockets.controller.Presentacion;

import com.das6.serversockets.WindowsUtil;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import org.kordamp.ikonli.javafx.FontIcon;

public class PresentacionController {
    @FXML
    private FontIcon btnCerrar;
    @FXML
    private FontIcon btnMinimizar;

    @FXML
    public void initialize() {
        btnCerrar.setOnMouseClicked(WindowsUtil::cerrarVentana);
        btnMinimizar.setOnMouseClicked(WindowsUtil::minimizarVenta);
    }

    @FXML
    private void OnMousePressed(MouseEvent event) { WindowsUtil.onMousePressed(event); }

    @FXML
    private  void onMouseDragged(MouseEvent event){
        WindowsUtil.onMouseDragged(event);
    }
}
