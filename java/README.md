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