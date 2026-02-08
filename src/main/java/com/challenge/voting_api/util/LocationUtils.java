package com.challenge.voting_api.util;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public final class LocationUtils {

	private LocationUtils() {
	}

	public static URI fromCurrentRequestWithId(final Object id) {
		return ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(id)
				.toUri();
	}
}
