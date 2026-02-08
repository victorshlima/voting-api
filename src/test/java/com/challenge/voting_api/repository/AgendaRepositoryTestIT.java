package com.challenge.voting_api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.challenge.voting_api.entity.Agenda;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
class AgendaRepositoryTestIT {

	@Autowired
	private AgendaRepository repository;

	@Test
	void shouldPersistAgenda() {
		Agenda agenda = new Agenda("Pauta JPA");

		Agenda saved = repository.save(agenda);

		assertThat(saved.getId()).isNotNull();
		Agenda stored = repository.findById(saved.getId()).orElseThrow();
		assertThat(stored.getTitle()).isEqualTo("Pauta JPA");
	}
}
