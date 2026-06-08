using Azure.Identity;
using Coravel;
using WebSiteRoute.Composition;
using WebSiteRoute.Extensions;
using WebSiteRoute.ScheduledTasks;

var builder = WebApplication.CreateBuilder(args);

var azureCredential = new DefaultAzureCredential();
builder.AddAzureAppConfiguration(azureCredential);
builder.Services.AddControllersWithViews();
builder.Services.AddCleanArchitecture(builder.Configuration);
builder.Services.AddTransient<RecordRandomAltitudeTask>();
builder.Services.AddScheduler();

var app = builder.Build();

if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Home/Error");
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseStaticFiles();

app.UseRouting();
app.UseAzureAppConfigurationRefresh();

app.UseAuthorization();

app.Services.UseScheduler(scheduler =>
{
    scheduler.Schedule<RecordRandomAltitudeTask>().EveryFiveSeconds();
});

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Home}/{action=Index}/{id?}");

app.Run();
