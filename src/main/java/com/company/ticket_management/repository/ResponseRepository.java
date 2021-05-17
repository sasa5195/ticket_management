package com.company.ticket_management.repository;

import com.company.ticket_management.model.AgentResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<AgentResponse, Long> {
}
