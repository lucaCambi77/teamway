# Task

Build a REST application from scratch that could serve as a work planning service.
Business requirements:
A worker has shifts
A shift is 8 hours long
A worker never has two shifts on the same day
It is a 24 hour timetable 0-8, 8-16, 16-24
Preferably write a couple of units tests.

## Requirements :

* Java11
* Gradle

## Getting Started

* To build the project
```bash
./gradlew clean build
```

## Run

* To run the application

```bash
./gradlew bootRun
```

## Endpoint

```text
POST http://localhost:8080/teamway/workerShift
```

Example payload :

```text
{
  "workerName" : "worker1",
  "shiftDay" : "2022-01-01",
  "shift" : "FIRST"
}
```

There are 3 shifts available : 

| Shift  | Description |
|--------|:-----------:|
| FIRST  |    0 -8     |
| SECOND |    8 -16    |
| THIRD  |   16- 24    |