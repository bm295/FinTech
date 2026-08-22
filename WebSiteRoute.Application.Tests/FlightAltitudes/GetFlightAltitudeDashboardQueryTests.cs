using WebSiteRoute.Application.Abstractions;
using WebSiteRoute.Application.FlightAltitudes;
using WebSiteRoute.Domain.Entities;
using WebSiteRoute.Domain.Services;

namespace WebSiteRoute.Application.Tests.FlightAltitudes;

public sealed class GetFlightAltitudeDashboardQueryTests
{
    [Fact]
    public async Task ExecuteAsync_UsesFirstReadingForTheDashboardStatus()
    {
        var readings = new[]
        {
            new AltitudeReading(DateTimeOffset.Parse("2026-01-01T00:00:00Z"), 36_000),
            new AltitudeReading(DateTimeOffset.Parse("2026-01-01T00:01:00Z"), 12_000)
        };
        var query = new GetFlightAltitudeDashboardQuery(
            new StubAltitudeReadingReader(readings),
            new PassengerFlightStatusMessages());

        var result = await query.ExecuteAsync();

        Assert.Same(readings, result.Readings);
        Assert.Equal(36_000, result.SampleAltitude);
        Assert.Equal("Cruising at a typical passenger-flight altitude.", result.FlightStatusMessage);
    }

    [Fact]
    public async Task ExecuteAsync_UsesZeroAltitudeWhenThereAreNoReadings()
    {
        var query = new GetFlightAltitudeDashboardQuery(
            new StubAltitudeReadingReader([]),
            new PassengerFlightStatusMessages());

        var result = await query.ExecuteAsync();

        Assert.Empty(result.Readings);
        Assert.Equal(0, result.SampleAltitude);
        Assert.Equal("Operating below standard cruising altitude.", result.FlightStatusMessage);
    }

    [Fact]
    public async Task ExecuteAsync_PassesCancellationToTheReader()
    {
        using var cancellation = new CancellationTokenSource();
        cancellation.Cancel();
        var reader = new StubAltitudeReadingReader([]);
        var query = new GetFlightAltitudeDashboardQuery(reader, new PassengerFlightStatusMessages());

        await query.ExecuteAsync(cancellation.Token);

        Assert.Equal(cancellation.Token, reader.ReceivedCancellationToken);
    }

    private sealed class StubAltitudeReadingReader(IReadOnlyCollection<AltitudeReading> readings)
        : IAltitudeReadingReader
    {
        public CancellationToken ReceivedCancellationToken { get; private set; }

        public Task<IReadOnlyCollection<AltitudeReading>> GetReadingsAsync(CancellationToken cancellationToken = default)
        {
            ReceivedCancellationToken = cancellationToken;
            return Task.FromResult(readings);
        }
    }
}
