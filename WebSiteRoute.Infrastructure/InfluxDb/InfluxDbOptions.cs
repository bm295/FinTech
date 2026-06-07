namespace WebSiteRoute.Infrastructure.InfluxDb;

public sealed class InfluxDbOptions
{
    public string Url { get; init; } = "https://51h585.stackhero-network.com";

    public string Token { get; init; } = string.Empty;

    public string Bucket { get; init; } = "test-bucket";

    public string Organization { get; init; } = "organization";

    public string PlaneId { get; init; } = "test-plane";
}
