using WebSiteRoute.Domain.Entities;

namespace WebSiteRoute.Application.Abstractions;

public interface IAltitudeReadingRepository
{
    Task<IReadOnlyCollection<AltitudeReading>> GetReadingsAsync(CancellationToken cancellationToken = default);

    Task AddReadingAsync(AltitudeReading reading, CancellationToken cancellationToken = default);
}
