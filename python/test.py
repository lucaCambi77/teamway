import unittest
from app import app

class WorkPlanningTests(unittest.TestCase):
    def setUp(self):
        self.app = app.test_client()
        self.app.testing = True

    def test_create_worker(self):
        response = self.app.post('/workers', json={"name": "John Doe"})
        self.assertEqual(response.status_code, 201)
        self.assertIn("worker_id", response.get_json())

    def test_assign_valid_shift(self):
        # Create worker
        worker_response = self.app.post('/workers', json={"name": "Alice"})
        worker_id = worker_response.get_json()["worker_id"]

        # Assign shift
        shift_data = {"date": "2024-09-01", "start": 0, "end": 8}
        response = self.app.post(f'/workers/{worker_id}/shifts', json=shift_data)
        self.assertEqual(response.status_code, 201)

    def test_invalid_shift_timing(self):
        # Create worker
        worker_response = self.app.post('/workers', json={"name": "Bob"})
        worker_id = worker_response.get_json()["worker_id"]

        # Attempt to assign invalid shift
        shift_data = {"date": "2024-09-01", "start": 2, "end": 10}  # Invalid shift
        response = self.app.post(f'/workers/{worker_id}/shifts', json=shift_data)
        self.assertEqual(response.status_code, 400)

    def test_double_shift_on_same_day(self):
        # Create worker
        worker_response = self.app.post('/workers', json={"name": "Eve"})
        worker_id = worker_response.get_json()["worker_id"]

        # Assign first shift
        shift_data = {"date": "2024-09-01", "start": 0, "end": 8}
        self.app.post(f'/workers/{worker_id}/shifts', json=shift_data)

        # Attempt to assign a second shift on the same day
        second_shift_data = {"date": "2024-09-01", "start": 8, "end": 16}
        response = self.app.post(f'/workers/{worker_id}/shifts', json=second_shift_data)
        self.assertEqual(response.status_code, 400)

if __name__ == "__main__":
    unittest.main()