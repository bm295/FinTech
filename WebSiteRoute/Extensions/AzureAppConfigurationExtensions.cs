using Azure.Identity;
using Microsoft.Azure.AppConfiguration.AspNetCore;
using Microsoft.Extensions.Configuration.AzureAppConfiguration;

namespace WebSiteRoute.Extensions;

public static class AzureAppConfigurationExtensions
{
    private const string SectionName = "AzureAppConfiguration";

    public static WebApplicationBuilder AddAzureAppConfiguration(
        this WebApplicationBuilder builder,
        DefaultAzureCredential azureCredential)
    {
        ArgumentNullException.ThrowIfNull(azureCredential);

        if (!builder.Configuration.GetValue($"{SectionName}:Enabled", true))
        {
            return builder;
        }

        var connectionString = builder.Configuration[$"{SectionName}:ConnectionString"];
        var endpoint = builder.Configuration[$"{SectionName}:Endpoint"];

        if (string.IsNullOrWhiteSpace(connectionString) && string.IsNullOrWhiteSpace(endpoint))
        {
            return builder;
        }

        var keyFilter = builder.Configuration[$"{SectionName}:KeyFilter"] ?? KeyFilter.Any;
        var labelFilter = builder.Configuration[$"{SectionName}:Label"] ?? LabelFilter.Null;
        var refreshIntervalSeconds = builder.Configuration.GetValue<int?>($"{SectionName}:RefreshIntervalSeconds");

        builder.Configuration.AddAzureAppConfiguration(options =>
        {
            if (!string.IsNullOrWhiteSpace(connectionString))
            {
                options.Connect(connectionString);
            }
            else
            {
                options.Connect(new Uri(endpoint!), azureCredential);
            }

            options.Select(keyFilter, labelFilter)
                .ConfigureKeyVault(keyVault => keyVault.SetCredential(azureCredential))
                .ConfigureRefresh(refreshOptions =>
                {
                    var registration = refreshOptions.RegisterAll();

                    if (refreshIntervalSeconds is > 0)
                    {
                        registration.SetRefreshInterval(TimeSpan.FromSeconds(refreshIntervalSeconds.Value));
                    }
                });

            var refresher = options.GetRefresher();
            builder.Services.AddSingleton(refresher);
        });

        builder.Services.AddAzureAppConfiguration();
        builder.Services.AddSingleton(new AzureAppConfigurationRegistrationMarker());

        return builder;
    }

    public static WebApplication UseAzureAppConfigurationRefresh(this WebApplication app)
    {
        if (app.Services.GetService<AzureAppConfigurationRegistrationMarker>() is not null)
        {
            app.UseAzureAppConfiguration();
        }

        return app;
    }

    private sealed class AzureAppConfigurationRegistrationMarker;
}
