package com.das6.serversockets.server;

import com.das6.serversockets.shared.SocketJsonUtil;
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
                            break;
                        case "polled_ticket":
                            setTicket(response.getJSONObject("data"));
                            System.out.println(getTicket());
                            break;
                        default:
                            System.out.println(response);
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al escuchar servidor: " + e.getMessage());
            }
        }).start();
    }

    public void generarTicket() {
        try {
            JSONObject ticket = new JSONObject();
            ticket.put("action", "new_ticket");
            ticket.put("type", "CHECKOUT");
            ticket.put("ref_client", JSONObject.NULL);
            SocketJsonUtil.send(out, ticket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());

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


    /* public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Client client = new Client();

        System.out.print("ingrese su usuario: ");

        String usuario = sc.nextLine();

        System.out.print("ingrese su contrasenia: ");

        String contrasenia = sc.nextLine();

        JSONObject creds = new JSONObject();

        creds.put("username", usuario);
        creds.put("password", contrasenia);
        try {
            SocketJsonUtil.send(client.out, creds);
            JSONObject user = SocketJsonUtil.receive(client.in);
            System.out.println(user);
            String command;
            if(user.getJSONObject("data").getString("type").equals("KIOSK")) {
                while(client.isClientConnected()) {
                    command = sc.nextLine();
                    if(command.equals("generate")) {
                        JSONObject ticket = new JSONObject();
                        ticket.put("action","new_ticket");
                        ticket.put("type","CHECKOUT");
                        ticket.put("ref_client",JSONObject.NULL);
                        SocketJsonUtil.send(client.out, ticket);
                    }
                }
            }
            if(user.getJSONObject("data").getString("type").equals("SCREEN")) {
                while(client.isClientConnected()) {
                    System.out.println(SocketJsonUtil.receive(client.in));
                }
            }
            new Thread(() -> {
                try {
                    while(client.isClientConnected()) {
                        JSONObject response = SocketJsonUtil.receive(client.in);
                        switch(response.getString("action_type")) {
                            case "update", "finished_ticket", "transfer_ticket":
                                System.out.println(response);
                                break;
                            case "polled_ticket":
                                client.setTicket(response.getJSONObject("data"));
                                System.out.println(client.getTicket());
                                break;
                            default:
                                System.out.println(response);
                                break;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            while(client.isClientConnected()) {
                command = sc.nextLine();
                if(command.equals("poll")) {
                    JSONObject request = new JSONObject();
                    request.put("action","get_ticket");
                    SocketJsonUtil.send(client.out, request);
                }else if(command.equals("finish")) {
                    JSONObject request = new JSONObject();
                    request.put("action","finish_ticket");
                    request.put("no_ticket", client.getTicket().getString("code"));
                    SocketJsonUtil.send(client.out, request);
                }else if(command.equals("transfer")) {
                    JSONObject request = new JSONObject();
                    request.put("action","transfer_ticket");
                    request.put("no_ticket", client.getTicket().getString("code"));
                    request.put("new_type","SERVICE");
                    SocketJsonUtil.send(client.out, request);
                }
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }*/

}
