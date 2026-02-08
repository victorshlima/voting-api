package com.challenge.voting_api.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.challenge.voting_api.entity.Agenda;
import com.challenge.voting_api.repository.AgendaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class VotingSessionCreationTestIT {

	private static final String API_VERSION_HEADER = "X-API-Version";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AgendaRepository agendaRepository;

	@Value("${voting-session.default-duration-minutes}")
	private int defaultDurationMinutes;

	@Value("${voting-session.max-duration-minutes}")
	private int maxDurationMinutes;

	@Test
	void shouldCreateVotingSessionWithDefaultDuration() throws Exception {
		Agenda agenda = agendaRepository.save(new Agenda("Pauta default"));

		MvcResult result = mockMvc.perform(post("/voting-sessions")
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody(Map.of("agendaId", agenda.getId()))))
				.andExpect(status().isCreated())
				.andReturn();

		JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
		OffsetDateTime startsAt = OffsetDateTime.parse(json.get("startsAt").asText());
		OffsetDateTime endsAt = OffsetDateTime.parse(json.get("endsAt").asText());

		assertThat(json.get("id").asLong()).isPositive();
		assertThat(json.get("agendaId").asLong()).isEqualTo(agenda.getId());
		assertThat(Duration.between(startsAt, endsAt).toMinutes())
				.isEqualTo(defaultDurationMinutes);
	}

	@Test
	void shouldCreateVotingSessionWithCustomDuration() throws Exception {
		Agenda agenda = agendaRepository.save(new Agenda("Pauta custom"));

		MvcResult result = mockMvc.perform(post("/voting-sessions")
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody(Map.of(
								"agendaId", agenda.getId(),
								"durationMinutes", 10
						))))
				.andExpect(status().isCreated())
				.andReturn();

		JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
		OffsetDateTime startsAt = OffsetDateTime.parse(json.get("startsAt").asText());
		OffsetDateTime endsAt = OffsetDateTime.parse(json.get("endsAt").asText());

		assertThat(Duration.between(startsAt, endsAt).toMinutes()).isEqualTo(10);
	}

	@Test
	void shouldRejectDurationAboveMax() throws Exception {
		Agenda agenda = agendaRepository.save(new Agenda("Pauta max"));

		mockMvc.perform(post("/voting-sessions")
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody(Map.of(
								"agendaId", agenda.getId(),
								"durationMinutes", maxDurationMinutes + 1
						))))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldReturnNotFoundWhenAgendaMissing() throws Exception {
		mockMvc.perform(post("/voting-sessions")
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody(Map.of("agendaId", 9999))))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldReturnConflictWhenSessionAlreadyExists() throws Exception {
		Agenda agenda = agendaRepository.save(new Agenda("Pauta duplicada"));
		String payload = jsonBody(Map.of(
				"agendaId", agenda.getId(),
				"durationMinutes", 5
		));

		mockMvc.perform(post("/voting-sessions")
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/voting-sessions")
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isConflict());
	}

	private String jsonBody(Map<String, Object> payload) throws Exception {
		return objectMapper.writeValueAsString(payload);
	}
}
