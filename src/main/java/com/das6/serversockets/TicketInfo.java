package com.das6.serversockets;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TicketInfo {
    private final StringProperty code = new SimpleStringProperty();
    private final StringProperty dateCreated = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public TicketInfo(String code, String dateCreated, String status) {
        this.code.set(code);
        this.dateCreated.set(dateCreated);
        this.status.set(status);
    }

    public StringProperty codeProperty() { return code; }
    public StringProperty dateCreatedProperty() { return dateCreated; }
    public StringProperty statusProperty() { return status; }
}
