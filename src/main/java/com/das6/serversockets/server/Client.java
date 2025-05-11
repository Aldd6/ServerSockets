package com.das6.serversockets.server;

import com.das6.serversockets.controller.cliente.PrincipalClienteController;
import com.das6.serversockets.shared.SocketJsonUtil;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {

    private static String HOST;
    private static int PORT;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private static Properties properties;

    private JSONObject ticket;

    private PrincipalClienteController controller;

    private static void loadProperties() {
        properties = new Properties();
        try {
            properties.load(Client.class.getClassLoader().getResourceAsStream("config.properties"));
            HOST = properties.getProperty("SERVER_HOSTNAME");
            PORT = Integer.parseInt(properties.getProperty("SERVER_COMMUNICATION_PORT"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Client() {
        loadProperties();
        try {
            this.socket = new Socket(HOST, PORT);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            freeResources();
            System.out.println(e.getMessage());
        }
    }

    public boolean isClientConnected() {
        return socket.isConnected();
    }

    public void setTicket(JSONObject ticket) {
        this.ticket = ticket;
    }

    public JSONObject getTicket() {
        return this.ticket;
    }

    private void freeResources() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public JSONObject iniciarSesion(JSONObject creds) {
        try {
            SocketJsonUtil.send(out, creds);
            JSONObject user = SocketJsonUtil.receive(in);
            System.out.println(user);

            return user;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void escucharServidor() {
        new Thread(() -> {
            try {
                while (isClientConnected()) {
                    JSONObject response = SocketJsonUtil.receive(in);

                    switch (response.getString("action_type")) {
                        case "update":
                        case "finished_ticket":
                        case "transfer_ticket":
                            System.out.println(response);

                            JSONArray tickets = response.getJSONArray("data");
                            Platform.runLater(() -> controller.actualizarTabla(tickets));

//                            if (controller != null) {
//                                JSONArray tickets = response.getJSONArray("data");
//                                Platform.runLater(() -> controller.actualizarTabla(tickets));
//                            }

                            break;
                        case "polled_ticket":
                            JSONObject polled = response.getJSONObject("data");
                            setTicket(polled);
                            System.out.println("Ticket recibido: " + getTicket());

                            if (controller != null){
                                Platform.runLater(() -> controller.mostrarTicket(polled));
                            }

                            break;
                        case "new_ticket":
                            JSONObject nuevoTicket = response.getJSONObject("data");
                            setTicket(nuevoTicket);
                            System.out.println("Nuevo ticket generado: " + nuevoTicket);

                            break;

                        default:
                            System.out.println("Respuesta por defecto dentro de metodo escucharServidor " + response);
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al escuchar servidor: " + e.getMessage());
            }
        }).start();
    }

    public void generarTicket(String tipo) {
        try {
            JSONObject ticket = new JSONObject();
            ticket.put("action", "new_ticket");
            ticket.put("type", tipo);
            ticket.put("ref_client", JSONObject.NULL);

            SocketJsonUtil.send(out, ticket);
            System.out.println("Ticket generado para tipo " + tipo);
            System.out.println("Ticket enviado: " + ticket);

        } catch (IOException ex) {
            System.out.println("Error al generar ticket: " + ex.getMessage());
        }
    }

    public void iniciarEscuchaPantalla() {
        new Thread(() -> {
            try {
                while (isClientConnected()) {
                    JSONObject data = SocketJsonUtil.receive(in);
                    System.out.println(data);
                }

            } catch (IOException ex) {
                System.out.println("Error al escuchar la pantalla: " + ex.getMessage());
            }
        }).start();

    }

    public void solicitarTicket() {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "get_ticket");
            SocketJsonUtil.send(out, request);
        } catch (IOException e) {
            System.out.println("Error al solicitar ticket: " + e.getMessage());
        }
    }

    public void finalizarTicket(String code) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "finish_ticket");
            request.put("no_ticket", code);
            SocketJsonUtil.send(out, request);
        } catch (IOException e) {
            System.out.println("Error al finalizar ticket: " + e.getMessage());
        }
    }

    public void transferirTicket(String code, String nuevoTipo) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "transfer_ticket");
            request.put("no_ticket", code);
            request.put("new_type", nuevoTipo);
            SocketJsonUtil.send(out, request);
        } catch (IOException e) {
            System.out.println("Error al transferir ticket: " + e.getMessage());
        }
    }

    public void setController(PrincipalClienteController controller) {
        this.controller = controller;
    }
}
