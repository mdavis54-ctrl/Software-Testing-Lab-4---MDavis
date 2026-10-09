# Test Condition and Input (TCI)

## Equivalence Partitioning

| TCI   | Input / Condition     | Equivalence Partition             | Expected Behavior                    |
| ----- |-----------------------|-----------------------------------|--------------------------------------|
| EP-01 | resourceId            | `null`                            | Return `false`                       |
| EP-02 | resourceId            | Valid UUID                        | Continue processing                  |
| EP-03 | Resource availability | Resource unavailable              | Return `false`                       |
| EP-04 | Resource availability | Resource available                | Continue checkout                    |
| EP-05 | updateStatus          | Returns `false`                   | Throw `DatabaseFailureException`     |
| EP-06 | updateStatus          | Returns `true`                    | Continue to email                    |
| EP-07 | sendEmail             | Returns `false`                   | Throw `EmailFailureException`        |
| EP-08 | sendEmail             | Returns `true`                    | Return `true`                        |
| EP-09 | sendEmail             | Throws `EmailFailureException`    | Propagate `EmailFailureException`    |
| EP-10 | updateStatus          | Throws `DatabaseFailureException` | Propagate `DatabaseFailureException` |

## Boundary Value Analysis

This system does not contain numeric ranges or traditional input boundaries. So the closest boundary conditions are the transitions between the major processing states.

| TCI    | Input / Condition     | Boundary Values        |
| ------ |-----------------------|------------------------|
| BVA-01 | resourceId            | `null`, valid UUID     |
| BVA-02 | Resource availability | unavailable, available |
| BVA-03 | updateStatus result   | `false`, `true`        |
| BVA-04 | sendEmail result      | `false`, `true`        |

## Decision Table

| TCI   | Resource ID | Available? | Update Status | Send Email | Expected Result            |
| ----- |-------------|------------|---------------|------------|----------------------------|
| DT-01 | Null        | N/A        | N/A           | N/A        | Return `false`             |
| DT-02 | Valid       | No         | N/A           | N/A        | Return `false`             |
| DT-03 | Valid       | Yes        | False         | N/A        | `DatabaseFailureException` |
| DT-04 | Valid       | Yes        | True          | False      | `EmailFailureException`    |
| DT-05 | Valid       | Yes        | True          | True       | Return `true`              |
| DT-06 | Valid       | Yes        | Exception     | N/A        | `DatabaseFailureException` |
| DT-07 | Valid       | Yes        | True          | Exception  | `EmailFailureException`    |