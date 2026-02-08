package com.challenge.voting_api.controller;

import com.challenge.voting_api.dto.request.VotingSessionCreateRequest;
import com.challenge.voting_api.dto.response.VotingSessionResponse;
import com.challenge.voting_api.service.VotingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(path = "/voting-sessions", headers = "X-API-Version=1")
@Tag(name = "Voting Sessions", description = "Operacoes para gerenciar sessoes de votacao")
public class VotingSessionController {

	private final VotingSessionService votingSessionService;

	public VotingSessionController(VotingSessionService votingSessionService) {
		this.votingSessionService = votingSessionService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Criar sessao de votacao",
			description = "Abre uma sessao de votacao para uma pauta por um periodo de minutos.",
			parameters = {
					@Parameter(
							name = "X-API-Version",
							in = ParameterIn.HEADER,
							required = true,
							description = "Versao da API",
							schema = @Schema(type = "string"),
							example = "1"
					)
			}
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "201",
					description = "Sessao criada",
					headers = {
							@Header(name = "Location", description = "URI do recurso criado")
					},
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = VotingSessionResponse.class)
					)
			),
			@ApiResponse(responseCode = "400", description = "Dados invalidos"),
			@ApiResponse(responseCode = "404", description = "Pauta nao encontrada"),
			@ApiResponse(responseCode = "409", description = "Sessao ja existe para a pauta")
	})
	public ResponseEntity<VotingSessionResponse> create(
			final @Valid @RequestBody VotingSessionCreateRequest request
	) {
		VotingSessionResponse response = votingSessionService.create(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}
}
