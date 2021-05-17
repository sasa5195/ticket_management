package com.company.ticket_management.scheduler;

import com.company.ticket_management.service.TicketService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private final TicketService ticketService;

    public ScheduledTasks(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Scheduled(cron = "${scheduler.resolve_status_task_time}")
    public void updateAllOldTicketStatusToResolved() {
        ticketService.updateAllOldTicketStatusToResolved();
    }

    @Scheduled(cron = "${scheduler.resolve_status_task_time}")
    public void updateAgentsMaxTicketCount() {
        ticketService.updateAgentsMaxTicketCount();
    }
}
