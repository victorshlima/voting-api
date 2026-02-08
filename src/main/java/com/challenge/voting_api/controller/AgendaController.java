package com.challenge.voting_api.controller;

import com.challenge.voting_api.dto.request.AgendaCreateRequest;
import com.challenge.voting_api.dto.response.AgendaResponse;

import com.challenge.voting_api.service.AgendaService;
import jakarta.validation.Valid;
import java.net.URI;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(path = "/agendas", headers = "X-API-Version=1")
@Log4j2
public class AgendaController {

	private final AgendaService agendaService;

	public AgendaController(final AgendaService agendaService) {
		this.agendaService = agendaService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AgendaResponse> create(final @Valid @RequestBody AgendaCreateRequest agendaCreateRequest) {
		log.info("Creating agenda with title: {}", agendaCreateRequest.title());
		AgendaResponse response = agendaService.createAgenda(agendaCreateRequest);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}
}
