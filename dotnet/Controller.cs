using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

public class Worker
{
    public int WorkerId { get; set; }
    public string Name { get; set; }
    public ICollection<Shift> Shifts { get; set; } = new List<Shift>();
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


[Route("api/[controller]")]
[ApiController]
public class WorkersController : ControllerBase
{
    private readonly WorkPlanningContext _context;

    public WorkersController(WorkPlanningContext context)
    {
        _context = context;
    }

    // GET: api/workers
    [HttpGet("{id}")]
    public async Task<ActionResult<Worker>> GetWorkers(int id)
    {
        var worker = await _context.Workers
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
        _context.Workers.Add(worker);
        await _context.SaveChangesAsync();
        return CreatedAtAction("GetWorkers", new { id = worker.WorkerId }, worker);
    }

    // POST: api/workers/{id}/shifts
    [HttpPost("{id}/shifts")]
    public async Task<IActionResult> AddShift(int id, Shift shift)
    {
        var worker = await _context.Workers
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

        var x_shift = new Shift { ShiftId = 1, Date = DateTime.Now, Type = ShiftType.MORNING, WorkerId = worker.WorkerId };

        _context.Add(x_shift);

        await _context.SaveChangesAsync();

        return CreatedAtAction("GetWorkers", new { id = shift.WorkerId }, shift);
    }
}
