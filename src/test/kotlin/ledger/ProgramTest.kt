package ledger

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProgramTest {

    @Test
    fun `withdraw rule violation gets reported correctly`() {
        val program = Program()
        val modelTransaction: FailedTransaction = FailedTransaction(RejectedTransaction.INSUFFICIENT_FUNDS, 1)

        assertEquals(modelTransaction,  program.processLine("WITHDRAW,ACC-200,200.00", 1).get())
    }

    @Test
    fun `transfer bad to-account violation gets reported correctly`() {
        val program = Program()
        val line = "TRANSFER,ACC-200,ACC-999,10.00"
        val lineNumber = 4

        val expectedResult: FailedTransaction = FailedTransaction(RejectedTransaction.ACCOUNT_NOT_FOUND, lineNumber)
        val actualResult = program.processLine(line, lineNumber).get()

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `transfer insufficient funds violation gets reported correctly`() {
        val program = Program()
        val line = "TRANSFER,ACC-300,ACC-100,200.00"
        val lineNumber = 4

        val expectedResult: FailedTransaction = FailedTransaction(RejectedTransaction.INSUFFICIENT_FUNDS, lineNumber)
        val actualResult = program.processLine(line, lineNumber).get()

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `NAN amount gets reported correctly`() {
        val program = Program()
        val line = "WITHDRAW,ACC-100,abc"
        val lineNumber = 4

        val expectedResult: FailedTransaction = FailedTransaction(Error.AMOUNT_NAN, lineNumber)
        val actualResult = program.processLine(line, lineNumber).get()

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `invalid transaction type amount gets reported correctly`() {
        val program = Program()
        val line = "WITHDR-AMP,ACC-100,abc"
        val lineNumber = 4

        val expectedResult: FailedTransaction = FailedTransaction(Error.UNKNOWN_TRANSACTION_TYPE, lineNumber)
        val actualResult = program.processLine(line, lineNumber).get()

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `file path not present violation exits correctly`() {
        val program = Program()
        val filepath = "transFUNction.txt"

        val expectedResult = emptyList<String>()
        val actualResult = program.readFile(filepath)

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `ensure balance correctly changes after withdrawal`() {
        val program = Program()
        val line = "WITHDRAW,ACC-100,100.00"
        val lineNumber = 4

        val expectedResult = BigDecimal("400.00")
        val a = program.processLine(line, lineNumber)
        val actualResult = program.getBalance("ACC-100")

        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun `ensure balance correctly changes after deposit`() {
        val program = Program()
        val line = "DEPOSIT,ACC-100,100.00"
        val lineNumber = 4

        val expectedResult = BigDecimal("600.00")
        val a = program.processLine(line, lineNumber)
        val actualResult = program.getBalance("ACC-100")

        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun `ensure balance correctly changes after transfer`() {
        val program = Program()
        val line = "TRANSFER,ACC-200,ACC-100,100.00"
        val lineNumber = 4

        val a = program.processLine(line, lineNumber)

        val expectedToResult = BigDecimal("600.00")
        val actualToResult = program.getBalance("ACC-100")

        val expectedFromResult = BigDecimal("50.00")
        val actualFromResult = program.getBalance("ACC-200")

        assertEquals(expectedToResult, actualToResult)
        assertEquals(expectedFromResult, actualFromResult)
    }

}