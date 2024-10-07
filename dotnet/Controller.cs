using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace dotnet;

public class Worker
{
    public int WorkerId { get; set; }
    public string Name { get; set; }
    public ICollection<Shift> Shifts { get; set; } = new List<Shift>();
}

public class WorkerDto
{
    public int WorkerId { get; set; }
    public string Name { get; set; }
    public ICollection<ShiftDto> Shifts { get; set; }
}

public enum ShiftType
{
    MORNING,   // 0-8
    AFTERNOON, // 8-16
    NIGHT      // 16-24
}

public class Shift
{
    public int ShiftId { get; set; }
    public DateTime Date { get; set; }
    public int WorkerId { get; set; }
    public Worker Worker { get; set; }

    public ShiftType Type { get; set; }  // Enum for shift type
}

public class ShiftDto
{
    public int ShiftId { get; set; }
    public DateTime Date { get; set; }
    public int WorkerId { get; set; }

    public ShiftType Type { get; set; }  // Enum for shift type
}


[Route("api/[controller]")]
[ApiController]
public class WorkersController(WorkPlanningContext context) : ControllerBase
{
    // GET: api/workers
    [HttpGet("{id}")]
    public async Task<ActionResult<Worker>> GetWorkers(int id)
    {
        var worker = await context.Workers
            .Include(w => w.Shifts) // Ensure shifts are loaded
            .FirstOrDefaultAsync(w => w.WorkerId == id);

        if (worker == null)
        {
            return NotFound();
        }

        return worker;
    }

    // POST: api/workers
    [HttpPost]
    public async Task<ActionResult<Worker>> PostWorker(Worker worker)
    {
        context.Workers.Add(worker);
        await context.SaveChangesAsync();
        return CreatedAtAction("GetWorkers", new { id = worker.WorkerId }, worker);
    }

    // POST: api/workers/{id}/shifts
    [HttpPost("{id}/shifts")]
    public async Task<IActionResult> AddShift(int id, ShiftDto shift)
    {
        var worker = await context.Workers
            .Include(w => w.Shifts)
            .FirstOrDefaultAsync(w => w.WorkerId == id);

        if (worker == null) return NotFound();

        // Ensure the worker does not have a shift on the same day
        if (worker.Shifts.Any(s => s.Date.Date == shift.Date.Date))
        {
            return BadRequest("Worker already has a shift on this day.");
        }

        if (!Enum.IsDefined(typeof(ShiftType), shift.Type))
        {
            return BadRequest("Invalid shift type.");
        }

        context.Add(new Shift { ShiftId = shift.ShiftId, Date = shift.Date, Type = shift.Type, WorkerId = shift.WorkerId });

        await context.SaveChangesAsync();

        return CreatedAtAction("GetWorkers", new { id = shift.WorkerId }, shift);
    }
}