package com.challenge.voting_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "agenda")
public class Agenda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "des_title", nullable = false, length = 500)
	private String title;

	@Column(name = "result", length = 500)
	private String result;

	@Column(name = "dat_create_result")
	private OffsetDateTime resultCreatedAt;

	@Column(name = "created_at", nullable = false, updatable = false, insertable = false)
	private OffsetDateTime createdAt;

	public Agenda() {
	}

	public Agenda(String title) {
		this.title = title;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getResult() {
		return result;
	}

	public OffsetDateTime getResultCreatedAt() {
		return resultCreatedAt;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void updateResult(final String result, final OffsetDateTime resultCreatedAt) {
		this.result = result;
		this.resultCreatedAt = resultCreatedAt;
	}
}
