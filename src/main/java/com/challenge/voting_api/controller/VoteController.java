package com.challenge.voting_api.controller;

import com.challenge.voting_api.dto.request.VoteCreateRequest;
import com.challenge.voting_api.dto.response.VoteResponse;
import com.challenge.voting_api.service.VoteService;
import com.challenge.voting_api.util.LocationUtils;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/voting-sessions/{votingSessionId}/votes", headers = "X-API-Version=1")
@Tag(name = "Votes", description = "Operacoes para registrar votos em sessoes abertas")
public class VoteController {

	private final VoteService voteService;

	public VoteController(final VoteService voteService) {
		this.voteService = voteService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Registrar voto",
			description = "Registra o voto de um associado em uma sessao aberta.",
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
					description = "Voto registrado",
					headers = {
							@Header(name = "Location", description = "URI do recurso criado")
					},
					content = @Content(
							mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = VoteResponse.class)
					)
			),
			@ApiResponse(responseCode = "400", description = "Dados invalidos"),
			@ApiResponse(responseCode = "404", description = "Sessao nao encontrada"),
			@ApiResponse(responseCode = "409", description = "Sessao encerrada ou voto duplicado")
	})
	public ResponseEntity<VoteResponse> create(
			@Parameter(description = "Identificador da sessao de votacao", example = "1")
			@PathVariable final Long votingSessionId,
			final @Valid @RequestBody VoteCreateRequest request
	) {
		VoteResponse response = voteService.create(votingSessionId, request);
		URI location = LocationUtils.fromCurrentRequestWithId(response.id());
		return ResponseEntity.created(location).body(response);
	}
}
