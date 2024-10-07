// Data/WorkPlanningContext.cs

using Microsoft.EntityFrameworkCore;

namespace dotnet;

public class WorkPlanningContext(DbContextOptions<WorkPlanningContext> options) : DbContext(options)
{
    public DbSet<Worker> Workers { get; set; }
    public DbSet<Shift> Shifts { get; set; }
}