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
    private Queue<String> checkOutQueue;
    private Queue<String> frontDeskQueue;
    private Queue<String> generalQueue;

    public Server() throws IOException {
        loadProperties();
        serverSocket = new ServerSocket(communicationPort);
    }

    public void run() {
        checkOutQueue = new ConcurrentLinkedQueue<>();
        frontDeskQueue = new ConcurrentLinkedQueue<>();
        generalQueue = new ConcurrentLinkedQueue<>();

        while(true) {
            clientSocket = null;
            try {
                System.out.println(LocalDateTime.now() + ": Waiting for client connection...");
                clientSocket = serverSocket.accept();
                System.out.println(LocalDateTime.now() + ": Incoming connection from " + clientSocket.getInetAddress() + " at port " + clientSocket.getPort());
                inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outStream = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            }catch(IOException e) {

            }
        }
    }

    private void loadProperties() {
        try {
            properties = new Properties();
            properties.load(Server.class.getResourceAsStream("config.properties"));
            communicationPort = Integer.parseInt(properties.getProperty("SERVER_COMMUNICATION_PORT"));
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
