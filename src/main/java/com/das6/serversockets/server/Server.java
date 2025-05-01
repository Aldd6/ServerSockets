package com.das6.serversockets.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.time.LocalDateTime;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader inStream;
    private BufferedWriter outStream;
    private int communicationPort;

    private Properties properties;

    public Server() throws IOException {
        loadProperties();
        serverSocket = new ServerSocket(communicationPort);
    }

    public void run() {
        System.out.println(LocalDateTime.now() + ": Server listening on port " + communicationPort);
        while(true) {
            clientSocket = null;
            try {
                System.out.println(LocalDateTime.now() + ": Waiting for client connection...");
                clientSocket = serverSocket.accept();
                System.out.println(LocalDateTime.now() + ": Incoming connection from " + clientSocket.getInetAddress() + " at port " + clientSocket.getPort());
                inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outStream = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                Thread clientThread = new Thread(new ClientHandler(clientSocket, inStream, outStream));
                clientThread.start();
            }catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void loadProperties() {
        properties = new Properties();
        try {
            properties.load(Server.class.getClassLoader().getResourceAsStream("config.properties"));
            communicationPort = Integer.parseInt(properties.getProperty("SERVER_COMMUNICATION_PORT"));
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void stop() {
        try {
            if(serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }catch (IOException e) {
            System.out.println(LocalDateTime.now() + ": Closing server socket");
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.run();
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
