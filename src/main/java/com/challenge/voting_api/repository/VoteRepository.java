package com.challenge.voting_api.repository;

import com.challenge.voting_api.entity.Vote;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

	interface VoteCounts {
		long getYesVotes();

		long getNoVotes();
	}

	boolean existsByVotingSessionIdAndAssociateId(Long votingSessionId, UUID associateId);

	@Query("""
			select
				coalesce(sum(case when v.vote = true then 1 else 0 end), 0) as yesVotes,
				coalesce(sum(case when v.vote = false then 1 else 0 end), 0) as noVotes
			from Vote v
			where v.votingSession.id = :votingSessionId
			""")
	VoteCounts countVotesBySessionId(@Param("votingSessionId") Long votingSessionId);
}
