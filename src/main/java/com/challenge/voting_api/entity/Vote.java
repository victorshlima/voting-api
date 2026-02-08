package com.challenge.voting_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
		name = "vote",
		uniqueConstraints = @UniqueConstraint(columnNames = {"voting_session_id", "associate_id"})
)
public class Vote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "voting_session_id", nullable = false)
	private VotingSession votingSession;

	@Column(name = "associate_id", nullable = false)
	private UUID associateId;

	@Column(name = "des_vote", nullable = false)
	private boolean vote;

	@Column(name = "dat_created_at", nullable = false, updatable = false, insertable = false)
	private OffsetDateTime createdAt;

	protected Vote() {
	}

	public Vote(final VotingSession votingSession, final UUID associateId, final boolean vote) {
		this.votingSession = votingSession;
		this.associateId = associateId;
		this.vote = vote;
	}

	public Long getId() {
		return id;
	}

	public VotingSession getVotingSession() {
		return votingSession;
	}

	public UUID getAssociateId() {
		return associateId;
	}

	public boolean isVote() {
		return vote;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
}
