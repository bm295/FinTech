package com.websiteroute.application;

import com.websiteroute.domain.FlightStatusMessageProvider;
import org.springframework.stereotype.Service;

@Service
public class GetFlightAltitudeDashboardQuery {
    private final AltitudeReadingRepository repository; private final FlightStatusMessageProvider messages;
    public GetFlightAltitudeDashboardQuery(AltitudeReadingRepository repository, FlightStatusMessageProvider messages) { this.repository = repository; this.messages = messages; }
    public FlightAltitudeDashboard execute() {
        var readings = repository.getReadings(); int altitude = readings.stream().findFirst().map(r -> r.altitude()).orElse(0);
        return new FlightAltitudeDashboard(readings, messages.getStatusMessage(altitude), altitude);
    }
}
