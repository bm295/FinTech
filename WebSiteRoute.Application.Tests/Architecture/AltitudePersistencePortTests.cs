using WebSiteRoute.Application.Abstractions;

namespace WebSiteRoute.Application.Tests.Architecture;

public sealed class AltitudePersistencePortTests
{
    [Fact]
    public void ReadAndWritePortsRemainIndependentlyConsumable()
    {
        var readerMethod = Assert.Single(typeof(IAltitudeReadingReader).GetMethods());
        var writerMethod = Assert.Single(typeof(IAltitudeReadingWriter).GetMethods());

        Assert.Equal(nameof(IAltitudeReadingReader.GetReadingsAsync), readerMethod.Name);
        Assert.Equal(nameof(IAltitudeReadingWriter.AddReadingAsync), writerMethod.Name);
    }
}
