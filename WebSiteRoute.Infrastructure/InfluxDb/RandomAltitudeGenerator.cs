using WebSiteRoute.Application.Abstractions;

namespace WebSiteRoute.Infrastructure.InfluxDb;

public sealed class RandomAltitudeGenerator : IAltitudeGenerator
{
    public int NextAltitude() => Random.Shared.Next(1000, 5000);
}
