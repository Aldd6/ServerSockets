package com.das6.serversockets.controller.login;

import com.das6.serversockets.WindowsUtil;
import com.das6.serversockets.server.Client;
import com.das6.serversockets.shared.SocketJsonUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class LoginController {
    @FXML
    private FontIcon btnCerrar;

    @FXML
    private FontIcon btnMinimizar;

    @FXML
    private TextField txtUsuario;

    @FXML
    private TextField txtContraseniaPlano;

    @FXML
    private PasswordField txtContrasenia;

    @FXML
    private CheckBox chkMostrarContra;

    @FXML
    private Button btnIniciarSesion;

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

    public void mostrarContrasenia() {
        if (chkMostrarContra.isSelected()) {
            txtContraseniaPlano.setText(txtContrasenia.getText());
            txtContraseniaPlano.setVisible(true);
            txtContraseniaPlano.setManaged(true);

            txtContrasenia.setVisible(false);
            txtContrasenia.setManaged(false);
        } else {
            txtContrasenia.setText(txtContraseniaPlano.getText());
            txtContrasenia.setVisible(true);
            txtContrasenia.setManaged(true);

            txtContraseniaPlano.setVisible(false);
            txtContraseniaPlano.setManaged(false);
        }
    }

    public void iniciar(ActionEvent event){
        String usuario = txtUsuario.getText();
        String contrasenia = chkMostrarContra.isSelected() ? txtContraseniaPlano.getText() : txtContrasenia.getText();

        Client client = new Client();

        JSONObject credenciales = new JSONObject();
        credenciales.put("username", usuario);
        credenciales.put("password", contrasenia);

        client.iniciarSesion(credenciales);

    }
}
