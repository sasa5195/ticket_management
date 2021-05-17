package com.company.ticket_management.controller;

import com.company.ticket_management.model.*;
import com.company.ticket_management.service.ResponseService;
import com.company.ticket_management.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;
    private final ResponseService responseService;

    public TicketController(TicketService ticketService, ResponseService responseService) {
        this.ticketService = ticketService;
        this.responseService = responseService;
    }

    @PostMapping
    public ResponseEntity<Ticket> addTicket(@RequestBody TicketDTO ticket) {
        Ticket addedTicket = ticketService.addTicket(ticket);
        HttpStatus httpStatus = addedTicket != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(addedTicket, httpStatus);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return new ResponseEntity<>(ticketService.getAllTickets(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Ticket>> getTickets(TicketPage ticketPage,
                                                   TicketSearchCriteria ticketSearchCriteria) {
        return new ResponseEntity<>(ticketService.getTickets(ticketPage, ticketSearchCriteria),
                HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicketById(id);
        HttpStatus httpStatus = ticket != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(ticket, httpStatus);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Ticket> updateTicketById(@PathVariable Long id, @RequestBody TicketDTO updatingTicket) {
        Ticket updatedTicket = ticketService.updateTicketBy(id, updatingTicket);
        HttpStatus httpStatus = updatedTicket != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(updatedTicket, httpStatus);
    }


    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteTicketById(@PathVariable Long id) {
        ticketService.deleteTicketById(id);
        return new ResponseEntity<>("Deleted Successfully!", HttpStatus.OK);
    }

    @PutMapping(path = "/{id}/status")
    public ResponseEntity<Ticket> updateTicketStatusById(@PathVariable Long id, @RequestParam String status) {
        Ticket updatedTicket = ticketService.updateTicketStatusBy(id, status);
        HttpStatus httpStatus = updatedTicket != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(updatedTicket, httpStatus);
    }

    @PutMapping(path = "/{id}/assignagent")
    public ResponseEntity<Ticket> updateAgentByTicketId(@PathVariable Long id, @RequestParam Long agentId) {
        Ticket updatedTicket = ticketService.updateAgentByTicketId(id, agentId);
        HttpStatus httpStatus = updatedTicket != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(updatedTicket, httpStatus);
    }

    @PostMapping(path = "{id}/response")
    public ResponseEntity<AgentResponse> addTicket(@PathVariable Long id, @RequestBody ResponseDTO responseDTO) {
        return new ResponseEntity<>(responseService.addResponse(id, responseDTO), HttpStatus.CREATED);
    }

    @GetMapping(path = "/closeoldticket")
    public void closeOldTicket() {
        ticketService.updateAllOldTicketStatusToResolved();
    }


}
