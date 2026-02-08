package com.challenge.voting_api.service.impl;

import com.challenge.voting_api.dto.request.AgendaCreateRequest;
import com.challenge.voting_api.dto.response.AgendaResponse;
import com.challenge.voting_api.entity.Agenda;
import com.challenge.voting_api.repository.AgendaRepository;
import com.challenge.voting_api.service.AgendaService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class AgendaServiceImpl implements AgendaService {

	private final AgendaRepository agendaRepository;

	public AgendaServiceImpl(AgendaRepository agendaRepository) {
		this.agendaRepository = agendaRepository;
	}

	@Override
	@Transactional
	public AgendaResponse createAgenda(AgendaCreateRequest request) {
		Agenda saved = agendaRepository.save(
				new Agenda(request.title()));
		log.info("Agenda Created id={} title={}", saved.getId(), saved.getTitle());
		return new AgendaResponse(saved.getId(), saved.getTitle());
	}
}
