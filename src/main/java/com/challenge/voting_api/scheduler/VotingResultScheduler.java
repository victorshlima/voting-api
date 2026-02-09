package com.challenge.voting_api.scheduler;

import com.challenge.voting_api.service.VotingResultService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VotingResultScheduler {

	private final VotingResultService votingResultService;

	public VotingResultScheduler(final VotingResultService votingResultService) {
		this.votingResultService = votingResultService;
	}

	@Scheduled(fixedDelayString = "${voting-result.scheduler-interval}")
	@SchedulerLock(name = "VotingResultScheduler_processClosedSessions")
	public void processClosedSessions() {
		votingResultService.processClosedSessions();
	}
}
