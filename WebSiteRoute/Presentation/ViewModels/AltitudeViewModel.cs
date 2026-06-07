using WebSiteRoute.Domain.Entities;

namespace WebSiteRoute.Presentation.ViewModels;

public sealed class AltitudeViewModel
{
    public required string Time { get; init; }

    public required int Altitude { get; init; }

    public string DisplayText => $"Plane was at altitude {Altitude} ft. at {Time}";

    public static AltitudeViewModel FromReading(AltitudeReading reading)
        => new()
        {
            Time = reading.Timestamp.ToString("u"),
            Altitude = reading.Altitude
        };
}
