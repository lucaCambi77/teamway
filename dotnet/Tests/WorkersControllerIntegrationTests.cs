using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Xunit;
using FluentAssertions;


namespace WorkPlanning.Tests
{
    public class WorkersControllerIntegrationTests : IClassFixture<WebApplicationFactory<Program>>, IDisposable
    {
        private readonly HttpClient _client;
        private readonly WorkPlanningContext _context;


        public WorkersControllerIntegrationTests(WebApplicationFactory<Program> factory)
        {
            _client = factory.WithWebHostBuilder(builder =>
            {
                builder.ConfigureServices(services =>
                {
                    // Remove the existing DbContext
                    var descriptor = services.SingleOrDefault(
                        d => d.ServiceType == typeof(DbContextOptions<WorkPlanningContext>));
                    if (descriptor != null)
                    {
                        services.Remove(descriptor);
                    }

                    // Add a new DbContext using an in-memory database
                    services.AddDbContext<WorkPlanningContext>(options =>
                    {
                        options.UseInMemoryDatabase("InMemoryDbForTesting");
                    });
                });
            }).CreateClient();

            // Manually create a DbContext using the same in-memory database for direct access
            var options = new DbContextOptionsBuilder<WorkPlanningContext>()
                .UseInMemoryDatabase("InMemoryDbForTesting")
                .Options;

            _context = new WorkPlanningContext(options);
        }

        public void Dispose()
        {
            _context.Database.EnsureDeleted();  // Clean up the in-memory database after each test
            _context.Dispose();  // Ensure the DbContext is disposed after each test
        }

        [Fact]
        public async Task GetWorker_ReturnsWorker()
        {
            // Arrange
            var worker = new Worker { WorkerId = 1, Name = "John Doe" };
            var response = await _client.PostAsync("/api/workers", new StringContent(
                JsonConvert.SerializeObject(worker), Encoding.UTF8, "application/json"));

            var workerResponse = await response.Content.ReadAsStringAsync();

            response.EnsureSuccessStatusCode();

            // Act
            var result = await _client.GetAsync("/api/workers/1");

            // Assert
            result.StatusCode.Should().Be(HttpStatusCode.OK);
            var workers = JsonConvert.DeserializeObject<Worker>(await result.Content.ReadAsStringAsync());
            workers.Should().NotBeNull();
            workers.Name.Should().Be("John Doe");
        }

        [Fact]
        public async Task AddShift_ReturnsBadRequest_IfWorkerHasShiftOnSameDay()
        {
            // Arrange
            var worker = new Worker { WorkerId = 1, Name = "John Doe" };
            await _client.PostAsync("/api/workers", new StringContent(
                JsonConvert.SerializeObject(worker), Encoding.UTF8, "application/json"));

            var shift = new Shift { ShiftId = 1, Date = DateTime.Now, Type = ShiftType.MORNING, Worker = worker };
            await _client.PostAsync("/api/workers/1/shifts", new StringContent(
                JsonConvert.SerializeObject(shift), Encoding.UTF8, "application/json"));

            // Act
            var duplicateShift = new Shift { ShiftId = 2, Date = DateTime.Now, Type = ShiftType.AFTERNOON, Worker = worker };
            var result = await _client.PostAsync("/api/workers/1/shifts", new StringContent(
                JsonConvert.SerializeObject(duplicateShift), Encoding.UTF8, "application/json"));

            // Assert
            result.StatusCode.Should().Be(HttpStatusCode.BadRequest);
            var responseMessage = await result.Content.ReadAsStringAsync();

            responseMessage.Should().Contain("Worker already has a shift on this day.");
        }

        [Fact]
        public async Task AddShift_ReturnsBadRequest_IfShiftTypeIsInvalid()
        {
            // Arrange
            var worker = new Worker { WorkerId = 1, Name = "John Doe" };
            await _client.PostAsync("/api/workers", new StringContent(
                JsonConvert.SerializeObject(worker), Encoding.UTF8, "application/json"));

            var invalidShift = new Shift { ShiftId = 2, Date = DateTime.Now, Type = (ShiftType)999, Worker = worker }; // Invalid ShiftType

            // Act
            var result = await _client.PostAsync("/api/workers/1/shifts", new StringContent(
                JsonConvert.SerializeObject(invalidShift), Encoding.UTF8, "application/json"));

            // Assert
            result.StatusCode.Should().Be(HttpStatusCode.BadRequest);
            var responseMessage = await result.Content.ReadAsStringAsync();
            responseMessage.Should().Contain("Invalid shift type.");
        }
    }
}