using WebSiteRoute.Domain.Entities;

namespace WebSiteRoute.Application.Abstractions;

public interface IAltitudeReadingReader
{
    Task<IReadOnlyCollection<AltitudeReading>> GetReadingsAsync(CancellationToken cancellationToken = default);
}
