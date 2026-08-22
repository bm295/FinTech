using WebSiteRoute.Application.Abstractions;
using WebSiteRoute.Application.FlightAltitudes;
using WebSiteRoute.Domain.Entities;

namespace WebSiteRoute.Application.Tests.FlightAltitudes;

public sealed class RecordRandomAltitudeCommandTests
{
    [Fact]
    public async Task ExecuteAsync_StoresTheGeneratedAltitude()
    {
        var writer = new RecordingAltitudeReadingWriter();
        var before = DateTimeOffset.UtcNow;
        var command = new RecordRandomAltitudeCommand(
            new StubAltitudeGenerator(4_321),
            writer,
            TimeProvider.System);

        await command.ExecuteAsync();

        var after = DateTimeOffset.UtcNow;
        Assert.NotNull(writer.AddedReading);
        Assert.Equal(4_321, writer.AddedReading.Altitude);
        Assert.InRange(writer.AddedReading.Timestamp, before, after);
    }

    [Fact]
    public async Task ExecuteAsync_PassesCancellationToTheWriter()
    {
        using var cancellation = new CancellationTokenSource();
        cancellation.Cancel();
        var writer = new RecordingAltitudeReadingWriter();
        var command = new RecordRandomAltitudeCommand(
            new StubAltitudeGenerator(4_321),
            writer,
            TimeProvider.System);

        await command.ExecuteAsync(cancellation.Token);

        Assert.Equal(cancellation.Token, writer.ReceivedCancellationToken);
    }

    [Fact]
    public async Task ExecuteAsync_UsesTheProvidedClock()
    {
        var expectedTimestamp = DateTimeOffset.Parse("2026-02-03T04:05:06Z");
        var writer = new RecordingAltitudeReadingWriter();
        var command = new RecordRandomAltitudeCommand(
            new StubAltitudeGenerator(4_321),
            writer,
            new FixedTimeProvider(expectedTimestamp));

        await command.ExecuteAsync();

        Assert.Equal(expectedTimestamp, writer.AddedReading?.Timestamp);
    }

    private sealed class StubAltitudeGenerator(int altitude) : IAltitudeGenerator
    {
        public int NextAltitude() => altitude;
    }

    private sealed class FixedTimeProvider(DateTimeOffset timestamp) : TimeProvider
    {
        public override DateTimeOffset GetUtcNow() => timestamp;
    }

    private sealed class RecordingAltitudeReadingWriter : IAltitudeReadingWriter
    {
        public AltitudeReading? AddedReading { get; private set; }
        public CancellationToken ReceivedCancellationToken { get; private set; }

        public Task AddReadingAsync(AltitudeReading reading, CancellationToken cancellationToken = default)
        {
            AddedReading = reading;
            ReceivedCancellationToken = cancellationToken;
            return Task.CompletedTask;
        }
    }
}
