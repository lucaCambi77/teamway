package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strconv"
	"time"
)

// Enum for shift types
type ShiftType string

const (
	ShiftMorning   ShiftType = "morning"
	ShiftAfternoon ShiftType = "afternoon"
	ShiftNight     ShiftType = "night"
)

// Worker represents a worker
type Worker struct {
	ID     int     `json:"id"`
	Name   string  `json:"name"`
	Shifts []Shift `json:"shifts"`
}

// Shift represents a shift assigned to a worker
type Shift struct {
	WorkerID  int       `json:"worker_id"`
	Date      time.Time `json:"date"`
	ShiftType ShiftType `json:"shift_type"`
}

type ShiftService struct {
	workerShifts map[int][]Shift // maps Worker ID to list of their shifts
}

func NewShiftService() *ShiftService {
	return &ShiftService{
		workerShifts: make(map[int][]Shift),
	}
}

func (s *ShiftService) AssignShift(workerID int, shiftType ShiftType, date time.Time) error {
	// Ensure the worker doesn't have a shift on this date already
	for _, shift := range s.workerShifts[workerID] {
		if shift.Date.Day() == date.Day() {
			return fmt.Errorf("worker already has a shift on this day")
		}
	}

	// Create new shift
	shift := Shift{
		WorkerID:  workerID,
		Date:      date,
		ShiftType: shiftType,
	}

	// Assign the shift to the worker
	s.workerShifts[workerID] = append(s.workerShifts[workerID], shift)
	return nil
}

func (s *ShiftService) GetShiftsForWorker(workerID int) ([]Shift, error) {
	shifts, exists := s.workerShifts[workerID]
	if !exists {
		return nil, fmt.Errorf("worker not found")
	}
	return shifts, nil
}

type ShiftController struct {
	shiftService *ShiftService
}

func NewShiftController(service *ShiftService) *ShiftController {
	return &ShiftController{shiftService: service}
}

// Struct for the input data from the client (JSON body)
type ShiftRequest struct {
	WorkerID  int       `json:"worker_id"`
	ShiftType ShiftType `json:"shift_type"`
	Date      string    `json:"date"` // Date as string in the format "2006-01-02"
}

func (ctrl *ShiftController) AssignShift(w http.ResponseWriter, r *http.Request) {
	// Parse the JSON body
	var request ShiftRequest
	decoder := json.NewDecoder(r.Body)
	err := decoder.Decode(&request)
	if err != nil {
		http.Error(w, "Invalid JSON input", http.StatusBadRequest)
		return
	}

	// Convert the string date to time.Time
	shiftDate, err := time.Parse("2006-01-02", request.Date)
	if err != nil {
		http.Error(w, "Invalid date format", http.StatusBadRequest)
		return
	}

	// Call service to assign the shift
	err = ctrl.shiftService.AssignShift(request.WorkerID, request.ShiftType, shiftDate)
	if err != nil {
		http.Error(w, err.Error(), http.StatusConflict)
		return
	}

	w.WriteHeader(http.StatusCreated)
	fmt.Fprintf(w, "Shift assigned successfully")
}

func (ctrl *ShiftController) GetShifts(w http.ResponseWriter, r *http.Request) {
	// Example: /shifts?worker_id=1
	workerID := r.URL.Query().Get("worker_id")
	if workerID == "" {
		http.Error(w, "Missing worker ID", http.StatusBadRequest)
		return
	}

	workerIDInt, err := strconv.Atoi(workerID)
	if err != nil {
		http.Error(w, "Invalid worker ID", http.StatusBadRequest)
		return
	}

	// Get worker shifts
	shifts, err := ctrl.shiftService.GetShiftsForWorker(workerIDInt)
	if err != nil {
		http.Error(w, err.Error(), http.StatusNotFound)
		return
	}

	// Return shifts as JSON
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(shifts)
}

func main() {
	shiftService := NewShiftService()
	shiftController := NewShiftController(shiftService)

	http.HandleFunc("/assign-shift", shiftController.AssignShift)
	http.HandleFunc("/shifts", shiftController.GetShifts)

	log.Fatal(http.ListenAndServe(":8080", nil))
}
