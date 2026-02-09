package com.challenge.voting_api.repository;

import com.challenge.voting_api.entity.VotingSession;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

	boolean existsByAgendaId(Long agendaId);

	@Query("""
			select s from VotingSession s
			join fetch s.agenda a
			where s.endsAt <= :time
			  and a.result is null
			""")
	List<VotingSession> findClosedSessionsWithoutResult(
			@Param("time") OffsetDateTime time
	);
}
