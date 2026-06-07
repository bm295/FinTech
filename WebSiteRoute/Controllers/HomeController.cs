using Microsoft.AspNetCore.Mvc;
using System.Diagnostics;
using WebSiteRoute.Application.FlightAltitudes;
using WebSiteRoute.Models;
using WebSiteRoute.Presentation.ViewModels;

namespace WebSiteRoute.Controllers;

public class HomeController(ILogger<HomeController> logger, GetFlightAltitudeDashboardQuery dashboardQuery) : Controller
{
    public async Task<IActionResult> Index(CancellationToken cancellationToken)
    {
        var dashboard = await dashboardQuery.ExecuteAsync(cancellationToken);
        var viewModel = FlightAltitudeDashboardViewModel.FromDashboard(dashboard);

        logger.LogInformation("Rendered flight status message for altitude {Altitude}", dashboard.SampleAltitude);

        return View(viewModel);
    }

    public IActionResult Privacy()
    {
        return View();
    }

    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
    public IActionResult Error()
    {
        return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
    }
}
