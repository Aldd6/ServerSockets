package com.das6.serversockets.shared;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SocketJsonUtil {
    public static void send(BufferedWriter out, JSONObject message) throws IOException {
        out.write(message.toString());
        out.newLine();
        out.flush();
    }

    public static JSONObject receive(BufferedReader in) throws IOException {
        String jsonLine = in.readLine();

        if(jsonLine != null && !jsonLine.isEmpty()) {
            return new JSONObject(jsonLine);
        }else {
            throw new IOException("Empty message or connection closed");
        }
    }
}
