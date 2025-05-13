package com.das6.serversockets.server;

import com.das6.serversockets.controller.Presentacion.PresentacionController;
import com.das6.serversockets.controller.cliente.PrincipalClienteController;
import com.das6.serversockets.shared.SocketJsonUtil;
import com.das6.serversockets.shared.UserType;
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
    private int deskNumber;

    private UserType userType;

    private PrincipalClienteController controller;
    private PresentacionController presController;

    private int lastQueueSize = 0;

    // escuchar y matar el hilo
    private volatile boolean listening = true;
    private Thread escuchaThread;

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

    public JSONObject iniciarSesion(JSONObject creds, int deskNumber) {
        try {
            SocketJsonUtil.send(out, creds);
            JSONObject user = SocketJsonUtil.receive(in);
            System.out.println(user);

            this.deskNumber = deskNumber;
            this.userType = UserType.convertTypeFromString(user
                    .getJSONObject("data")
                    .getString("type")
            );

            return user;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void escucharServidor() {
        escuchaThread = new Thread(() -> {
            try {
                while (listening && isClientConnected()) {
                    JSONObject response = SocketJsonUtil.receive(in);

                    switch (response.getString("action_type")) {
                        case "update_screen":
                            System.out.println(response);

                            JSONArray lastTickets = response.getJSONArray("data");

                            if (!lastTickets.isEmpty()) {

                                if (lastTickets.length() > lastQueueSize) {
                                    System.out.println("tamaño de la cola actual: " + lastTickets.length() + "\n tamaño de la cola anterior: " + lastQueueSize);
                                    lastQueueSize = lastTickets.length();
                                    Platform.runLater(() -> {
                                        presController.actualizarTicket(lastTickets.getJSONObject(lastTickets.length() - 1));
                                        presController.actualizarCola(lastTickets);
                                    });
                                } else if (lastTickets.length() < lastQueueSize) {
                                    System.out.println("tamaño de la cola actual: " + lastTickets.length() + "\n tamaño de la cola anterior: " + lastQueueSize);
                                    lastQueueSize = lastTickets.length();
                                    Platform.runLater(() -> presController.actualizarCola(lastTickets));
                                } else {
                                    System.out.println("tamaño de la cola actual: " + lastTickets.length() + "\n tamaño de la cola anterior: " + lastQueueSize);
                                    System.out.println("colas del mismo tamaño");
                                }

                            } else {
                                lastQueueSize = 0;
                            }

                            break;
                        case "finished_ticket":
                            System.out.println("Ticket terminado: " + response.getString("code"));
                            break;
                        case "update_checkout":
                            System.out.println(response);

                            JSONArray ticketsCheckout = response.getJSONArray("data");
                            Platform.runLater(() -> controller.actualizarTabla(ticketsCheckout));

//                            if (controller != null) {
//                                JSONArray ticketsCheckout = response.getJSONArray("data");
//                                Platform.runLater(() -> controller.actualizarTabla(ticketsCheckout));
//                            }

                            break;
                        case "update_service":
                            System.out.println(response);

                            JSONArray ticketsService = response.getJSONArray("data");
                            Platform.runLater(() -> controller.actualizarTabla(ticketsService));
                            break;
                        case "polled_ticket":
                            JSONObject polled = response.getJSONObject("data");
                            setTicket(polled);
                            System.out.println("Ticket recibido: " + getTicket());

                            if (controller != null) {
                                Platform.runLater(() -> controller.mostrarTicket(polled));
                            }

                            break;
                        case "new_ticket":
                            JSONObject nuevoTicket = response.getJSONObject("data");
                            setTicket(nuevoTicket);
                            System.out.println("Nuevo ticket generado: " + nuevoTicket);

                            break;
                        case "transfer_ticket":
                            System.out.println(response);
                            Platform.runLater(() -> controller.mostrarTicketTransferido(response.getString("code")));

                            break;
                        default:
                            System.out.println("Respuesta por defecto dentro de metodo escucharServidor " + response);
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al escuchar servidor: " + e.getMessage());
            }
        });
        escuchaThread.setDaemon(true);
        escuchaThread.start();
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
            request.put("deskNumber",this.deskNumber); //cambiar por el numero de escritorio cuando se integre
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

    public void transferirTicket() {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "transfer_ticket");
            request.put("no_ticket", ticket.getString("code"));
            switch (userType) {
                case UserType.CHECKOUT -> request.put("new_type", "SERVICE");
                case UserType.SERVICE -> request.put("new_type", "CHECKOUT");
            }
            SocketJsonUtil.send(out, request);
        } catch (IOException e) {
            System.out.println("Error al transferir ticket: " + e.getMessage());
        }
    }

    public void shutDown() {
        listening = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setController(PrincipalClienteController controller) {
        this.controller = controller;
    }

    public void setPresController(PresentacionController presController) {
        this.presController = presController;
    }
}
