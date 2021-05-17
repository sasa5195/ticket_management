package com.company.ticket_management.service;

import com.company.ticket_management.model.*;
import com.company.ticket_management.repository.AgentRepository;
import com.company.ticket_management.repository.CustomerRepository;
import com.company.ticket_management.repository.TicketCriteriaRepository;
import com.company.ticket_management.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketCriteriaRepository ticketCriteriaRepository;
    private final AgentRepository agentRepository;
    private final CustomerRepository customerRepository;

    public TicketService(TicketRepository ticketRepository,
                         TicketCriteriaRepository ticketCriteriaRepository, AgentRepository agentRepository, CustomerRepository customerRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketCriteriaRepository = ticketCriteriaRepository;
        this.agentRepository = agentRepository;
        this.customerRepository = customerRepository;
    }

    public Ticket addTicket(TicketDTO ticketDTO) {
        Optional<Customer> customer = customerRepository.findById(ticketDTO.getCustomerId());
        if (customer.isPresent()) {
            if (ticketDTO.getAgentId() != null) {
                Optional<Agent> agent = agentRepository.findById(ticketDTO.getAgentId());
                if (agent.isPresent()) {

                    Agent assigningAgent = agent.get();
                    assigningAgent.setAssignedCount(assigningAgent.getAssignedCount() + 1);
                    Agent assignedAgent = agentRepository.save(assigningAgent);

                    Ticket ticket = new Ticket();
                    assignTicketDTOToTicket(ticketDTO, ticket, customer.get(), assignedAgent);
                    return ticketRepository.save(ticket);
                } else {
                    return null;
                }
            } else {
                Agent assigningAgent = getNewAgentForTicket();
                assigningAgent.setAssignedCount(assigningAgent.getAssignedCount() + 1);
                Agent assignedAgent = agentRepository.save(assigningAgent);
                Ticket ticket = new Ticket();
                assignTicketDTOToTicket(ticketDTO, ticket, customer.get(), assignedAgent);
                return ticketRepository.save(ticket);
            }
        }
        return null;
    }

    private Agent getNewAgentForTicket() {
        return agentRepository.findFirstByOrderByAssignedCountAscIdAsc();
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Page<Ticket> getTickets(TicketPage ticketPage,
                                   TicketSearchCriteria ticketSearchCriteria) {
        return ticketCriteriaRepository.findAllWithFilters(ticketPage, ticketSearchCriteria);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public Ticket updateTicketBy(Long id, TicketDTO updatingTicket) {
        Optional<Ticket> currentTicket = ticketRepository.findById(id);
        Optional<Customer> customer = customerRepository.findById(updatingTicket.getCustomerId());
        Optional<Agent> agent = agentRepository.findById(updatingTicket.getAgentId());
        if (currentTicket.isPresent() && customer.isPresent() && agent.isPresent()) {
            Ticket ticket = currentTicket.get();
            Agent currentAgent = agent.get();
            if (!ticket.getAgent().equals(currentAgent)) {
                currentAgent.setAssignedCount(currentAgent.getAssignedCount() + 1);
                Agent updatedAgent = agentRepository.save(currentAgent);
                assignTicketDTOToTicket(updatingTicket, ticket, customer.get(), updatedAgent);
            } else {
                assignTicketDTOToTicket(updatingTicket, ticket, customer.get(), currentAgent);
            }
        }
        return null;
    }

    private void assignTicketDTOToTicket(TicketDTO ticketDTO, Ticket ticket, Customer customer, Agent agent) {
        ticket.setType(ticketDTO.getType());
        ticket.setDescription(ticketDTO.getDescription());
        ticket.setCreatedBy(ticketDTO.getCreatedBy());
        ticket.setCreatedAt(LocalDate.now());
        ticket.setCustomer(customer);
        ticket.setAgent(agent);
        ticket.setPriority(ticketDTO.getPriority());
        ticket.setStatus(TicketStatus.fromString(ticketDTO.getStatus()));
    }

    public void deleteTicketById(Long id) {
        ticketRepository.deleteById(id);
    }

    public Ticket updateTicketStatusBy(Long id, String status) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            Ticket updatedTicket = ticket.get();
            updatedTicket.setStatus(TicketStatus.fromString(status));
            return ticketRepository.save(updatedTicket);
        }
        return null;
    }

    public Ticket updateAgentByTicketId(Long id, Long agentId) {
        Optional<Agent> agent = agentRepository.findById(agentId);
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent() && agent.isPresent()) {

            Agent updatingAgent = agent.get();
            updatingAgent.setAssignedCount(updatingAgent.getAssignedCount() + 1);
            Agent updatedAgent = agentRepository.save(updatingAgent);
            Ticket updatingTicket = ticket.get();
            updatingTicket.setAgent(updatedAgent);
            return ticketRepository.save(updatingTicket);
        }
        return null;
    }

    public void updateAllOldTicketStatusToResolved() {
        List<Ticket> allOldResolvedTickets = ticketRepository.findAllByCreatedAtBeforeAndStatus(LocalDate.now().minus(Period.ofDays(30)), TicketStatus.RESOLVED);
        allOldResolvedTickets.forEach(ticket -> {
            ticket.setStatus(TicketStatus.CLOSED);
            ticketRepository.save(ticket);
        });
    }

    public void updateAgentsMaxTicketCount() {
        agentRepository.findAll().forEach(agent -> {
            agent.setAssignedCount(0);
            agentRepository.save(agent);
        });
    }
}
