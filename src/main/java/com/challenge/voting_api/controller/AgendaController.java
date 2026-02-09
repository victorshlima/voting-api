package com.challenge.voting_api.controller;

import com.challenge.voting_api.dto.request.AgendaCreateRequest;
import com.challenge.voting_api.dto.response.AgendaResponse;
import com.challenge.voting_api.service.AgendaService;
import com.challenge.voting_api.util.LocationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/agendas", headers = "X-API-Version=1")
@Tag(name = "Agendas", description = "Operacoes para gerenciar pautas")
@Log4j2
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(final AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Criar pauta",
            description = "Cria uma nova pauta para votacao.",
            parameters = {
                    @Parameter(
                            name = "X-API-Version",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Versao da API",
                            schema = @Schema(type = "string"),
                            example = "1")
            }
    )
    public ResponseEntity<AgendaResponse> create(
            final @Valid @RequestBody AgendaCreateRequest agendaCreateRequest) {
        log.info("Creating agenda with title: {}", agendaCreateRequest.title());
        AgendaResponse response = agendaService.createAgenda(agendaCreateRequest);
        URI location = LocationUtils.fromCurrentRequestWithId(response.agendaId());
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(path = "/{agendaId}")
    @Operation(
            summary = "Excluir pauta",
            description = "Remove uma pauta que ainda nao foi iniciada.",
            parameters = {
                    @Parameter(
                            name = "X-API-Version",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Versao da API",
                            schema = @Schema(type = "string"),
                            example = "1")
            }
    )
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da pauta", example = "1")
            @PathVariable final Long agendaId
    ) {
        log.info("Deleting agenda id={}", agendaId);
        agendaService.deleteAgenda(agendaId);
        return ResponseEntity.noContent().build();
    }
}
