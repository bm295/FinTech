using WebSiteRoute.Application.Abstractions;
using WebSiteRoute.Domain.Entities;

namespace WebSiteRoute.Application.FlightAltitudes;

public sealed class RecordRandomAltitudeCommand(
    IAltitudeGenerator altitudeGenerator,
    IAltitudeReadingRepository altitudeReadingRepository)
{
    public Task ExecuteAsync(CancellationToken cancellationToken = default)
    {
        var reading = new AltitudeReading(DateTimeOffset.UtcNow, altitudeGenerator.NextAltitude());
        return altitudeReadingRepository.AddReadingAsync(reading, cancellationToken);
    }
}
