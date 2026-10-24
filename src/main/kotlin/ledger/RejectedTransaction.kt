package ledger

enum class RejectedTransaction : Failure {
    INVALID_AMOUNT,
    ACCOUNT_NOT_FOUND,
    INSUFFICIENT_FUNDS,
    TRANSFER_TO_SELF
}