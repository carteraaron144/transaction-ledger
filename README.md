# Assignment 2: Bank Transaction Ledger

## Scenario
A small bank receives a text file of transactions at the end of each day. Write a Kotlin program that reads the file, applies each transaction to the account balances, and reports what happened.

The input is messy on purpose. Some lines are garbled, and some are well-formed but break a business rule. Your program has to handle both **without crashing** and explain every line it didn't apply.

## Starting accounts
Hard-code these in your program:

| Account | Owner | Balance |
|---|---|---|
| ACC-100 | Alice | 500.00 |
| ACC-200 | Bob | 150.00 |
| ACC-300 | Carol | 0.00 |

## Input file
`transactions.txt`, one transaction per line, comma-separated:

```
DEPOSIT,<account>,<amount>
WITHDRAW,<account>,<amount>
TRANSFER,<from account>,<to account>,<amount>
```

A sample file is provided in this folder.

## Requirements
1. Read the file line by line and process transactions **in order**.
2. **Skip blank lines** silently.
3. A line is **malformed** if it can't be understood. For example:
   - the wrong number of fields
   - an amount that isn't a number
   - an unknown transaction type

   Skip it, record the line number and the reason, and keep going.
4. A well-formed transaction is **rejected** if it breaks a rule. For example:
   - the amount is zero or negative
   - the account doesn't exist
   - there isn't enough money for a withdrawal or transfer
   - a transfer goes from an account to itself

   A rejected transaction must not change any balance. Record the line number and the reason, and keep going.
5. A **transfer is all-or-nothing**: either both balances change, or neither does.
6. If the input file **doesn't exist or can't be read**, print a clear error message and exit. Don't print a stack trace.
7. At the end, print:
   - each account's final balance, sorted by account ID
   - every rejected transaction, with its line number and reason
   - every malformed line, with its line number and reason
8. **Tests are required this time.** Write JUnit tests for the account and transaction rules. Include at least one test that checks a rule violation is reported correctly.

## Constraints
- Plain Kotlin (standard library) and JUnit only: no database, no frameworks. Java library classes like `BigDecimal` are fine; Kotlin uses them directly.
- Aim for a small number of classes. If you find yourself designing layers, stop and ask whether the problem needs them.

## Before you code
1. Work out by hand what should happen to each line of the sample file, and what the final balances should be. That's your test oracle again.
2. Then describe your design: the classes, what each one does, and **how a malformed line and a rejected transaction each get reported**. That last part is the heart of this assignment.

## Concepts this exercises
- Exception handling: custom exceptions, which code catches what, and `use { }` (Kotlin's version of try-with-resources) for reading files. Kotlin has no checked exceptions, so the compiler won't force you to handle anything: deciding where each failure gets caught is entirely on you.
- Telling "bad input" apart from "valid input that breaks a rule."
- Exact money arithmetic.
- Writing tests first-class, including tests that expect a failure.

## Stretch goals (optional)
- Read the starting accounts from a second file.
- Add a daily withdrawal limit per account.
