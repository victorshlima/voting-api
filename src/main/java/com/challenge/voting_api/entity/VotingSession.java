package com.challenge.voting_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;

@Entity
@Table(
		name = "voting_session",
		uniqueConstraints = @UniqueConstraint(columnNames = "agenda_id")
)
public class VotingSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "agenda_id", nullable = false, unique = true)
	private Agenda agenda;

	@Column(name = "starts_at", nullable = false)
	private OffsetDateTime startsAt;

	@Column(name = "ends_at", nullable = false)
	private OffsetDateTime endsAt;

	@Column(name = "created_at", nullable = false, updatable = false, insertable = false)
	private OffsetDateTime createdAt;

	protected VotingSession() {
	}

	public VotingSession(final Agenda agenda, final  OffsetDateTime startsAt, final OffsetDateTime endsAt) {
		this.agenda = agenda;
		this.startsAt = startsAt;
		this.endsAt = endsAt;
	}

	public Long getId() {
		return id;
	}

	public Agenda getAgenda() {
		return agenda;
	}

	public OffsetDateTime getStartsAt() {
		return startsAt;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public OffsetDateTime getEndsAt() {
		return endsAt;
	}
}

