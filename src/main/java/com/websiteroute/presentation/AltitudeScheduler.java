package com.websiteroute.presentation;

import com.websiteroute.application.RecordRandomAltitudeCommand; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Component;

@Component public class AltitudeScheduler { private final RecordRandomAltitudeCommand command; public AltitudeScheduler(RecordRandomAltitudeCommand command) { this.command=command; } @Scheduled(fixedRate=5000) public void record() { command.execute(); } }
