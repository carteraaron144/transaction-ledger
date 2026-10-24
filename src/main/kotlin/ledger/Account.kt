package ledger

import java.math.BigDecimal

data class Account(val accountId: String, val owner: String, var balance: BigDecimal) {
    override fun toString(): String {
        return "AccountId: $accountId\nOwner: $owner\nBalance: $balance\n"
    }
}

