// Data/WorkPlanningContext.cs
using Microsoft.EntityFrameworkCore;

public class WorkPlanningContext : DbContext
{
    public WorkPlanningContext(DbContextOptions<WorkPlanningContext> options) : base(options) { }

    public DbSet<Worker> Workers { get; set; }
    public DbSet<Shift> Shifts { get; set; }
}
