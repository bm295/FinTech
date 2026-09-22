package com.websiteroute.infrastructure;

import com.websiteroute.application.AltitudeGenerator;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class RandomAltitudeGenerator implements AltitudeGenerator { public int nextAltitude() { return ThreadLocalRandom.current().nextInt(1000, 5000); } }
