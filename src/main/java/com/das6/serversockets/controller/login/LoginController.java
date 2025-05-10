package com.das6.serversockets.controller.login;

import com.das6.serversockets.WindowsUtil;
import com.das6.serversockets.controller.cliente.PrincipalClienteController;
import com.das6.serversockets.controller.kiosko.KioskoController;
import com.das6.serversockets.server.Client;
import com.das6.serversockets.utilities.ControladorBase;
import com.das6.serversockets.utilities.VistaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;

public class LoginController extends ControladorBase {
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

    public void iniciar(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String contrasenia = chkMostrarContra.isSelected() ? txtContraseniaPlano.getText() : txtContrasenia.getText();

        Client client = new Client();

        JSONObject credenciales = new JSONObject();
        credenciales.put("username", usuario);
        credenciales.put("password", contrasenia);

        JSONObject responseUser = client.iniciarSesion(credenciales);

        if (responseUser != null && responseUser.getInt("status") == 200) {

            JSONObject data = responseUser.getJSONObject("data");

            String tipo = data.getString("type");

            System.out.println(tipo);

            switch (tipo) {
                case "CHECKOUT":
                    PrincipalClienteController controllerCliente = VistaUtil.cambiar(stage, "/com/das6/serversockets/Cliente/principal-cliente.fxml", 366, 251, "CHECKOUT");
                    controllerCliente.setClient(client);
                    break;
                case "SERVICE":
                    VistaUtil.cambiar(stage, "/com/das6/serversockets/Cliente/services-cliente.fxml", 366, 251, "SERVICES");
                    break;
                case "KIOSK":
                    KioskoController controller = VistaUtil.cambiar(stage, "/com/das6/serversockets/Kiosko/kiosko.fxml", 1080, 720, "KIOSK");
                    controller.setClient(client);
                    break;
                case "SCREEN":
                    VistaUtil.cambiar(stage, "/com/das6/serversockets/Cliente/principal-cliente.fxml", 366, 251, "CHECK");
                    break;
                default:
                    System.out.println("VISTA POR DEFECTO");
                    break;
            }

        } else {
            VistaUtil.cambiar(stage, "/com/das6/serversockets/Login/error-login.fxml", 366, 251, "ERROR");
        }
    }
}