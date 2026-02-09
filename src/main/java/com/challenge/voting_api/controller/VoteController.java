package com.challenge.voting_api.controller;

import com.challenge.voting_api.dto.request.VoteCreateRequest;
import com.challenge.voting_api.dto.request.VoteCreateTestRequest;
import com.challenge.voting_api.dto.response.VoteResponse;
import com.challenge.voting_api.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/voting-sessions", headers = "X-API-Version=1")
@Tag(name = "Votes", description = "Operacoes para registrar votos em sessoes abertas")
public class VoteController {

	private final VoteService voteService;

	public VoteController(final VoteService voteService) {
		this.voteService = voteService;
	}

	@PostMapping(path = "/{votingSessionId}/votes", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Registrar voto",
			description = "Registra o voto de um associado em uma sessao aberta."
	)
	public ResponseEntity<VoteResponse> create(
			@Parameter(description = "ID da sessao de votacao", example = "1")
			@PathVariable final Long votingSessionId,
			final @Valid @RequestBody VoteCreateRequest request
	) {
		VoteResponse response = voteService.create(votingSessionId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping(path = "/{votingSessionId}/votes/test", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Registrar voto (UUID gerado)",
			description = "Registra voto com UUID gerado automaticamente para testes."
	)
	public ResponseEntity<VoteResponse> createWithGeneratedAssociateId(
			@Parameter(description = "ID da sessao de votacao", example = "1")
			@PathVariable final Long votingSessionId,
			final @Valid @RequestBody VoteCreateTestRequest request
	) {
		VoteResponse response = voteService.create(
				votingSessionId,
				new VoteCreateRequest(UUID.randomUUID(), request.vote())
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
