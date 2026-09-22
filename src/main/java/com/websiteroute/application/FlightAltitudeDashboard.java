package com.websiteroute.application;

import com.websiteroute.domain.AltitudeReading;
import java.util.Collection;

public record FlightAltitudeDashboard(Collection<AltitudeReading> readings, String flightStatusMessage, int sampleAltitude) {}
