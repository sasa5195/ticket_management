package com.company.ticket_management.model;

import lombok.Data;

@Data
public class TicketSearchCriteria {
    private Long agentId;
    private Long customerId;
    private String status;
}
