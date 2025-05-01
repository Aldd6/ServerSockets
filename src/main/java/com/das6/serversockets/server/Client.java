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
            this.socket = new Socket(HOST,PORT);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e) {
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
            if(socket != null) {
                socket.close();
            }
            if(in != null) {
                in.close();
            }
            if(out != null) {
                out.close();
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void iniciarSesion(JSONObject creds){
        Scanner sc = new Scanner(System.in);
        try {
            SocketJsonUtil.send(out, creds);
            JSONObject user = SocketJsonUtil.receive(in);
            System.out.println(user);

            String command;
            if(user.getString("type").equals("KIOSK")) {
                while(isClientConnected()) {
                    command = sc.nextLine();
                    if(command.equals("generate")) {
                        JSONObject ticket = new JSONObject();
                        ticket.put("action","new_ticket");
                        ticket.put("type","CHECKOUT");
                        ticket.put("ref_client",JSONObject.NULL);
                        SocketJsonUtil.send(out, ticket);
                    }
                }
            }
            if(user.getString("type").equals("SCREEN")) {
                while(isClientConnected()) {
                    System.out.println(SocketJsonUtil.receive(in));
                }
            }
            new Thread(() -> {
                try {
                    while(isClientConnected()) {
                        JSONObject response = SocketJsonUtil.receive(in);
                        switch(response.getString("action_type")) {
                            case "update", "finished_ticket", "transfer_ticket":
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
                    throw new RuntimeException(e);
                }
            }).start();
            while(isClientConnected()) {
                command = sc.nextLine();
                if(command.equals("poll")) {
                    JSONObject request = new JSONObject();
                    request.put("action","get_ticket");
                    SocketJsonUtil.send(out, request);
                }else if(command.equals("finish")) {
                    JSONObject request = new JSONObject();
                    request.put("action","finish_ticket");
                    request.put("no_ticket", getTicket().getString("code"));
                    SocketJsonUtil.send(out, request);
                }else if(command.equals("transfer")) {
                    JSONObject request = new JSONObject();
                    request.put("action","transfer_ticket");
                    request.put("no_ticket", getTicket().getString("code"));
                    request.put("new_type","SERVICE");
                    SocketJsonUtil.send(out, request);
                }
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
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
            if(user.getString("type").equals("KIOSK")) {
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
            if(user.getString("type").equals("SCREEN")) {
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
