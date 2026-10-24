package ledger

enum class Error : Failure {
    WRONG_NUMBER_OF_FIELDS,
    AMOUNT_NAN,
    UNKNOWN_TRANSACTION_TYPE
}