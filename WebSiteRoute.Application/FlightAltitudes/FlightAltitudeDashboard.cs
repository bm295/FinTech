using WebSiteRoute.Domain.Entities;

namespace WebSiteRoute.Application.FlightAltitudes;

public sealed record FlightAltitudeDashboard(
    IReadOnlyCollection<AltitudeReading> Readings,
    string FlightStatusMessage,
    int SampleAltitude);
