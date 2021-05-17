package com.company.ticket_management.repository;

import com.company.ticket_management.model.Ticket;
import com.company.ticket_management.model.TicketPage;
import com.company.ticket_management.model.TicketSearchCriteria;
import com.company.ticket_management.model.TicketStatus;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class TicketCriteriaRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    private final AgentRepository agentRepository;
    private final CustomerRepository customerRepository;

    public TicketCriteriaRepository(EntityManager entityManager, AgentRepository agentRepository, CustomerRepository customerRepository) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
        this.agentRepository = agentRepository;
        this.customerRepository = customerRepository;
    }

    public Page<Ticket> findAllWithFilters(TicketPage ticketPage,
                                           TicketSearchCriteria ticketSearchCriteria) {
        CriteriaQuery<Ticket> criteriaQuery = criteriaBuilder.createQuery(Ticket.class);
        Root<Ticket> ticketRoot = criteriaQuery.from(Ticket.class);
        Predicate predicate = getPredicate(ticketSearchCriteria, ticketRoot);
        criteriaQuery.where(predicate);
        setOrder(ticketPage, criteriaQuery, ticketRoot);

        TypedQuery<Ticket> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(ticketPage.getPageNumber() * ticketPage.getPageSize());
        typedQuery.setMaxResults(ticketPage.getPageSize());

        Pageable pageable = getPageable(ticketPage);

        long ticketsCount = getTicketsCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, ticketsCount);
    }

    private Predicate getPredicate(TicketSearchCriteria ticketSearchCriteria,
                                   Root<Ticket> ticketRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(ticketSearchCriteria.getCustomerId())) {
            predicates.add(
                    criteriaBuilder.equal(ticketRoot.get("customer"),
                            customerRepository.findById(ticketSearchCriteria.getCustomerId()).orElse(null))
            );
        }
        if (Objects.nonNull(ticketSearchCriteria.getAgentId())) {
            predicates.add(
                    criteriaBuilder.equal(ticketRoot.get("agent"),
                            agentRepository.findById(ticketSearchCriteria.getAgentId()).orElse(null))
            );
        }
        if (Objects.nonNull(ticketSearchCriteria.getStatus())) {
            predicates.add(
                    criteriaBuilder.equal(ticketRoot.get("status"),
                            TicketStatus.fromString(ticketSearchCriteria.getStatus()))
            );
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(TicketPage ticketPage,
                          CriteriaQuery<Ticket> criteriaQuery,
                          Root<Ticket> ticketRoot) {
        if (ticketPage.getSortDirection().equals(Sort.Direction.ASC)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(ticketRoot.get(ticketPage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(ticketRoot.get(ticketPage.getSortBy())));
        }
    }

    private Pageable getPageable(TicketPage ticketPage) {
        Sort sort = Sort.by(ticketPage.getSortDirection(), ticketPage.getSortBy());
        return PageRequest.of(ticketPage.getPageNumber(), ticketPage.getPageSize(), sort);
    }

    private long getTicketsCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Ticket> countRoot = countQuery.from(Ticket.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
