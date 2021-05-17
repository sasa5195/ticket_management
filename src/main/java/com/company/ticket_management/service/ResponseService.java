package com.company.ticket_management.service;

import com.company.ticket_management.model.*;
import com.company.ticket_management.repository.ResponseRepository;
import com.company.ticket_management.repository.TicketRepository;
import com.company.ticket_management.repository.AgentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResponseService {
    private final ResponseRepository responseRepository;
    private final TicketRepository ticketRepository;
    private final AgentRepository agentRepository;
    private final EmailService emailService;

    public ResponseService(ResponseRepository responseRepository, TicketRepository ticketRepository, AgentRepository agentRepository, EmailService emailService) {
        this.responseRepository = responseRepository;
        this.ticketRepository = ticketRepository;
        this.agentRepository = agentRepository;
        this.emailService = emailService;
    }

    public AgentResponse addResponse(Long ticketId, ResponseDTO responseDTO) {
        Optional<Agent> agent = agentRepository.findById(responseDTO.getAgentId());
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isPresent() && agent.isPresent()) {
            AgentResponse agentResponse = new AgentResponse();
            agentResponse.setTicket(ticket.get());
            agentResponse.setAgent(agent.get());
            agentResponse.setResponse(responseDTO.getResponse());
            AgentResponse addedResponse = responseRepository.save(agentResponse);

            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setTo(ticket.get().getCustomer().getEmail());
            emailRequest.setSubject("RESPONSE FROM GROW");
            emailRequest.setBody(addedResponse.getResponse());

            emailService.sendMail(emailRequest);

            return addedResponse;
        }
        return null;
    }
}
