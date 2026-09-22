package com.websiteroute.domain;

import org.springframework.stereotype.Component;

@Component
public class PassengerFlightStatusMessages implements FlightStatusMessageProvider {
    @Override public String getStatusMessage(int altitude) {
        if (altitude >= 35000) return "Cruising at a typical passenger-flight altitude.";
        if (altitude >= 10000) return "Climbing or descending within controlled airspace.";
        return "Operating below standard cruising altitude.";
    }
}
