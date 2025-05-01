package com.das6.serversockets.server;

import com.das6.serversockets.shared.UserType;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;

public class Ticket {

    private final String code;
    private final String refClient;
    private final UserType type;
    private final LocalDateTime dateCreated;

    private LocalDateTime initService;
    private LocalDateTime endService;

    Ticket(String code, UserType type) {
        this.code = code;
        this.type = type;
        this.refClient = null;
        this.dateCreated = LocalDateTime.now();
    }

    Ticket(String code, UserType type, String refClient) {
        this.code = code;
        this.type = type;
        this.refClient = refClient;
        this.dateCreated = LocalDateTime.now();
    }

    //When a ticket is requested by the client the initService is initialized
    public void markInitService() {
        this.initService = LocalDateTime.now();
    }

    public void markEndService() {
        this.endService = LocalDateTime.now();
    }

    public Duration getWaitDuration() {
        return (initService != null)
                ? Duration.between(dateCreated, initService)
                : Duration.ZERO;
    }

    public Duration getServiceDuration() {
        return (initService != null && endService != null)
                ? Duration.between(initService, endService)
                : Duration.ZERO;
    }

    public JSONObject toJson() {
        JSONObject message = new JSONObject();
        message.put("code", code);
        message.put("type", type.name());
        if(refClient != null) {
            message.put("refClient", refClient);
        }
        message.put("refClient",JSONObject.NULL);
        message.put("dateCreated", dateCreated);
        if(initService != null) {
            message.put("initService", initService);
        }
        return message;
    }

    public String getCode() {
        return code;
    }

    public String getRefClient() {
        return refClient;
    }
}
