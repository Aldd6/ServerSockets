module com.das6.serversockets {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires org.json;
    requires java.sql;
    requires java.naming;

    opens com.das6.serversockets to javafx.fxml;
    exports com.das6.serversockets;
}