package com.das6.serversockets.controller.kiosko;

import com.das6.serversockets.WindowsUtil;
import com.das6.serversockets.utilities.ControladorBase;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.kordamp.ikonli.javafx.FontIcon;

public class ExitoKioskoController extends ControladorBase {
    @FXML
    private Label lbNumeroTicket;

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
    private void onMousePressed(MouseEvent event) {
        WindowsUtil.onMousePressed(event);
    }

    @FXML
    private void onMouseDragged(MouseEvent event) {
        WindowsUtil.onMouseDragged(event);
    }


      public void setNumeroTicket(String codigo){
          lbNumeroTicket.setText(codigo);
      }
}
