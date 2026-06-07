using InfluxDB.Client;
using InfluxDB.Client.Api.Domain;
using InfluxDB.Client.Writes;
using WebSiteRoute.Application.Abstractions;
using WebSiteRoute.Domain.Entities;

namespace WebSiteRoute.Infrastructure.InfluxDb;

public sealed class InfluxDbAltitudeReadingRepository(InfluxDbOptions options) : IAltitudeReadingRepository
{
    public async Task<IReadOnlyCollection<AltitudeReading>> GetReadingsAsync(CancellationToken cancellationToken = default)
    {
        using var client = CreateClient();
        var query = client.GetQueryApi();
        var flux = $"from(bucket:\"{options.Bucket}\") |> range(start: 0)";
        var tables = await query.QueryAsync(flux, options.Organization);

        return tables
            .SelectMany(table => table.Records)
            .Select(record => new AltitudeReading(
                ParseTimestamp(record.GetTime()?.ToString()),
                Convert.ToInt32(record.GetValue())))
            .ToArray();
    }

    public Task AddReadingAsync(AltitudeReading reading, CancellationToken cancellationToken = default)
    {
        using var client = CreateClient();
        using var write = client.GetWriteApi();
        var point = PointData.Measurement("altitude")
            .Tag("plane", options.PlaneId)
            .Field("value", reading.Altitude)
            .Timestamp(reading.Timestamp.UtcDateTime, WritePrecision.Ns);

        write.WritePoint(point, options.Bucket, options.Organization);
        return Task.CompletedTask;
    }

    private static DateTimeOffset ParseTimestamp(string? timestamp)
        => DateTimeOffset.TryParse(timestamp, out var parsed) ? parsed : DateTimeOffset.MinValue;

    private InfluxDBClient CreateClient()
    {
        if (string.IsNullOrWhiteSpace(options.Token))
        {
            throw new InvalidOperationException("InfluxDb:Token must be configured before reading or writing altitude telemetry.");
        }

        return InfluxDBClientFactory.Create(options.Url, options.Token);
    }
}
