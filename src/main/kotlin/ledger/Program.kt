package ledger

import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.util.Optional

class Program {
    var accounts: MutableList<Account> = mutableListOf(
        Account("ACC-100", "Alice", BigDecimal("500.00")),
        Account("ACC-200", "Bob", BigDecimal("150.00")),
        Account("ACC-300", "Carol", BigDecimal("0.00"))
    )
    var malformedTransactions: MutableList<FailedTransaction> = mutableListOf()
    var rejectedTransactions: MutableList<FailedTransaction> = mutableListOf()

    val TRANSACTION_TYPE_INDEX = 0
    val ACCOUNT_ID_INDEX = 1
    val DEPO_AMOUNT_INDEX = 2;
    val WITH_AMOUNT_INDEX = 2;

    val TRANS_FROM_ACCOUNT_INDEX = 1
    val TRANS_TO_ACCOUNT_INDEX = 2
    val TRANS_AMOUNT_INDEX = 3


    fun run(lines: List<String>) {
        // business logic
        for (lineNumber in 0..lines.lastIndex) {
            var line = processLine(lines[lineNumber], lineNumber+1)
        }
    }

    fun processLine(line: String, lineNumber: Int) : Optional<FailedTransaction> {
        //if the line is blank, skip it.
        if (line.isEmpty()) return Optional.empty()

        // check for malformed line

        var l = line.split(",")
        var transactionType: TransactionType;

        // Try to get transaction type
        try {
            transactionType = TransactionType.valueOf(l[TRANSACTION_TYPE_INDEX])
        } catch (e: IllegalArgumentException) {
            // Invalid transaction type
            return Optional.of(reportError(Error.UNKNOWN_TRANSACTION_TYPE, lineNumber))
        }

        if (l.count() != transactionType.fields) {
            // Too many or not enough fields
            return Optional.of(reportError(Error.WRONG_NUMBER_OF_FIELDS, lineNumber))
        }

        val amount = try {
            when (transactionType) {
                TransactionType.DEPOSIT, TransactionType.WITHDRAW -> l[DEPO_AMOUNT_INDEX].toBigDecimal()
                TransactionType.TRANSFER -> l[TRANS_AMOUNT_INDEX].toBigDecimal()
            }
        } catch (e: IllegalArgumentException) {
            // Error converting to BigDecimal, not a number
            return Optional.of(reportError(Error.AMOUNT_NAN, lineNumber))
        }

        // check for well-formed transaction issues

        // Zero or negative values
        if (amount <= BigDecimal.ZERO) {
            return Optional.of(reportError(RejectedTransaction.INVALID_AMOUNT, lineNumber))
        }

        // No such account
        val accountIndex = accounts.indexOfFirst { it.accountId == l[ACCOUNT_ID_INDEX] }
        if (accountIndex == -1) {
            return Optional.of(reportError(RejectedTransaction.ACCOUNT_NOT_FOUND, lineNumber))
        }

        // Deposits
        if (transactionType == TransactionType.DEPOSIT) {
            // DO DEPOSIT
            accounts[accountIndex].balance = accounts[accountIndex].balance.add(amount)
        }

        // Transfers
        if (transactionType == TransactionType.TRANSFER) {
            val toAccount = accounts.indexOfFirst { it.accountId == l[TRANS_TO_ACCOUNT_INDEX] }
            if (toAccount == -1) { // No to-account id present
                return Optional.of(reportError(RejectedTransaction.ACCOUNT_NOT_FOUND, lineNumber))
            }
            if (l[TRANS_FROM_ACCOUNT_INDEX] == l[TRANS_TO_ACCOUNT_INDEX]) {
                return Optional.of(reportError(RejectedTransaction.TRANSFER_TO_SELF, lineNumber))
            }
            if (amount > accounts[accountIndex].balance) {
                return Optional.of(reportError(RejectedTransaction.INSUFFICIENT_FUNDS, lineNumber))
            }
            // DO TRANSFER
            accounts[accountIndex].balance = accounts[accountIndex].balance.subtract(amount)
            accounts[toAccount].balance = accounts[toAccount].balance.add(amount)
        }

        // Withdrawals
        if (transactionType == TransactionType.WITHDRAW) {
            if (amount > accounts[accountIndex].balance) {
                return Optional.of(reportError(RejectedTransaction.INSUFFICIENT_FUNDS, lineNumber))
            }
            // DO WITHDRAWAL
            accounts[accountIndex].balance = accounts[accountIndex].balance.subtract(amount)
        }
        return Optional.empty()
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

    fun reportError(error: RejectedTransaction, line: Int) : FailedTransaction {
        val rejectedTransaction = FailedTransaction(error, line)
        rejectedTransactions.add(rejectedTransaction)
        return rejectedTransaction
    }

    fun reportError(error: Error, line: Int) : FailedTransaction {
        val malformedTransaction = FailedTransaction(error, line)
        malformedTransactions.add(malformedTransaction)
        return malformedTransaction
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

    fun getBalance(id: String) : BigDecimal {
        return accounts.first { it.accountId == id }.balance
    }
}