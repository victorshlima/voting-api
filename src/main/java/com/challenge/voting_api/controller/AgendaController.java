package com.challenge.voting_api.controller;

import com.challenge.voting_api.dto.request.AgendaCreateRequest;
import com.challenge.voting_api.dto.response.AgendaResponse;
import com.challenge.voting_api.entity.Agenda;
import com.challenge.voting_api.repository.AgendaRepository;
import com.challenge.voting_api.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/agendas", headers = "X-API-Version=1")
@Tag(name = "Agendas", description = "Operacoes para gerenciar pautas")
public class AgendaController {

    private final AgendaService agendaService;
    private final AgendaRepository agendaRepository;

    public AgendaController(final AgendaService agendaService, final AgendaRepository agendaRepository) {
        this.agendaService = agendaService;
        this.agendaRepository = agendaRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Criar pauta",
            description = "Cria uma nova pauta para votacao."
    )
    public ResponseEntity<AgendaResponse> create(
            final @Valid @RequestBody AgendaCreateRequest agendaCreateRequest) {
        AgendaResponse response = agendaService.createAgenda(agendaCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Listar pautas",
            description = "Endpoint simples para visualizar todas as pautas com todos os campos (teste)."
    )
    public List<Agenda> list() {
        return agendaRepository.findAll();
    }
}
