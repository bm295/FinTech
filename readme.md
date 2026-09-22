# WebSiteRoute (Java / Spring Boot)

WebSiteRoute is a Clean Architecture flight-altitude telemetry demo migrated from C#/.NET to Java.

## Structure

- `domain` contains altitude readings and flight-status policy.
- `application` contains use cases, ports, dashboard logic, and the transaction service contract.
- `infrastructure` contains the random generator and InfluxDB repository.
- `presentation` contains Spring MVC endpoints and the five-second scheduled recording task.

## Requirements

- Java 21+ and Maven 3.9+
- InfluxDB credentials for live telemetry persistence

## Run

```bash
mvn spring-boot:run
```

Open `http://localhost:8080`. Build with `mvn package` and run `java -jar target/website-route-1.0.0.jar`.

## Configuration

Set `influxdb.url`, `influxdb.token`, `influxdb.bucket`, `influxdb.organization`, and `influxdb.plane-id` through properties or environment variables. The application also accepts the original Docker-style names (`InfluxDb__Token`, etc.).

## Docker

```bash
docker build -t websiteroute .
docker run --rm -p 8080:8080 -e InfluxDb__Token="<token>" websiteroute
```
