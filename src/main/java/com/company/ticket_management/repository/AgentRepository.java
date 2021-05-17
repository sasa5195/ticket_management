package com.company.ticket_management.repository;

import com.company.ticket_management.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Agent findFirstByOrderByAssignedCountAscIdAsc();
}
