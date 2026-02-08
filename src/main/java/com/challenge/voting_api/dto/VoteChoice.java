package com.challenge.voting_api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.text.Normalizer;
import java.util.Locale;

public enum VoteChoice {
	SIM(true),
	NAO(false);

	private final boolean value;

	VoteChoice(final boolean value) {
		this.value = value;
	}

	public boolean toBoolean() {
		return value;
	}

	public static VoteChoice fromBoolean(final boolean value) {
		return value ? SIM : NAO;
	}

	@JsonCreator
	public static VoteChoice fromString(final String value) {
		if (value == null) {
			return null;
		}
		String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.trim()
				.toUpperCase(Locale.ROOT);
		if ("SIM".equals(normalized)) {
			return SIM;
		}
		if ("YES".equals(normalized)) {
			return SIM;
		}
		if ("NAO".equals(normalized)) {
			return NAO;
		}
		if ("NO".equals(normalized)) {
			return NAO;
		}
		throw new IllegalArgumentException("Invalid vote value: " + value);
	}

	@JsonValue
	public String toJson() {
		return name();
	}
}
