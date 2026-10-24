package ledger

enum class TransactionType(val fields: Int) {
    WITHDRAW(3),
    DEPOSIT(3),
    TRANSFER(4)
}