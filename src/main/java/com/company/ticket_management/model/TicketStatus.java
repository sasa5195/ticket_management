package com.company.ticket_management.model;

import java.util.Arrays;

public enum TicketStatus {
    OPEN("open"),
    WAITING_ON_CUSTOMER("waiting on customer"),
    CUSTOMER_RESPONDED("customer responded"),
    RESOLVED("resolved"),
    CLOSED("closed");

    private final String value;

    TicketStatus(String value) {
        this.value = value;
    }

    public static TicketStatus fromString(String s) throws IllegalArgumentException {
        return Arrays.stream(TicketStatus.values())
                .filter(v -> v.value.equals(s))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown value: " + s));
    }
}
