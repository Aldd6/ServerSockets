package com.das6.serversockets.server;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import com.das6.serversockets.shared.UserType;

public class QueueManager {
    public static final Queue<Ticket>checkOutQueue = new ConcurrentLinkedQueue<>();
    public static final Queue<Ticket>frontDeskQueue = new ConcurrentLinkedQueue<>();
    public static final Queue<Ticket>generalQueue = new ConcurrentLinkedDeque<>();

    public static Queue<Ticket> getQueueByType(UserType type) {
        return switch(type) {
            case UserType.CHECKOUT -> checkOutQueue;
            case UserType.SERVICE -> frontDeskQueue;
            case UserType.SCREEN -> generalQueue;
            default -> throw new IllegalStateException("Unexpected Type");
        };
    }
}
