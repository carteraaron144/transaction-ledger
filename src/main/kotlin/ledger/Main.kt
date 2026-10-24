package ledger

import java.io.File
import java.math.BigDecimal

fun main() {
    val FILE_PATH = "transactions.txt"
    var program = Program()


    var lines = program.readFile(FILE_PATH)

    program.run(lines)

    program.printOutput()
}
