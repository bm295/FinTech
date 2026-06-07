using Coravel.Invocable;
using WebSiteRoute.Application.FlightAltitudes;

namespace WebSiteRoute.ScheduledTasks;

public sealed class RecordRandomAltitudeTask(RecordRandomAltitudeCommand command) : IInvocable
{
    public Task Invoke() => command.ExecuteAsync();
}
