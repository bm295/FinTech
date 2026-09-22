package com.websiteroute.domain;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public record AltitudeReading(OffsetDateTime timestamp, int altitude) {
    public String displayText() {
        return "Plane was at altitude %d ft. at %s".formatted(altitude, timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss'Z'")));
    }
}
