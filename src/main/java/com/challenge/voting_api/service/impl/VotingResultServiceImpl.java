package com.challenge.voting_api.service.impl;

import com.challenge.voting_api.entity.Agenda;
import com.challenge.voting_api.entity.VotingSession;
import com.challenge.voting_api.repository.AgendaRepository;
import com.challenge.voting_api.repository.VoteRepository;
import com.challenge.voting_api.repository.VotingSessionRepository;
import com.challenge.voting_api.service.VotingResultService;
import com.challenge.voting_api.service.result.VotingResultRule;
import com.challenge.voting_api.service.result.VotingResultStatus;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class VotingResultServiceImpl implements VotingResultService {

	private final VotingSessionRepository votingSessionRepository;
	private final VoteRepository voteRepository;
	private final AgendaRepository agendaRepository;
	private final VotingResultRule votingResultRule;

	public VotingResultServiceImpl(
			final VotingSessionRepository votingSessionRepository,
			final VoteRepository voteRepository,
			final AgendaRepository agendaRepository,
			final VotingResultRule votingResultRule
	) {
		this.votingSessionRepository = votingSessionRepository;
		this.voteRepository = voteRepository;
		this.agendaRepository = agendaRepository;
		this.votingResultRule = votingResultRule;
	}

	@Override
	@Transactional
	public void processClosedSessions() {

			final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
			final List<VotingSession> sessions = votingSessionRepository.findClosedSessionsWithoutResult(now);
			if (sessions.isEmpty()) {
				log.info("No closed sessions found to compute result");
				return;
			}

			for (VotingSession session : sessions) {
				log.info("Computing agenda result sessionId={} status={}", session.getId());
				try {
				final Agenda agenda = session.getAgenda();
				if (agenda.getResult() != null) {
					continue;
				}
				final VoteRepository.VoteCounts voteCounts = voteRepository.countVotesBySessionId(session.getId());
				final long yesVotes = voteCounts.getYesVotes();
				final long noVotes = voteCounts.getNoVotes();
				final VotingResultStatus status = votingResultRule.decide(yesVotes, noVotes);
				final String result = formatResult(agenda, session, status, yesVotes, noVotes);
				agenda.updateResult(result, now);
				agendaRepository.save(agenda);
				log.info("Agenda result computed agendaId={} sessionId={} status={}",
						agenda.getId(),
						session.getId(),
						status);
				} catch (Exception exception) {
					log.error("Error on generation agenda result - sessionId={} ex={}", session.getId(), exception);
					throw exception;
				}
			}
	}

	private String formatResult(
			final Agenda agenda,
			final VotingSession session,
			final VotingResultStatus status,
			final long yesVotes,
			final long noVotes
	) {
		return String.format(
				"Pauta %s – Sessão %d - Resultado:  %s, com %d votos SIM e %d votos NÃO.",
				agenda.getTitle(),
				session.getId(),
				status.label(),
				yesVotes,
				noVotes
		);
	}
}
