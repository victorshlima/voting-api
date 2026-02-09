package com.challenge.voting_api.service.impl;

import com.challenge.voting_api.dto.VoteChoice;
import com.challenge.voting_api.dto.request.VoteCreateRequest;
import com.challenge.voting_api.dto.response.VoteResponse;
import com.challenge.voting_api.entity.Vote;
import com.challenge.voting_api.entity.VotingSession;
import com.challenge.voting_api.exception.GenericAgendaException;
import com.challenge.voting_api.repository.VoteRepository;
import com.challenge.voting_api.repository.VotingSessionRepository;
import com.challenge.voting_api.service.VoteService;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Log4j2
public class VoteServiceImpl implements VoteService {

	private final VoteRepository voteRepository;
	private final VotingSessionRepository votingSessionRepository;

	public VoteServiceImpl(
			final VoteRepository voteRepository,
			final VotingSessionRepository votingSessionRepository
	) {
		this.voteRepository = voteRepository;
		this.votingSessionRepository = votingSessionRepository;
	}

	@Override
	@Transactional
	public VoteResponse create(final Long votingSessionId, final VoteCreateRequest request) {
		try {
			final VotingSession session = votingSessionRepository.findById(votingSessionId)
					.orElseThrow(() -> new ResponseStatusException(
							HttpStatus.NOT_FOUND,
							"Voting session not found"
					));
			validateSessionOpen(session);
			if (voteRepository.existsByVotingSessionIdAndAssociateId(
					session.getId(),
					request.associateId()
			)) {
				throw new ResponseStatusException(
						HttpStatus.CONFLICT,
						"Associate already voted in this session"
				);
			}
			Vote saved = voteRepository.save(
					new Vote(session, request.associateId(), request.vote().toBoolean())
			);
			return new VoteResponse(
					saved.getId(),
					session.getId(),
					request.associateId(),
					VoteChoice.fromBoolean(saved.isVote()));
		} catch (ResponseStatusException | DataIntegrityViolationException exception) {
			log.error("VoteServiceImpl#create - Error in voting process - associateId={}",
					request.associateId(),
					exception);
			throw exception;
		} catch (Exception exception) {
			log.error("VoteServiceImpl#create - Error in voting process - associateId={}",
					request.associateId(),
					exception);
			throw new GenericAgendaException(exception.getMessage());
		}
	}

	private void validateSessionOpen(final VotingSession session) {
		final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		if (now.isBefore(session.getStartsAt()) || !now.isBefore(session.getEndsAt())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Voting session is closed");
		}
	}
}
