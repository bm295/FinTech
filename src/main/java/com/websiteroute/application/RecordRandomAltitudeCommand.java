package com.websiteroute.application;

import com.websiteroute.domain.AltitudeReading;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

@Service
public class RecordRandomAltitudeCommand {
    private final AltitudeGenerator generator; private final AltitudeReadingRepository repository;
    public RecordRandomAltitudeCommand(AltitudeGenerator generator, AltitudeReadingRepository repository) { this.generator = generator; this.repository = repository; }
    public void execute() { repository.addReading(new AltitudeReading(OffsetDateTime.now(java.time.ZoneOffset.UTC), generator.nextAltitude())); }
}
