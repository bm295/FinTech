namespace WebSiteRoute.Domain.Entities;

public sealed record AltitudeReading(DateTimeOffset Timestamp, int Altitude)
{
    public string DisplayText => $"Plane was at altitude {Altitude} ft. at {Timestamp:u}";
}
