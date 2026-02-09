package com.challenge.voting_api.service.impl;

import com.challenge.voting_api.config.VotingSessionProperties;
import com.challenge.voting_api.dto.request.VotingSessionCreateRequest;
import com.challenge.voting_api.dto.response.VotingSessionResponse;
import com.challenge.voting_api.entity.Agenda;
import com.challenge.voting_api.entity.VotingSession;
import com.challenge.voting_api.repository.AgendaRepository;
import com.challenge.voting_api.repository.VotingSessionRepository;
import com.challenge.voting_api.service.VotingSessionService;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Log4j2
public class VotingSessionServiceImpl implements VotingSessionService {

	private final AgendaRepository agendaRepository;
	private final VotingSessionRepository votingSessionRepository;
	private final VotingSessionProperties properties;

	public VotingSessionServiceImpl(
			final AgendaRepository agendaRepository,
			final VotingSessionRepository votingSessionRepository,
			final VotingSessionProperties properties
	) {
		this.agendaRepository = agendaRepository;
		this.votingSessionRepository = votingSessionRepository;
		this.properties = properties;
	}

	@Override
	@Transactional
	public VotingSessionResponse create(final Long agendaId, final VotingSessionCreateRequest request) {
		final Agenda agenda = getAgendaIfIdValid(agendaId);
		final int durationMinutes = resolveDurationMinutes(request.durationMinutes());
		final OffsetDateTime startsAt = OffsetDateTime.now(ZoneOffset.UTC);
		final OffsetDateTime endsAt = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(durationMinutes);
		final VotingSession saved = votingSessionRepository.save(
				new VotingSession(agenda, startsAt, endsAt)
		);
		log.info(
				"Voting session created sessionId={} agendaId={} startsAt={}",
				saved.getId(),
				agenda.getId(),
				startsAt
		);
		return new VotingSessionResponse(
				saved.getId(),
				saved.getStartsAt(),
				saved.getEndsAt()
		);
	}

	private Agenda getAgendaIfIdValid(final Long agendaId) {
		final Agenda agenda = agendaRepository.findById(agendaId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agenda not found"));
		if (votingSessionRepository.existsByAgendaId(agenda.getId())) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"Voting session already exists for agenda"
			);
		}
		return agenda;
	}

	private int resolveDurationMinutes(final Integer durationMinutes) {
		if (durationMinutes == null || durationMinutes < 1) {
			return properties.defaultDurationMinutes();
		} else if (durationMinutes < properties.maxDurationMinutes()) {
			return durationMinutes;
		} else if (durationMinutes > properties.maxDurationMinutes()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Duration must be at most " + properties.maxDurationMinutes() + " minutes"
			);
		}
		return 0;
	}
}
