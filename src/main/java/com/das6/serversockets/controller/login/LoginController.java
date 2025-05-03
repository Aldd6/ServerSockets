package com.das6.serversockets.controller.login;

import com.das6.serversockets.WindowsUtil;
import com.das6.serversockets.server.Client;
import com.das6.serversockets.shared.SocketJsonUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
                    cargarVista("/com/das6/serversockets/Cliente/principal-cliente.fxml", 366, 251, "CHECKOUT");
                    break;
                case "SERVICE":
                    cargarVista("/com/das6/serversockets/Cliente/services-cliente.fxml", 366, 251, "SERVICES");
                    break;
                case "KIOSK":
                    cargarVista("/com/das6/serversockets/Kiosko/vistaKiosko.fxml", 1280, 720, "KIOSK");
                    break;
                case "SCREEN":
                    cargarVista("/com/das6/serversockets/Cliente/principal-cliente.fxml", 366, 251, "CHECK");
                    break;
                default:
                    System.out.println("VISTA POR DEFECTO");
                    break;
            }

        } else {
            cargarVista("/com/das6/serversockets/Login/error-login.fxml", 366, 251, "ERROR");
        }

    }

    private void cargarVista(String rutaFXML, double anchoVentana, double altoVentana, String tipo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            // obtenemos los limites de la ventana
            Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();

            double posicionX;
            double posicionY;

            if (tipo.equals("CHECKOUT") || tipo.equals("SERVICES")) {
                posicionX = pantalla.getMaxX() - anchoVentana;
                posicionY = pantalla.getMinY();
            } else {
                posicionX = (pantalla.getWidth() - anchoVentana) / 2;
                posicionY = (pantalla.getHeight() - altoVentana) / 2;
            }

            // Posiciona la pantalla
            stage.setX(posicionX);
            stage.setY(posicionY);

            // Eliminamos los botones de la ventana
            stage.initStyle(StageStyle.TRANSPARENT);

            // Pasamos la escena a la ventana del stage y mostramos la ventana
            stage.setScene(scene);
            stage.show();

            // Redondeamos  la ventana.
            Rectangle clip = new Rectangle(anchoVentana, altoVentana);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            root.setClip(clip);

            Stage actual = (Stage) btnIniciarSesion.getScene().getWindow();
            actual.close();

        } catch (IOException e) {
            System.out.println("Error al cargar la vista: " + rutaFXML);
            throw new RuntimeException(e);
        }
    }
}
