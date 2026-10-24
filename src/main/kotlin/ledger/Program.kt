package ledger

import java.io.File
import java.io.IOException
import java.math.BigDecimal

class Program {
    var accounts: MutableList<Account> = mutableListOf(
        Account("ACC-100", "Alice", BigDecimal("500.00")),
        Account("ACC-200", "Bob", BigDecimal("150.00")),
        Account("ACC-300", "Carol", BigDecimal("0.00"))
    )
    var malformedTransactions: MutableList<FailedTransaction> = mutableListOf()
    var rejectedTransactions: MutableList<FailedTransaction> = mutableListOf()


    fun run(lines: List<String>) {
        // business logic
        for (lineNumber in 0..lines.lastIndex) {
            var currentLine = lines[lineNumber];
            //if the line is blank, skip it.
            if (currentLine.isEmpty()) continue;

            // check for malformed line

            var l = currentLine.split(",")
            var transactionType: TransactionType;

            // Try to get transaction type
            try {
                transactionType = TransactionType.valueOf(l[0])
            } catch (e: IllegalArgumentException) {
                // Invalid transaction type
                reportError(Error.UNKNOWN_TRANSACTION_TYPE, lineNumber)
                continue
            }

            if (l.count() != transactionType.fields) {
                // Too many or not enough fields
                reportError(Error.WRONG_NUMBER_OF_FIELDS, lineNumber)
                continue
            }

            val amount = try {
                 when (transactionType) {
                    TransactionType.DEPOSIT, TransactionType.WITHDRAW -> l[2].toBigDecimal()
                    TransactionType.TRANSFER -> l[3].toBigDecimal()
                 }
            } catch (e: IllegalArgumentException) {
                // Error converting to BigDecimal, not a number
                reportError(Error.AMOUNT_NAN, lineNumber)
                continue
            }

            // check for well-formed transaction issues

            // Zero or negative values
            if (amount <= BigDecimal.ZERO) {
                reportError(RejectedTransaction.INVALID_AMOUNT, lineNumber)
                continue
            }

            // No such account
            val accountIndex = accounts.indexOfFirst { it.accountId == l[1] }
            if (accountIndex == -1) {
                reportError(RejectedTransaction.ACCOUNT_NOT_FOUND, lineNumber)
                continue
            }

            // DO DEPOSIT
            if (TransactionType.DEPOSIT == transactionType) {
                accounts[accountIndex].balance = accounts[accountIndex].balance.add(amount)
            }

            // Check transfer for both accounts and same to/from location
            if (TransactionType.TRANSFER == transactionType) {
                if (!tryTransfer(l, accountIndex, amount, lineNumber)) continue
            }

            if (TransactionType.WITHDRAW == transactionType) {
                if (amount > accounts[accountIndex].balance) {
                    reportError(RejectedTransaction.INSUFFICIENT_FUNDS, lineNumber)
                    continue
                }
                // DO WITHDRAWAL
                accounts[accountIndex].balance = accounts[accountIndex].balance.subtract(amount)
            }
        }
    }

    fun readFile(path: String): List<String> {
        val lines: List<String> = try {
            File(path).readLines()
        } catch (e: IOException) {
            System.err.println("Error reading $path")
            System.err.println("Error: ${e.message}")
            return emptyList()
        }
        return lines
    }

    fun reportError(error: RejectedTransaction, line: Int) {
        rejectedTransactions.add(FailedTransaction(error, line+1))
    }

    fun reportError(error: Error, line: Int) {
        malformedTransactions.add(FailedTransaction(error, line+1))
    }

    fun printOutput() {
        accounts.sortBy{ it.accountId }
        printList(accounts)
        println("Rejected transactions:")
        printList(rejectedTransactions)
        println("Malformed transactions:")
        printList(malformedTransactions)
    }

    fun printList(list: List<Any>) {
        for (item in list) {
            println(item.toString())
        }
    }

    fun tryTransfer(l: List<String>, accountIndex: Int, amount: BigDecimal, lineNumber: Int) : Boolean {
        val toAccount = accounts.indexOfFirst { it.accountId == l[2] }
        if (toAccount == -1) {
            reportError(RejectedTransaction.ACCOUNT_NOT_FOUND, lineNumber)
            return false
        }
        if (l[1] == l[2]) {
            reportError(RejectedTransaction.TRANSFER_TO_SELF, lineNumber)
            return false
        }
        if (amount > accounts[accountIndex].balance) {
            reportError(RejectedTransaction.INSUFFICIENT_FUNDS, lineNumber)
            return false
        }
        // DO TRANSFER
        accounts[accountIndex].balance = accounts[accountIndex].balance.subtract(amount)
        accounts[toAccount].balance = accounts[toAccount].balance.add(amount)
        return true
    }

}