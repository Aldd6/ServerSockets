module com.das6.serversockets {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.das6.serversockets to javafx.fxml;
    exports com.das6.serversockets;
}