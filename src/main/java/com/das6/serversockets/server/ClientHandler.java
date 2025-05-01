package com.das6.serversockets.server;


import com.das6.serversockets.repository.Repository;
import com.das6.serversockets.shared.SocketJsonUtil;
import com.das6.serversockets.shared.StatusCode;
import com.das6.serversockets.shared.UserType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Queue;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private UserType userType;
    private JSONObject user;
    HashMap<String,Object> params;

    ClientHandler(Socket socket, BufferedReader in, BufferedWriter out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.params = new HashMap<>();
    }

    @Override
    public void run() {
        boolean isActiveUser = false;
        try {
            JSONObject credsRequest = SocketJsonUtil.receive(in);
            if(logInRequest(credsRequest) != 401) {
                isActiveUser = true;
                TicketDispatcher.registerClient(this.userType,this);

                SocketJsonUtil.send(out, this.user);
                if(hasAsignedQueue()) {
                    this.getUpdatedQueue();
                }

                while(true) {
                    JSONObject request = SocketJsonUtil.receive(in);
                    handleRequest(request);
                }
            }
        }catch(IOException e) {
            String message = e.getMessage();
            if(message.contains("Connection reset")) {
                if(isActiveUser) {
                    System.out.println(LocalDateTime.now() + ": Connection terminated from " + socket.getInetAddress() + ", "  + e.getMessage());
                    closeClientConnection();
                }else {
                    System.out.println(LocalDateTime.now() + ": Connection suddenly terminated from " + socket.getInetAddress() + ", "  + e.getMessage());
                    closeInternalConnection();
                }
            }else {
                System.out.println(LocalDateTime.now() + ": An error in communication has occurred, session terminated\n" + e);
            }
        }
    }

    private boolean hasAsignedQueue() {
        return this.userType != UserType.ADMIN && this.userType != UserType.KIOSK;
    }

    private int logInRequest(JSONObject request) {

        JSONObject user = Repository.lookUpUserByCredentials(request);
        if(user.getInt("status") == 401) {
            try {
                params.put("action_type","login");
                SocketJsonUtil.send(out, StatusCode.UNAUTHORIZED.toJsonWithParams(params));
                closeInternalConnection();
                return user.getInt("status");
            }catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }

        this.user = user.getJSONObject("data");
        this.userType = UserType.convertTypeFromString(user.getJSONObject("data").getString("type"));
        return user.getInt("status");
    }

    private void closeClientConnection() {
        TicketDispatcher.removeClient(userType,this);
        closeInternalConnection();
    }

    private void closeInternalConnection() {
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

    public void getUpdatedQueue() {
        params.clear();
        Queue<Ticket> actualQueue = QueueManager.getQueueByType(this.userType);

        JSONArray queueJson = new JSONArray();
        actualQueue.forEach(t -> {
            queueJson.put(t.toJson());
        });

        params.put("action_type", "update");

        try {
            if(!queueJson.isEmpty()) {
                SocketJsonUtil.send(out, StatusCode.OK.toJsonWithData(queueJson, params));
            }else {
                SocketJsonUtil.send(out, StatusCode.NOT_FOUND.toJsonWithData(queueJson, params));
            }
        }catch(IOException e) {
            try {
                SocketJsonUtil.send(out, StatusCode.INTERNAL_ERROR.toJSON());
            }catch(IOException ex) {
                System.out.println(ex.getMessage());
            }
            System.out.println(e.getMessage());
        }
    }

    public int getNoUser() {
        return this.user.getInt("no_user");
    }

    public String getName() {
        return this.user.getString("name");
    }

    public String getUsername() {
        return this.user.getString("username");
    }

    public void handleRequest(JSONObject request) {
        switch(this.userType) {
            case UserType.CHECKOUT, UserType.SERVICE -> checkOutAndServiceHandler(request);
            case UserType.KIOSK -> kioskHandler(request);
        }
    }

    private void checkOutAndServiceHandler(JSONObject request) {
        params.clear();
        String action = request.getString("action");
        switch(action) {
            case "get_ticket":
                try {
                    SocketJsonUtil.send(out,
                            TicketDispatcher.dispatchPolledTicket(this.userType));
                }catch(IOException e) {
                    try {
                        params.put("action_type", "polled_ticket");
                        SocketJsonUtil.send(out, StatusCode.INTERNAL_ERROR.toJsonWithParams(params));
                    }catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                    System.out.println(e.getMessage());
                }
                break;
            case "finish_ticket":
                try {
                    SocketJsonUtil.send(out, TicketDispatcher.dispatchEndedTicket(this,
                            request.getString("no_ticket")));
                } catch (IOException e) {
                    try {
                        params.put("action_type", "finished_ticket");
                        SocketJsonUtil.send(out, StatusCode.INTERNAL_ERROR.toJsonWithParams(params));
                    }catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                break;
            case "transfer_ticket":
                try {
                    SocketJsonUtil.send(out, TicketDispatcher.dispatchTransferedTicket(this,
                            request.getString("no_ticket"),
                            UserType.convertTypeFromString(request.getString("new_type"))));
                }catch(IOException e) {
                    try {
                        params.put("action_type", "transfer_ticket");
                        SocketJsonUtil.send(out, StatusCode.INTERNAL_ERROR.toJsonWithParams(params));
                    }catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                break;
            default:
                try {
                    params.put("action_type","default");
                    SocketJsonUtil.send(out, StatusCode.INTERNAL_ERROR.toJsonWithParams(params));
                }catch (IOException e) {
                    System.out.println(e.getMessage());
                }
        }
    }

    private void kioskHandler(JSONObject request) {
        params.clear();
        String action = request.getString("action");
        switch(action) {
            case "new_ticket":
                UserType typeTicket = UserType.convertTypeFromString(request.getString("type"));
                if(request.get("ref_client") != JSONObject.NULL) {
                    String ref = request.getString("ref_client");
                    TicketDispatcher.dispatchNewTicket(typeTicket,
                            TicketFactory.createTicket(typeTicket,ref));
                }else {
                    TicketDispatcher.dispatchNewTicket(typeTicket,
                            TicketFactory.createTicket(typeTicket));
                }
                break;
            default:
                try {
                    params.put("action_type", "default");
                    SocketJsonUtil.send(out, StatusCode.INTERNAL_ERROR.toJsonWithParams(params));
                }catch (IOException e) {
                    System.out.println(e.getMessage());
                }
        }
    }

}
