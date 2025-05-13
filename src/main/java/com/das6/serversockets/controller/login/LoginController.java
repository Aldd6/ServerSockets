package com.das6.serversockets.controller.login;

import com.das6.serversockets.WindowsUtil;
import com.das6.serversockets.controller.Presentacion.PresentacionController;
import com.das6.serversockets.controller.cliente.PrincipalClienteController;
import com.das6.serversockets.controller.kiosko.KioskoController;
import com.das6.serversockets.server.Client;
import com.das6.serversockets.utilities.ControladorBase;
import com.das6.serversockets.utilities.VistaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.UnaryOperator;

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
    private TextField txtNumCaja;

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

        // Expresión regular para limitar los numeros del 1 al 10
        UnaryOperator<TextFormatter.Change> regex = change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("([1-9]|10)?")) {
                return change;
            } else {
                return null;
            }
        };

        TextFormatter<String> formatter = new TextFormatter<>(regex);
        txtNumCaja.setTextFormatter(formatter);


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
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        String usuario = txtUsuario.getText();
        String contrasenia = chkMostrarContra.isSelected() ? txtContraseniaPlano.getText() : txtContrasenia.getText();


        String txtCaja = txtNumCaja.getText();
        int numCaja;

        if (txtCaja.isEmpty()){
            numCaja = 0;
        }else{
            numCaja = Integer.parseInt(txtCaja);
        }

        Client client = new Client();

        JSONObject credenciales = new JSONObject();
        credenciales.put("username", usuario);
        credenciales.put("password", contrasenia);

        JSONObject responseUser = client.iniciarSesion(credenciales, numCaja);
        client.escucharServidor();

        if (responseUser != null && responseUser.getInt("status") == 200) {

            JSONObject data = responseUser.getJSONObject("data");

            String tipo = data.getString("type");

            System.out.println(tipo);

            switch (tipo) {
                case "CHECKOUT":
                    PrincipalClienteController controllerCliente = VistaUtil.cambiar(stage, "/com/das6/serversockets/Cliente/principal-cliente.fxml", 366, 251, "CHECKOUT");
                    controllerCliente.setClient(client);
                    client.setController(controllerCliente);
                    break;
                case "SERVICE":
                    PrincipalClienteController controllerServicio = VistaUtil.cambiar(stage, "/com/das6/serversockets/Cliente/services-cliente.fxml", 366, 251, "SERVICES");
                    controllerServicio.setClient(client);
                    client.setController(controllerServicio);
                    break;
                case "KIOSK":
                    KioskoController controller = VistaUtil.cambiar(stage, "/com/das6/serversockets/Kiosko/kiosko.fxml", 1080, 720, "KIOSK");
                    controller.setClient(client);
                    break;
                case "SCREEN":
                    PresentacionController controllerPresentacion = VistaUtil.cambiar(stage, "/com/das6/serversockets/Presentacion/vistaVentana.fxml", 1080, 720, "SCREEN");
                    controllerPresentacion.setClient(client);
                    client.setPresController(controllerPresentacion);
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