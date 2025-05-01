package com.das6.serversockets.shared;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public enum StatusCode {
    OK(200, "Successful Operation"),
    CREATED(201, "Created Resource"),
    BAD_REQUEST(400, "Malformed Request"),
    UNAUTHORIZED(401, "Not Authorized"),
    NOT_FOUND(404, "Resource Not Found"),
    INTERNAL_ERROR(500, "Internal Server Error");

    private final int code;
    private final String defaultMessage;

    StatusCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int getCode() {
        return this.code;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    public JSONObject toJSON() {
        JSONObject response = new JSONObject();
        response.put("status", this.code);
        response.put("message", this.defaultMessage);
        return response;
    }

    public JSONObject toJSON(String message) {
        JSONObject response = new JSONObject();
        response.put("status", this.code);
        response.put("message", message);
        return response;
    }

    public JSONObject toJsonWithData(JSONObject data) {
        JSONObject response = toJSON();
        response.put("data", data);
        return response;
    }

    public JSONObject toJsonWithData(JSONArray data) {
        JSONObject response = toJSON();
        response.put("data", data);
        return response;
    }

    public JSONObject toJsonWithData(JSONArray data, HashMap<String,Object> parameters) {
        JSONObject response = toJSON();
        parameters.forEach(response::put);
        response.put("data", data);
        return response;
    }

    public JSONObject toJsonWithData(JSONObject data, HashMap<String,Object> parameters) {
        JSONObject response = toJSON();
        parameters.forEach(response::put);
        response.put("data", data);
        return response;
    }

    public JSONObject toJsonWithParams(HashMap<String,Object> parameters) {
        JSONObject response = toJSON();
        parameters.forEach(response::put);
        return response;
    }
}
