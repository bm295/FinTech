using WebSiteRoute.Application.Abstractions;
using WebSiteRoute.Application.FlightAltitudes;
using WebSiteRoute.Domain.Services;
using WebSiteRoute.Infrastructure.InfluxDb;

namespace WebSiteRoute.Composition;

public static class DependencyInjection
{
    public static IServiceCollection AddCleanArchitecture(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddDomain();
        services.AddApplication();
        services.AddInfrastructure(configuration);

        return services;
    }

    private static IServiceCollection AddDomain(this IServiceCollection services)
    {
        services.AddSingleton<IFlightStatusMessageProvider, PassengerFlightStatusMessages>();
        return services;
    }

    private static IServiceCollection AddApplication(this IServiceCollection services)
    {
        services.AddSingleton(TimeProvider.System);
        services.AddScoped<GetFlightAltitudeDashboardQuery>();
        services.AddScoped<RecordRandomAltitudeCommand>();
        return services;
    }

    private static IServiceCollection AddInfrastructure(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddSingleton(new InfluxDbOptions
        {
            Url = configuration["InfluxDb:Url"] ?? "https://51h585.stackhero-network.com",
            Token = configuration["InfluxDb:Token"] ?? string.Empty,
            Bucket = configuration["InfluxDb:Bucket"] ?? "test-bucket",
            Organization = configuration["InfluxDb:Organization"] ?? "organization",
            PlaneId = configuration["InfluxDb:PlaneId"] ?? "test-plane"
        });
        services.AddSingleton<IAltitudeGenerator, RandomAltitudeGenerator>();
        services.AddScoped<InfluxDbAltitudeReadingRepository>();
        services.AddScoped<IAltitudeReadingReader>(serviceProvider =>
            serviceProvider.GetRequiredService<InfluxDbAltitudeReadingRepository>());
        services.AddScoped<IAltitudeReadingWriter>(serviceProvider =>
            serviceProvider.GetRequiredService<InfluxDbAltitudeReadingRepository>());

        return services;
    }
}
