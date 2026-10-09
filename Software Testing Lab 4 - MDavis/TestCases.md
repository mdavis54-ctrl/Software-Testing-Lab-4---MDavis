# Test Cases

## Equivalence Partitioning Test Cases

| Test Case  | TCI                        | Resource ID  | Available  | Update Status    | Send Email       | Expected Result            |
|------------|----------------------------|--------------|------------|------------------|------------------|----------------------------|
| TC01       | EP-01                      | NULL         | N/A        | N/A              | N/A              | `false`                    |
| TC02       | EP-02, EP-03               | Valid UUID   | FALSE      | N/A              | N/A              | `false`                    |
| TC03       | EP-02, EP-04, EP-06, EP-08 | Valid UUID   | TRUE       | TRUE             | TRUE             | `true`                     |
| TC04       | EP-02, EP-04, EP-05        | Valid UUID   | TRUE       | FALSE            | N/A              | `DatabaseFailureException` |
| TC05       | EP-02, EP-04, EP-06, EP-07 | Valid UUID   | TRUE       | TRUE             | FALSE            | `EmailFailureException`    |
| TC06       | EP-02, EP-04, EP-06, EP-09 | Valid UUID   | TRUE       | TRUE             | Throws Exception | `EmailFailureException`    |
| TC07       | EP-02, EP-04, EP-10        | Valid UUID   | TRUE       | Throws Exception | N/A              | `DatabaseFailureException` |

## Boundary Value Analysis Test Cases

| Test Case | TCI      | Boundary Condition     | Expected Result            |
|-----------|----------|------------------------|----------------------------|
| TC01      | BVA-01   | `resourceId = null`    | `false`                    |
| TC02      | BVA-01   | Valid UUID             | Continue processing        |
| TC02      | BVA-02   | Resource unavailable   | `false`                    |
| TC03      | BVA-02   | Resource available     | Continue checkout          |
| TC04      | BVA-03   | `updateStatus = false` | `DatabaseFailureException` |
| TC03      | BVA-03   | `updateStatus = true`  | Continue to email          |
| TC05      | BVA-04   | `sendEmail = false`    | `EmailFailureException`    |
| TC03      | BVA-04   | `sendEmail = true`     | `true`                     |

## Decision Table Test Cases

| Test Case  | TCI   | Resource ID  | Available?  | Update Status  | Send Email  | Expected Result            |
|------------|-------|--------------|-------------|----------------|-------------|----------------------------|
| TC01       | DT-01 | NULL         | N/A         | N/A            | N/A         | `false`                    |
| TC02       | DT-02 | Valid        | FALSE       | N/A            | N/A         | `false`                    |
| TC04       | DT-03 | Valid        | TRUE        | FALSE          | N/A         | `DatabaseFailureException` |
| TC05       | DT-04 | Valid        | TRUE        | TRUE           | FALSE       | `EmailFailureException`    |
| TC03       | DT-05 | Valid        | TRUE        | TRUE           | TRUE        | `true`                     |
| TC07       | DT-06 | Valid        | TRUE        | Exception      | N/A         | `DatabaseFailureException` |
| TC06       | DT-07 | Valid        | TRUE        | TRUE           | Exception   | `EmailFailureException`    |

## Final Test Case List

| Test Case  | Purpose                         | Resource ID  | Available  | Update Status  | Send Email  | Expected Result            |
|------------|---------------------------------|--------------|------------|----------------|-------------|----------------------------|
| TC01       | Null resource ID                | NULL         | N/A        | N/A            | N/A         | `false`                    |
| TC02       | Resource unavailable            | Valid UUID   | FALSE      | N/A            | N/A         | `false`                    |
| TC03       | Successful checkout             | Valid UUID   | TRUE       | TRUE           | TRUE        | `true`                     |
| TC04       | Database update failure         | Valid UUID   | TRUE       | FALSE          | N/A         | `DatabaseFailureException` |
| TC05       | Email sending failure           | Valid UUID   | TRUE       | TRUE           | FALSE       | `EmailFailureException`    |
| TC06       | Email provider throws exception | Valid UUID   | TRUE       | TRUE           | Exception   | `EmailFailureException`    |
| TC07       | Repository throws exception     | Valid UUID   | TRUE       | Exception      | N/A         | `DatabaseFailureException` |