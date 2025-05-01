module com.das6.serversockets {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.bootstrapicons;
    requires org.kordamp.bootstrapfx.core;
    requires org.json;
    requires java.sql;
    requires java.naming;

    opens com.das6.serversockets.controller.cliente to javafx.fxml;
    opens com.das6.serversockets.controller.login to javafx.fxml;

    exports com.das6.serversockets;
    exports com.das6.serversockets.controller.cliente;
}