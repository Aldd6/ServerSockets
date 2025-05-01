package com.das6.serversockets.server;

import com.das6.serversockets.shared.UserType;

import java.util.Queue;
import java.util.Random;

public class TicketFactory {
    private static final Random randomizer = new Random();

    public static Ticket createTicket(UserType userType) {
        String code;

        do {
            code = generateTicketCode(userType);
        }while(alreadyInQueue(userType,code));

        return new Ticket(code, userType);
    }

    public static Ticket createTicket(UserType userType, String refClient) {
        String code;

        do {
            code = generateTicketCode(userType);
        }while(alreadyInQueue(userType,code));

        return new Ticket(code, userType, refClient);
    }

    private static String generateTicketCode(UserType userType) {
        StringBuilder ticketCode = new StringBuilder();

        ticketCode.append(generateServiceLetter(userType));
        ticketCode.append(generateRandomLetter());
        ticketCode.append(generateRandomNumer());

        return ticketCode.toString();
    }

    private static String generateRandomLetter() {
        return String.valueOf((char)('A' + randomizer.nextInt(26)));
    }

    private static String generateServiceLetter(UserType userType) {
        return switch(userType) {
            case UserType.SERVICE -> "S";
            case UserType.CHECKOUT -> "C";
            default -> throw new IllegalStateException("Unexpected value");
        };
    }

    private static String generateRandomNumer() {
        return String.valueOf(randomizer.nextInt(100));
    }

    private static boolean alreadyInQueue(UserType userType, String code) {
        return QueueManager.getQueueByType(userType).stream().anyMatch(t -> t.getCode().equals(code));
    }
}
