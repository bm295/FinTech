# WebSiteRoute (.NET 10 / C# 14 Demo)

This repository uses a Clean Architecture layout for a small ASP.NET Core MVC flight-altitude telemetry demo.

## Project structure

- `WebSiteRoute.Domain` contains enterprise rules and entities, including altitude readings and flight-status message policy.
- `WebSiteRoute.Application` contains use cases and ports, including dashboard queries and altitude-recording commands.
- `WebSiteRoute.Infrastructure` contains adapters for external systems, including InfluxDB persistence and random altitude generation.
- `WebSiteRoute` is the presentation/composition project. It contains MVC controllers, Razor views, scheduled tasks, and dependency injection wiring.

Dependencies point inward: Presentation references Application/Domain/Infrastructure for composition, Infrastructure implements Application ports, Application references Domain, and Domain has no external project references.

## Prerequisites

- .NET 10 SDK
- (Optional) Docker, if you want to run in a container
- InfluxDB credentials supplied through configuration or user secrets

## Configuration

The app reads InfluxDB settings from the `InfluxDb` configuration section:

```json
{
  "InfluxDb": {
    "Url": "https://51h585.stackhero-network.com",
    "Token": "<set with user secrets or environment variables>",
    "Bucket": "test-bucket",
    "Organization": "organization",
    "PlaneId": "test-plane"
  }
}
```

For local development, set the token with user secrets:

```bash
dotnet user-secrets set "InfluxDb:Token" "<token>" --project WebSiteRoute/WebSiteRoute.csproj
```

Azure App Configuration is optional. When `AzureAppConfiguration:ConnectionString` or `AzureAppConfiguration:Endpoint` is set, the web app adds Azure App Configuration as a configuration source and enables request-driven refresh. Microsoft Entra ID via the startup-provided `DefaultAzureCredential` is used when configuring an endpoint.

```json
{
  "AzureAppConfiguration": {
    "Enabled": true,
    "Endpoint": "https://<store-name>.azconfig.io",
    "ConnectionString": "<optional connection string>",
    "KeyFilter": "*",
    "Label": null,
    "RefreshIntervalSeconds": 30
  }
}
```

## Run locally

From the repository root:

```bash
dotnet restore WebSiteRoute.sln
dotnet run --project WebSiteRoute/WebSiteRoute.csproj
```

Then open the URL shown in the terminal (for example `https://localhost:xxxx`).

## Clean Architecture flow

1. `HomeController` asks the application layer for a flight-altitude dashboard.
2. `GetFlightAltitudeDashboardQuery` loads readings through the `IAltitudeReadingRepository` port and applies the domain flight-status policy.
3. `InfluxDbAltitudeReadingRepository` implements the persistence port using the InfluxDB client.
4. `RecordRandomAltitudeTask` is the Coravel scheduled task adapter that invokes `RecordRandomAltitudeCommand`.

## Run with Docker

```bash
docker build -t websiteroute .
docker run --rm -p 8080:80 -e PORT=80 -e InfluxDb__Token="<token>" websiteroute
```

Then browse to `http://localhost:8080`.
