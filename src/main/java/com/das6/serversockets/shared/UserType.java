package com.das6.serversockets.shared;

public enum UserType {
    CHECKOUT("Checkout"),
    SERVICE("Client service"),
    KIOSK("Kiosk Screen"),
    SCREEN("Ticket Screen"),
    ADMIN("Administrator Screen");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public static UserType convertTypeFromString(String type) {
        return switch(type.toUpperCase()) {
            case "CHECKOUT" -> CHECKOUT;
            case "SERVICE" -> SERVICE;
            case "KIOSK" -> KIOSK;
            case "SCREEN" -> SCREEN;
            case "ADMIN" -> ADMIN;
            default -> throw new IllegalArgumentException("Invalid user type");
        };
    }
}
