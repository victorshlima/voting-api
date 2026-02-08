package com.challenge.voting_api.repository;

import com.challenge.voting_api.entity.Vote;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

	boolean existsByVotingSessionIdAndAssociateId(final Long votingSessionId, final UUID associateId);
}
