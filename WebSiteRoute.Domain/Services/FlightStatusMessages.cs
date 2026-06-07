namespace WebSiteRoute.Domain.Services;

public interface IFlightStatusMessageProvider
{
    string GetStatusMessage(int altitude);
}

public class FlightStatusMessages : IFlightStatusMessageProvider
{
    public virtual string GetStatusMessage(int altitude)
        => $"Generic flight status: altitude {altitude} ft.";
}

public sealed class PassengerFlightStatusMessages : FlightStatusMessages
{
    public override string GetStatusMessage(int altitude)
        => altitude switch
        {
            >= 35000 => "Cruising at a typical passenger-flight altitude.",
            >= 10000 => "Climbing or descending within controlled airspace.",
            _ => "Operating below standard cruising altitude."
        };
}
