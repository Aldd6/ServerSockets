package com.das6.serversockets.controller.Presentacion;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ItemController {
    @FXML
    private Label lblTicketItem;
    @FXML
    private Label lblCajaItem;

    public void setLblTicketItem(String ticketItem) {
        lblTicketItem.setText(ticketItem);
    }

    public void setLblCajaItem(String cajaItem) {
        lblCajaItem.setText(cajaItem);
    }
}
