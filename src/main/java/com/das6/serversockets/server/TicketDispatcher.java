package com.das6.serversockets.server;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.sql.Time;

import com.das6.serversockets.repository.Repository;
import com.das6.serversockets.shared.StatusCode;
import com.das6.serversockets.shared.UserType;
import org.json.JSONObject;

public class TicketDispatcher {
    private static final Map<UserType, Set<ClientHandler>> suscribersByType = new ConcurrentHashMap<>();

    public static void registerClient(UserType type, ClientHandler handler) {
        suscribersByType
                .computeIfAbsent(type, k -> ConcurrentHashMap.newKeySet())
                .add(handler);
    }

    public static void removeClient(UserType type, ClientHandler handler) {
        suscribersByType
                .computeIfPresent(type, (t, set) -> {
                    set.remove(handler);
                    return set.isEmpty() ? null : set;
                });
    }

    public static JSONObject dispatchNewTicket(UserType type, Ticket ticket) {

        HashMap<String,Object> params = new HashMap<>();
        params.put("action_type","new_ticket");

        QueueManager.getQueueByType(type).add(ticket);

        JSONObject response;

        suscribersByType
                .computeIfPresent(type, (t, set) -> {
                    set.forEach(ClientHandler::getUpdatedQueue);
                    return set.isEmpty() ? null : set;
                });

        response = ticket.toJson();

        return StatusCode.OK.toJsonWithData(response,params);
    }

    public static JSONObject dispatchPolledTicket(UserType type) {

        HashMap<String,Object> params = new HashMap<>();
        params.put("action_type","polled_ticket");

        JSONObject response;

        Queue<Ticket> queue = QueueManager.getQueueByType(type);

        Ticket polledTicket = queue.poll();

        if(polledTicket == null) {

            return StatusCode.NOT_FOUND.toJsonWithParams(params);

        }

        response = polledTicket.toJson();

        //Add the polled ticket to the general queue for maintaining it alive in the system and mark its service as initialized
        QueueManager.generalQueue.add(polledTicket);
        polledTicket.markInitService();

        dispatchUpdatedQueue(type);

        //Notify the screen that a new ticket is being polled
        dispatchUpdatedQueue(UserType.SCREEN);

        return StatusCode.OK.toJsonWithData(response,params);

    }

    public static JSONObject dispatchEndedTicket(ClientHandler client, String ticket) {

        HashMap<String,Object> params = new HashMap<>();
        params.put("action_type","finished_ticket");

        Optional<Ticket> ticketToEnd = QueueManager.generalQueue
                .stream().filter(t -> t.getCode().equals(ticket))
                .findFirst();

        JSONObject internalResponse = endTicket(client, ticket);

        if(internalResponse.getInt("status") != 201) {
            return StatusCode.INTERNAL_ERROR.toJsonWithParams(params);
        }


        if(ticketToEnd.isPresent()) {
            params.put("code",ticket);
            return StatusCode.OK.toJsonWithParams(params);
        }else {
            params.put("code",JSONObject.NULL);
            return StatusCode.NOT_FOUND.toJsonWithParams(params);
        }

    }

    public static JSONObject dispatchTransferedTicket(ClientHandler client, String ticket, UserType newType) {

        HashMap<String,Object> params = new HashMap<>();
        params.put("action_type","transfer_ticket");

        Optional<Ticket> ticketToTransfer = QueueManager.generalQueue
                        .stream().filter(t -> t.getCode().equals(ticket))
                        .findFirst();

        JSONObject internalResponse = endTicket(client,ticket);
        if(internalResponse.getInt("status") != 201) {
            return StatusCode.INTERNAL_ERROR.toJsonWithParams(params);
        }

        Ticket newTicket;

        if(ticketToTransfer.isPresent()) {

            String refClient = ticketToTransfer.get().getRefClient();

            if(refClient == null) {
                newTicket = TicketFactory.createTicket(newType);
            }else {
                newTicket = TicketFactory.createTicket(newType, refClient);
            }

            dispatchNewTicket(newType, newTicket);

            params.put("code",newTicket.getCode());
            return StatusCode.OK.toJsonWithParams(params);
        }else {
            params.put("code",JSONObject.NULL);
            return StatusCode.NOT_FOUND.toJsonWithParams(params);
        }
    }

    public static void dispatchUpdatedQueue(UserType type) {
        //Notify all the users from the queue where the ticket has been polled
        suscribersByType
                .computeIfPresent(type, (t, set) -> {
                    set.forEach(ClientHandler::getUpdatedQueue);
                    return set.isEmpty() ? null : set;
                });
    }

    private static LocalTime parseDurationToTime(Duration duration) {
        LocalTime time = LocalTime.MIDNIGHT;
        time = time.plusHours(duration.toHoursPart());
        time = time.plusMinutes(duration.toMinutesPart());
        time = time.plusSeconds(duration.toSecondsPart());
        time = time.plusNanos(duration.toNanosPart());

        return time;
    }

    private static JSONObject endTicket(ClientHandler client, String ticket) {

        JSONObject log = new JSONObject();

        Optional<Ticket> ticketToTransfer = QueueManager.generalQueue
                .stream().filter(t -> t.getCode().equals(ticket))
                .findFirst();

        ticketToTransfer.ifPresent(t -> {
            t.markEndService();
            LocalTime serviceDuration = parseDurationToTime(t.getServiceDuration());

            if(t.getRefClient() != null) {

                log.put("no_user", client.getNoUser());
                log.put("no_ticket", ticket);
                log.put("ref_client", t.getRefClient());
                log.put("time_atention", Time.valueOf(serviceDuration));

            }else {

                log.put("no_user", client.getNoUser());
                log.put("no_ticket", ticket);
                log.put("ref_client", JSONObject.NULL);
                log.put("time_atention", Time.valueOf(serviceDuration));

            }

            QueueManager.generalQueue.remove(t);

            dispatchUpdatedQueue(UserType.SCREEN);
        });

        return Repository.insertLog(log);
    }
}
