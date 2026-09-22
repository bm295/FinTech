package com.websiteroute.application;

import com.websiteroute.domain.AltitudeReading;
import java.util.Collection;

public interface AltitudeReadingRepository {
    Collection<AltitudeReading> getReadings();
    void addReading(AltitudeReading reading);
}
