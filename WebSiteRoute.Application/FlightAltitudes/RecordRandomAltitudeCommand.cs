using WebSiteRoute.Application.Abstractions;
using WebSiteRoute.Domain.Entities;

namespace WebSiteRoute.Application.FlightAltitudes;

public sealed class RecordRandomAltitudeCommand(
    IAltitudeGenerator altitudeGenerator,
    IAltitudeReadingWriter altitudeReadingWriter,
    TimeProvider timeProvider)
{
    public Task ExecuteAsync(CancellationToken cancellationToken = default)
    {
        var reading = new AltitudeReading(timeProvider.GetUtcNow(), altitudeGenerator.NextAltitude());
        return altitudeReadingWriter.AddReadingAsync(reading, cancellationToken);
    }
}
