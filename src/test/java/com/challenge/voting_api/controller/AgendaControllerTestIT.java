package com.challenge.voting_api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AgendaControllerTestIT {

	private static final String API_VERSION_HEADER = "X-API-Version";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldCreateAgendaSuccessfully() throws Exception {
		var request = Map.of(
				"title", "Pauta de Teste"
		);

		mockMvc.perform(post("/agendas")
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.agendaId").isNumber())
				.andExpect(jsonPath("$.title").value("Pauta de Teste"))
				.andExpect(jsonPath("$.id").doesNotExist());
	}

	@Test
	void shouldReturn400WhenTitleIsMissing() throws Exception {
		var request = Map.of();

		mockMvc.perform(post("/agendas")
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturn400WhenTitleIsGreaterThan500Chars() throws Exception {
		String titleWith501Chars = "A".repeat(501);
		var request = Map.of(
				"title", titleWith501Chars
		);
		mockMvc.perform(post("/agendas")
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

}
