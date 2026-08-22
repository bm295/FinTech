using WebSiteRoute.Domain.Entities;

namespace WebSiteRoute.Application.Abstractions;

public interface IAltitudeReadingWriter
{
    Task AddReadingAsync(AltitudeReading reading, CancellationToken cancellationToken = default);
}
