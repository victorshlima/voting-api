package com.challenge.voting_api.service.impl;

import com.challenge.voting_api.dto.request.AgendaCreateRequest;
import com.challenge.voting_api.dto.response.AgendaResponse;
import com.challenge.voting_api.entity.Agenda;
import com.challenge.voting_api.repository.AgendaRepository;
import com.challenge.voting_api.repository.VotingSessionRepository;
import com.challenge.voting_api.service.AgendaService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Log4j2
public class AgendaServiceImpl implements AgendaService {

	private final AgendaRepository agendaRepository;
	private final VotingSessionRepository votingSessionRepository;

	public AgendaServiceImpl(
			final AgendaRepository agendaRepository,
			final VotingSessionRepository votingSessionRepository
	) {
		this.agendaRepository = agendaRepository;
		this.votingSessionRepository = votingSessionRepository;
	}

	@Override
	@Transactional
	public AgendaResponse createAgenda(AgendaCreateRequest request) {
		log.info("AgendaServiceImpl#createAgenda Creating Agenda  title={}", request.title());
		Agenda saved = agendaRepository.save(
				new Agenda(request.title()));
		log.info("AgendaServiceImpl#createAgenda  Agenda Created sessionId={} title={}", saved.getId(), saved.getTitle());
		return new AgendaResponse(saved.getId(), saved.getTitle());
	}

	@Override
	@Transactional
	public void deleteAgenda(final Long agendaId) {
		Agenda agenda = getAgenda(agendaId);
		ensureAgendaNotStarted(agendaId);
		agendaRepository.delete(agenda);
		log.info("AgendaServiceImpl#deleteAgenda Agenda deleted id={}", agendaId);
	}

	private Agenda getAgenda(final Long agendaId) {
		return agendaRepository.findById(agendaId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agenda not found"));
	}

	private void ensureAgendaNotStarted(final Long agendaId) {
		if (votingSessionRepository.existsByAgendaId(agendaId)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Agenda already started");
		}
	}
}
