package com.challenge.voting_api.repository;

import com.challenge.voting_api.entity.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

	boolean existsByAgendaId(final Long agendaId);
}
