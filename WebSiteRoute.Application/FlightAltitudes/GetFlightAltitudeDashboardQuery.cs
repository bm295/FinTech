using WebSiteRoute.Application.Abstractions;
using WebSiteRoute.Domain.Services;

namespace WebSiteRoute.Application.FlightAltitudes;

public sealed class GetFlightAltitudeDashboardQuery(
    IAltitudeReadingReader altitudeReadingReader,
    IFlightStatusMessageProvider flightStatusMessageProvider)
{
    public async Task<FlightAltitudeDashboard> ExecuteAsync(CancellationToken cancellationToken = default)
    {
        var readings = await altitudeReadingReader.GetReadingsAsync(cancellationToken);
        var sampleAltitude = readings.FirstOrDefault()?.Altitude ?? 0;
        var message = flightStatusMessageProvider.GetStatusMessage(sampleAltitude);

        return new FlightAltitudeDashboard(readings, message, sampleAltitude);
    }
}
