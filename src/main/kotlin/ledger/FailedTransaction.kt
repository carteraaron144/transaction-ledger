package ledger

data class FailedTransaction(
    val failure: Failure,
    val lineNumber: Int
) {
    override fun toString(): String {
        return "$lineNumber: $failure\n"
    }
}