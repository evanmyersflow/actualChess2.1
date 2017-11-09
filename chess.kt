import java.util.*
import kotlin.collections.ArrayList

const val DEBUG = true
val board = HashMap<Coordinate, Piece>()
val notation = listOf("A", "B", "C", "D", "E", "F", "G", "H")
const val SEPERATOR = ","
const val BOARD_SIZE = 7
val BOARD_RANGE = 0..BOARD_SIZE

fun main(args: Array<String>) {
    setUpBoard()

    println("Let the game begin!")

    while (true) {
        printBoard(board)
        val scanner = Scanner(System.`in`)
        val input = scanner.nextLine().toUpperCase()

        val (from, to) = try {
            input.split(SEPERATOR).let {
                Coordinate.parse(it.first().trim()) to Coordinate.parse(it.last().trim())
            }
        } catch (e: IllegalStateException) {
            println("You're dumb!")
            continue
        }

        try {
            val chosenPiece = board[from]
            if (chosenPiece == null) {
            } else {
                val validMoves = chosenPiece.validMoves

                if (DEBUG) printBoard(validMoves.associate { it to PlaceholderPiece(it) })

                if (validMoves.contains(to)) {
                    val oldPiece = board.remove(from)!!
                    board[to] = when (oldPiece) {
                        is Rook -> Rook(oldPiece.isWhite, to)
                        is Queen -> Queen(oldPiece.isWhite, to)
                        is Bishop -> Bishop(oldPiece.isWhite, to)
                        is Knight -> Knight(oldPiece.isWhite, to)
                        is King -> King(oldPiece.isWhite, to)
                        is Pawn -> Pawn(oldPiece.isWhite, to)
                        else -> TODO()
                    }
                }
            }
        } catch (e: NotImplementedError) {
            continue
        }
    }
}

private fun printBoard(board: Map<Coordinate, Piece>) {
    println()
    for (i in BOARD_SIZE downTo 0) {
        for (j in BOARD_RANGE) {
            val coordinate = Coordinate(j, i)
            val piece: Piece = board[coordinate] ?:
                    letOnSquare(coordinate) { NullPiece(it, coordinate) }

            print(if (j in 0..6) "$piece " else piece)
        }
        println()
    }
    println()
}

private fun setUpBoard() {
    setUpSide(true)
    setUpSide(false)
}

private fun setUpSide(white: Boolean) {
    val row1Y = if (white) 0 else BOARD_SIZE
    addPiece(Rook(white, Coordinate(0, row1Y)))
    addPiece(Rook(white, Coordinate(7, row1Y)))
    addPiece(Knight(white, Coordinate(1, row1Y)))
    addPiece(Knight(white, Coordinate(6, row1Y)))
    addPiece(Bishop(white, Coordinate(2, row1Y)))
    addPiece(Bishop(white, Coordinate(5, row1Y)))
    addPiece(Queen(white, Coordinate(3, row1Y)))
    addPiece(King(white, Coordinate(4, row1Y)))

    val row2Y = if (white) 1 else 6
    addPiece(Pawn(white, Coordinate(0, row2Y)))
    addPiece(Pawn(white, Coordinate(1, row2Y)))
    addPiece(Pawn(white, Coordinate(2, row2Y)))
    addPiece(Pawn(white, Coordinate(3, row2Y)))
    addPiece(Pawn(white, Coordinate(4, row2Y)))
    addPiece(Pawn(white, Coordinate(5, row2Y)))
    addPiece(Pawn(white, Coordinate(6, row2Y)))
    addPiece(Pawn(white, Coordinate(7, row2Y)))
}

private fun addPiece(piece: Piece) {
    board.put(piece.coordinate, piece)
}

interface Piece {
    val isWhite: Boolean

    val coordinate: Coordinate

    val validMoves: List<Coordinate>
}

data class Coordinate(val x: Int, val y: Int) {
    companion object {
        fun parse(input: String): Coordinate {
            if (input.length != 2) {
                throw IllegalStateException()
            }
            val x = notation.indexOf(input.first().toString())

            if (x < 0) {
                throw  IllegalStateException()
            }

            return Coordinate(x, input.last().toString().toInt() - 1)
        }
    }
}

data class Rook(override val isWhite: Boolean, override val coordinate: Coordinate) : Piece {
    override val validMoves: List<Coordinate>
        get() = getUpDownMoves(coordinate)

    override fun toString(): String = if (isWhite) "Rw" else "Rb"
}

data class Knight(override val isWhite: Boolean, override val coordinate: Coordinate) : Piece {
    override val validMoves: List<Coordinate>
        get() = getKnightMoves(coordinate)

    override fun toString(): String = if (isWhite) "Nw" else "Nb"
}

data class Bishop(override val isWhite: Boolean, override val coordinate: Coordinate) : Piece {
    override val validMoves: List<Coordinate>
        get() = getDiagonAlleyMoves(coordinate)

    override fun toString(): String = if (isWhite) "Bw" else "Bb"
}

data class Queen(override val isWhite: Boolean, override val coordinate: Coordinate) : Piece {
    override val validMoves: List<Coordinate>
        get() {
            val validMoves = ArrayList<Coordinate>()
            validMoves.addAll(getUpDownMoves(coordinate))
            validMoves.addAll(getDiagonAlleyMoves(coordinate))
            return validMoves.distinct()
        }

    override fun toString(): String = if (isWhite) "Qw" else "Qb"
}

data class King(override val isWhite: Boolean, override val coordinate: Coordinate) : Piece {
    override val validMoves: List<Coordinate>
        get() {
            val validMoves = ArrayList<Coordinate>()
            validMoves.addAll(getUpDownMoves(coordinate, 1))
            validMoves.addAll(getDiagonAlleyMoves(coordinate, 1))
            return validMoves.distinct()
        }

    override fun toString(): String = if (isWhite) "Kw" else "Kb"
}

data class Pawn(override val isWhite: Boolean, override val coordinate: Coordinate) : Piece {
    override val validMoves: List<Coordinate>
        get() {
            val validMoves = ArrayList<Coordinate>()
            if (isWhite) {
                validMoves += getWhitePawnMoves(coordinate)
            } else {
                validMoves += getBlackPawnMoves(coordinate)
            }
            if (coordinate.y == 1 && isWhite) {
                validMoves += Coordinate(coordinate.x, coordinate.y + 2)
            }
            if (coordinate.y == 6 && !isWhite) {
                validMoves += Coordinate(coordinate.x, coordinate.y - 2)
            }

            return validMoves
        }

    override fun toString(): String = if (isWhite) "Pw" else "Pb"
}


data class NullPiece(override val isWhite: Boolean, override val coordinate: Coordinate) : Piece {
    override val validMoves: List<Coordinate>
        get() = emptyList()

    override fun toString(): String = if (isWhite) "~ " else "- "
}

data class PlaceholderPiece(override val coordinate: Coordinate) : Piece {
    override val isWhite = false
    override val validMoves: List<Coordinate> = emptyList()

    override fun toString(): String = "X "
}

private fun getUpDownMoves(coordinate: Coordinate, size: Int = BOARD_SIZE): List<Coordinate> {
    val validMoves = ArrayList<Coordinate>()

    (coordinate.y..Math.min(coordinate.y + size, BOARD_SIZE))
            .map { Coordinate(coordinate.x, it) }
            .filter { coordinate != it }
            .forEach { validMoves += it }
    (coordinate.y downTo Math.max(coordinate.y - size, 0))
            .map { Coordinate(coordinate.x, it) }
            .filter { coordinate != it }
            .forEach { validMoves += it }

    (coordinate.x..Math.min(size + coordinate.x, BOARD_SIZE))
            .map { Coordinate(it, coordinate.y) }
            .filter { coordinate != it }
            .forEach { validMoves += it }
    (coordinate.x downTo Math.max(coordinate.x - size, 0))
            .map { Coordinate(it, coordinate.y) }
            .filter { coordinate != it }
            .forEach { validMoves += it }

    return validMoves.distinct()
}

private fun getDiagonAlleyMoves(coordinate: Coordinate, size: Int = BOARD_SIZE): List<Coordinate> {
    val validMoves = ArrayList<Coordinate>()

    for ((index, position) in (coordinate.y..Math.min(coordinate.y + size, BOARD_SIZE)).withIndex()) {
        validMoves += Coordinate(coordinate.x + index, position)
        validMoves += Coordinate(coordinate.x - index, position)
    }
    for ((index, position) in (coordinate.y downTo Math.max(coordinate.y - size, 0)).withIndex()) {
        validMoves += Coordinate(coordinate.x + index, position)
        validMoves += Coordinate(coordinate.x - index, position)
    }

    return validMoves.distinct()
}

private fun getWhitePawnMoves(coordinate: Coordinate, size: Int = BOARD_SIZE): List<Coordinate> {
    val validMoves = ArrayList<Coordinate>()

    validMoves += Coordinate(coordinate.x, coordinate.y + 1)
    validMoves += Coordinate(coordinate.x - 1, coordinate.y + 1)
    validMoves += Coordinate(coordinate.x + 1, coordinate.y + 1)

    return  validMoves
}

private fun getBlackPawnMoves(coordinate: Coordinate, size: Int = BOARD_SIZE): List<Coordinate> {
    val validMoves = ArrayList<Coordinate>()

    validMoves += Coordinate(coordinate.x, coordinate.y - 1)
    validMoves += Coordinate(coordinate.x - 1, coordinate.y - 1)
    validMoves += Coordinate(coordinate.x + 1, coordinate.y - 1)

    return  validMoves
}

private fun getWhitePromotion() {
    if (isWhite && Coordinate.y == 7)

}

private fun getKnightMoves(coordinate: Coordinate, size: Int = BOARD_SIZE): List<Coordinate> {
    val validMoves = ArrayList<Coordinate>()

    validMoves += Coordinate(coordinate.x + 1, coordinate.y + 2)
    validMoves += Coordinate(coordinate.x - 1, coordinate.y + 2)
    validMoves += Coordinate(coordinate.x + 1, coordinate.y - 2)
    validMoves += Coordinate(coordinate.x - 1, coordinate.y - 2)
    validMoves += Coordinate(coordinate.x + 2, coordinate.y + 1)
    validMoves += Coordinate(coordinate.x - 2, coordinate.y + 1)
    validMoves += Coordinate(coordinate.x + 2, coordinate.y - 1)
    validMoves += Coordinate(coordinate.x - 2, coordinate.y - 1)

    return validMoves

}

fun <T> letOnSquare(coordinate: Coordinate, block: (Boolean) -> T): T {
    return if (coordinate.y % 2 == 0) {
        if (coordinate.x % 2 == 0) {
            block(true)
        } else {
            block(false)
        }
    } else {
        if (coordinate.x % 2 == 0) {
            block(false)
        } else {
            block(true)
        }
    }
}

