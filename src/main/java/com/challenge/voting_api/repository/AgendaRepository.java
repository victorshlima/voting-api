package com.challenge.voting_api.repository;

import com.challenge.voting_api.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
