package com.das6.serversockets.services;

import com.das6.serversockets.server.QueueManager;
import com.das6.serversockets.server.Ticket;
import com.das6.serversockets.shared.SocketJsonUtil;
import com.das6.serversockets.shared.StatusCode;
import com.das6.serversockets.shared.UserType;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Queue;

public class CheckoutServices {
    public static void getTicket(BufferedWriter out) {
        try {
            Queue<Ticket> tickets = QueueManager.getQueueByType(UserType.CHECKOUT);
            if(!tickets.isEmpty()) {
                Ticket ticketPolled = tickets.poll();
                ticketPolled.markInitService();
                SocketJsonUtil.send(out, StatusCode.OK.toJsonWithData(ticketPolled.toJson()));
            }else {
                SocketJsonUtil.send(out, StatusCode.NOT_FOUND.toJSON("The queue for checkout is empty"));
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

}
