package com.challenge.voting_api.controller;

import com.challenge.voting_api.dto.request.VotingSessionCreateRequest;
import com.challenge.voting_api.dto.response.VotingSessionOpenResponse;
import com.challenge.voting_api.dto.response.VotingSessionResponse;
import com.challenge.voting_api.service.VotingSessionService;
import com.challenge.voting_api.util.LocationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(path = "/voting-sessions", headers = "X-API-Version=1")
@Tag(name = "Voting Sessions", description = "Operacoes para gerenciar sessoes de votacao")
public class VotingSessionController {

	private final VotingSessionService votingSessionService;

	public VotingSessionController(VotingSessionService votingSessionService) {
		this.votingSessionService = votingSessionService;
	}

	@PostMapping(
			path = "/{agendaId}",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	@Operation(
			summary = "Criar sessao de votacao",
			description = "Abre uma sessao de votacao para uma pauta por um periodo de minutos."
	)
	public ResponseEntity<VotingSessionResponse> create(
            final @Valid @RequestBody VotingSessionCreateRequest request,
			@Parameter(description = "ID da pauta", example = "1")
			@PathVariable final Long agendaId) {
		VotingSessionResponse response = votingSessionService.create(agendaId, request);
		URI location = LocationUtils.fromContextPathWithPath("/voting-sessions/{id}", response.sessionId());
		return ResponseEntity.created(location).body(response);
	}

	@GetMapping(path = "/open")
	public ResponseEntity<List<VotingSessionOpenResponse>> listOpenSessions() {
		return ResponseEntity.ok(votingSessionService.listOpenSessions());
	}
}
