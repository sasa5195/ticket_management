package com.company.ticket_management.repository;

import com.company.ticket_management.model.Ticket;
import com.company.ticket_management.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByCreatedAtBeforeAndStatus(LocalDate date, TicketStatus status);
}
