from flask import Flask, request, jsonify, abort

app = Flask(__name__)

# In-memory storage
workers = {}
shifts = {}

SHIFT_LENGTH = 8  # Shift length in hours
SHIFT_TIMES = [(0, 8), (8, 16), (16, 24)]  # Shift timings for the day


class Worker:
    def __init__(self, worker_id, name):
        self.worker_id = worker_id
        self.name = name


class Shift:
    def __init__(self, worker_id, date, start, end):
        self.worker_id = worker_id
        self.date = date
        self.start = start
        self.end = end


# Helper functions
def shift_exists(worker_id, date):
    return any(shift for shift in shifts.get(worker_id, []) if shift.date == date)


def is_valid_shift(start, end):
    return (start, end) in SHIFT_TIMES


# Routes
@app.route('/workers', methods=['POST'])
def create_worker():
    data = request.get_json()
    worker_id = len(workers) + 1
    worker = Worker(worker_id, data['name'])
    workers[worker_id] = worker
    return jsonify({"worker_id": worker_id, "name": worker.name}), 201


@app.route('/workers/<int:worker_id>/shifts', methods=['POST'])
def assign_shift(worker_id):
    if worker_id not in workers:
        abort(404, description="Worker not found")

    data = request.get_json()
    date = data['date']
    start = data['start']
    end = data['end']

    # Validate shift timing
    if not is_valid_shift(start, end):
        abort(400, description="Invalid shift timing")

    # Ensure no double shift on the same day
    if shift_exists(worker_id, date):
        abort(400, description="Worker already has a shift on this day")

    # Assign shift
    shift = Shift(worker_id, date, start, end)
    if worker_id not in shifts:
        shifts[worker_id] = []
    shifts[worker_id].append(shift)

    return jsonify({"worker_id": worker_id, "date": date, "start": start, "end": end}), 201


@app.route('/workers/<int:worker_id>/shifts', methods=['GET'])
def get_shifts(worker_id):
    if worker_id not in workers:
        abort(404, description="Worker not found")

    worker_shifts = shifts.get(worker_id, [])
    return jsonify([{
        "worker_id": shift.worker_id,
        "date": shift.date,
        "start": shift.start,
        "end": shift.end
    } for shift in worker_shifts]), 200


if __name__ == "__main__":
    app.run(debug=True)