using WebSiteRoute.Application.FlightAltitudes;

namespace WebSiteRoute.Presentation.ViewModels;

public sealed class FlightAltitudeDashboardViewModel
{
    public required IReadOnlyCollection<AltitudeViewModel> Readings { get; init; }

    public required string FlightStatusMessage { get; init; }

    public static FlightAltitudeDashboardViewModel FromDashboard(FlightAltitudeDashboard dashboard)
        => new()
        {
            Readings = dashboard.Readings.Select(AltitudeViewModel.FromReading).ToArray(),
            FlightStatusMessage = dashboard.FlightStatusMessage
        };
}
