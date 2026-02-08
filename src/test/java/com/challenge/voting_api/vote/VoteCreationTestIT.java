package com.challenge.voting_api.vote;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.challenge.voting_api.entity.Agenda;
import com.challenge.voting_api.entity.VotingSession;
import com.challenge.voting_api.repository.AgendaRepository;
import com.challenge.voting_api.repository.VotingSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class VoteCreationTestIT {

	private static final String API_VERSION_HEADER = "X-API-Version";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AgendaRepository agendaRepository;

	@Autowired
	private VotingSessionRepository votingSessionRepository;

	@Test
	void shouldCreateVoteWhenSessionIsOpen() throws Exception {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		VotingSession session = createVotingSession(now.minusMinutes(1), now.plusMinutes(5));
		UUID associateId = UUID.randomUUID();

		mockMvc.perform(post("/voting-sessions/{votingSessionId}/votes", session.getId())
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody(Map.of(
								"associateId", associateId,
								"vote", "SIM"
						))))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.votingSessionId").value(session.getId().intValue()))
				.andExpect(jsonPath("$.associateId").value(associateId.toString()))
				.andExpect(jsonPath("$.vote").value("SIM"));
	}

	@Test
	void shouldReturnConflictWhenSessionIsClosed() throws Exception {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		VotingSession session = createVotingSession(now.minusMinutes(10), now.minusMinutes(5));
		UUID associateId = UUID.randomUUID();

		mockMvc.perform(post("/voting-sessions/{votingSessionId}/votes", session.getId())
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody(Map.of(
								"associateId", associateId,
								"vote", "SIM"
						))))
				.andExpect(status().isConflict());
	}

	@Test
	void shouldReturnNotFoundWhenSessionIsMissing() throws Exception {
		UUID associateId = UUID.randomUUID();

		mockMvc.perform(post("/voting-sessions/{votingSessionId}/votes", 9999)
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody(Map.of(
								"associateId", associateId,
								"vote", "SIM"
						))))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldReturnConflictWhenVoteIsDuplicated() throws Exception {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		VotingSession session = createVotingSession(now.minusMinutes(1), now.plusMinutes(5));
		UUID associateId = UUID.randomUUID();
		String payload = jsonBody(Map.of(
				"associateId", associateId,
				"vote", "SIM"
		));

		mockMvc.perform(post("/voting-sessions/{votingSessionId}/votes", session.getId())
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/voting-sessions/{votingSessionId}/votes", session.getId())
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isConflict());
	}

	@Test
	void shouldReturnBadRequestWhenAssociateIdMissing() throws Exception {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		VotingSession session = createVotingSession(now.minusMinutes(1), now.plusMinutes(5));

		mockMvc.perform(post("/voting-sessions/{votingSessionId}/votes", session.getId())
						.header(API_VERSION_HEADER, "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonBody(Map.of(
								"vote", "SIM"
						))))
				.andExpect(status().isBadRequest());
	}

	private VotingSession createVotingSession(OffsetDateTime startsAt, OffsetDateTime endsAt) {
		Agenda agenda = agendaRepository.save(new Agenda("Pauta voto"));
		return votingSessionRepository.save(new VotingSession(agenda, startsAt, endsAt));
	}

	private String jsonBody(Map<String, Object> payload) throws Exception {
		return objectMapper.writeValueAsString(payload);
	}
}
