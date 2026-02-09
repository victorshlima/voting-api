package com.challenge.voting_api.result;

import static org.assertj.core.api.Assertions.assertThat;

import com.challenge.voting_api.entity.Agenda;
import com.challenge.voting_api.entity.Vote;
import com.challenge.voting_api.entity.VotingSession;
import com.challenge.voting_api.repository.AgendaRepository;
import com.challenge.voting_api.repository.VoteRepository;
import com.challenge.voting_api.repository.VotingSessionRepository;
import com.challenge.voting_api.service.VotingResultService;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VotingResultServiceTestIT {

	@Autowired
	private VotingResultService votingResultService;

	@Autowired
	private AgendaRepository agendaRepository;

	@Autowired
	private VotingSessionRepository votingSessionRepository;

	@Autowired
	private VoteRepository voteRepository;

	@Test
	void shouldComputeApprovedResultForClosedSession() {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		VotingSession session = createClosedSession("Pauta aprovada", now);
		addVotes(session, 2, 1);

		votingResultService.processClosedSessions();

		assertResult(session, "APROVADA", 2, 1);
	}

	@Test
	void shouldComputeReprovedResultForClosedSession() {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		VotingSession session = createClosedSession("Pauta reprovada", now);
		addVotes(session, 1, 2);

		votingResultService.processClosedSessions();

		assertResult(session, "REPROVADA", 1, 2);
	}

	@Test
	void shouldComputeDrawResultForClosedSession() {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		VotingSession session = createClosedSession("Pauta empate", now);
		addVotes(session, 1, 1);

		votingResultService.processClosedSessions();

		assertResult(session, "EMPATE", 1, 1);
	}

	private VotingSession createClosedSession(String title, OffsetDateTime now) {
		Agenda agenda = agendaRepository.save(new Agenda(title));
		return votingSessionRepository.save(
				new VotingSession(agenda, now.minusMinutes(10), now.minusMinutes(1))
		);
	}

	private void addVotes(VotingSession session, int yesVotes, int noVotes) {
		for (int i = 0; i < yesVotes; i++) {
			voteRepository.save(new Vote(session, UUID.randomUUID(), true));
		}
		for (int i = 0; i < noVotes; i++) {
			voteRepository.save(new Vote(session, UUID.randomUUID(), false));
		}
	}

	private void assertResult(VotingSession session, String expectedStatus, int expectedYes, int expectedNo) {
		Agenda updated = agendaRepository.findById(session.getAgenda().getId()).orElseThrow();
		assertThat(updated.getResult()).isNotNull();
		assertThat(updated.getResult()).contains(session.getAgenda().getTitle());
		assertThat(updated.getResult()).contains(session.getId().toString());
		assertThat(updated.getResult()).contains(expectedStatus);
		assertThat(updated.getResult()).contains(expectedYes + " votos SIM e " + expectedNo + " votos");
		assertThat(updated.getResultCreatedAt()).isNotNull();
	}
}
