package com.challenge.voting_api.service.result;

public enum VotingResultStatus {
	APPROVED("APROVADA"),
	REPROVED("REPROVADA"),
	DRAW("EMPATE");

	private final String label;

	VotingResultStatus(final String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}
}
