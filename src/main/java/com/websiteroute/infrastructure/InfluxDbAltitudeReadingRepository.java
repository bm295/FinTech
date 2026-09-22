package com.websiteroute.infrastructure;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.websiteroute.application.AltitudeReadingRepository;
import com.websiteroute.domain.AltitudeReading;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class InfluxDbAltitudeReadingRepository implements AltitudeReadingRepository {
    private final String url, token, bucket, organization, planeId;
    public InfluxDbAltitudeReadingRepository(@Value("${influxdb.url:https://51h585.stackhero-network.com}") String url, @Value("${influxdb.token:}") String token, @Value("${influxdb.bucket:test-bucket}") String bucket, @Value("${influxdb.organization:organization}") String organization, @Value("${influxdb.plane-id:test-plane}") String planeId) { this.url=url; this.token=token; this.bucket=bucket; this.organization=organization; this.planeId=planeId; }
    private InfluxDBClient client() { if (token.isBlank()) throw new IllegalStateException("influxdb.token must be configured before reading or writing altitude telemetry."); return InfluxDBClientFactory.create(url, token.toCharArray(), organization, bucket); }
    public Collection<AltitudeReading> getReadings() { try (var client=client()) { return client.getQueryApi().query("from(bucket: \"%s\") |> range(start: 0)".formatted(bucket), organization).stream().flatMap(t -> t.getRecords().stream()).map(r -> new AltitudeReading(r.getTime() == null ? OffsetDateTime.MIN : OffsetDateTime.ofInstant(r.getTime(), ZoneOffset.UTC), ((Number) r.getValue()).intValue())).toList(); } }
    public void addReading(AltitudeReading reading) { try (var client=client()) { client.getWriteApiBlocking().writePoint(Point.measurement("altitude").addTag("plane", planeId).addField("value", reading.altitude()).time(reading.timestamp().toInstant(), WritePrecision.NS)); } }
}
