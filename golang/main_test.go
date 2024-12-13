package main

import (
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

func TestAssignShift(t *testing.T) {
	shiftService := NewShiftService()
	shiftController := NewShiftController(shiftService)

	// Create a request with JSON body
	jsonBody := `{"worker_id": 1, "shift_type": "morning", "date": "2024-12-13"}`
	req, err := http.NewRequest("POST", "/assign-shift", strings.NewReader(jsonBody))
	if err != nil {
		t.Fatalf("Failed to create request: %v", err)
	}

	// Create a response recorder to capture the response
	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(shiftController.AssignShift)

	// Call the handler with the request and response recorder
	handler.ServeHTTP(rr, req)

	// Check if the response status is what we expect
	if status := rr.Code; status != http.StatusCreated {
		t.Errorf("Expected status %v, got %v", http.StatusCreated, status)
	}

	// Check the response body (optional)
	expected := "Shift assigned successfully"
	if rr.Body.String() != expected {
		t.Errorf("Expected body %v, got %v", expected, rr.Body.String())
	}
}
