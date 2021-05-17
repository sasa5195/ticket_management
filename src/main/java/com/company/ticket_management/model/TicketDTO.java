package com.company.ticket_management.model;

import lombok.Data;

@Data
public class TicketDTO {
    private String type;
    private String description;
    private String createdBy;
    private Long customerId;
    private Long agentId;
    private Long priority;
    private String status;
}
