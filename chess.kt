import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

const val DEBUG = false
val board = HashMap<Coordinate, Piece>()
val notation = listOf("A", "B", "C", "D", "E", "F", "G", "H")
val pieceNotation = listOf("R", "N", "K", "B", "Q")
val scanner = Scanner(System.`in`)
const val SEPERATOR = ","
const val BOARD_SIZE = 7
val BOARD_RANGE = 0..BOARD_SIZE

fun main(args: Array<String>) {
    setUpBoard()

    println("Let the game begin!")

    loop@ while (true) {
        printBoard(board)
        val input = scanner.nextLine().toUpperCase()

        val (from, to) = try {
            input.split(SEPERATOR).let {
                Coordinate.parse(it.first().trim()) to Coordinate.parse(it.last().trim())
            }
        } catch (e: IllegalStateException) {
            println("Illegal move!")
            continue
        }

        val chosenPiece = board[from]
        if (chosenPiece == null) {
            println("That's not a piece!")
        } else {
            val validMoves = chosenPiece.validMoves

            if (DEBUG) printBoard(validMoves.associate { it to PlaceholderPiece(it) })

            if (validMoves.contains(to)) {
                val toPiece = board[to]

                val moveBranches = HashMap<MoveBranch, List<Coordinate>>()

                for (branch in MoveBranch.values()) {
                    moveBranches.put(branch, when (branch) {
                        MoveBranch.UP -> {
                            validMoves.filter {
                                chosenPiece.coordinate.x == it.x && chosenPiece.coordinate.y < it.y
                            }
                        }
                        MoveBranch.DOWN -> {
                            validMoves.filter {
                                chosenPiece.coordinate.x == it.x && chosenPiece.coordinate.y > it.y
                            }
                        }
                        MoveBranch.LEFT -> {
                            validMoves.filter {
                                chosenPiece.coordinate.x > it.x && chosenPiece.coordinate.y == it.y
                            }
                        }
                        MoveBranch.RIGHT -> {
                            validMoves.filter {
                                chosenPiece.coordinate.x < it.x && chosenPiece.coordinate.y == it.y
                            }
                        }
                        MoveBranch.UP_LEFT ->
                            validMoves.filter {
                                chosenPiece.coordinate.x > it.x && chosenPiece.coordinate.y < it.y
                            }
                        MoveBranch.UP_RIGHT ->
                            validMoves.filter {
                                chosenPiece.coordinate.x < it.x && chosenPiece.coordinate.y < it.y
                            }
                        MoveBranch.DOWN_LEFT -> {
                            validMoves.filter {
                                chosenPiece.coordinate.x > it.x && chosenPiece.coordinate.y > it.y
                            }
                        }
                        MoveBranch.DOWN_RIGHT ->
                            validMoves.filter {
                                chosenPiece.coordinate.x > it.x && chosenPiece.coordinate.y < it.y
                            }
                    })
                }

                /*
                1. Get all the move branches
                2. Put the moves in the map
                3. Write a method to get which branch the piece is moving along
                4. moveBranches[branchThePieceIsMovingOn] and use those moves to see if a piece is in the way
                */
                for (move in validMoves) {

                }

                val isCapturing = toPiece != null
                if (isCapturing && chosenPiece.isWhite == toPiece!!.isWhite) {
                    println("You can't take your own piece!")
                    continue
                }

                val oldPiece = board.remove(from)!!
                board[to] = when (oldPiece) {
                    is Rook -> Rook(oldPiece.isWhite, to)
                    is Queen -> Queen(oldPiece.isWhite, to)
                    is Bishop -> Bishop(oldPiece.isWhite, to)
                    is Knight -> Knight(oldPiece.isWhite, to)
                    is King -> King(oldPiece.isWhite, to)
                    is Pawn -> {
                        if (oldPiece.isWhite && to.y == 7
                                || !oldPiece.isWhite && to.y == 0) {
                            println("Promote to a: ")
                            val newPieceName = scanner.nextLine().toUpperCase().trim()
                            if (pieceNotation.contains(newPieceName)) {
                                when (newPieceName) {
                                    "Q", "Queen" -> Queen(oldPiece.isWhite, to)
                                    "R", "Rook" -> Rook(oldPiece.isWhite, to)
                                    "N", "K", "Knight" -> Knight(oldPiece.isWhite, to)
                                    "B", "Bishop" -> Bishop(oldPiece.isWhite, to)
                                    else -> throw IllegalStateException("That's not a piece!")
                                }
                            } else {
                                println("You can't promote!")
                                continue@loop
                            }
                        } else {
                            Pawn(oldPiece.isWhite, to)
                        }
                    }
                    else -> throw IllegalStateException("Illegal move!")
                }
            }
        }
    }
}

fun <T, U> HashMap(UP: Boolean, DOWN: Boolean, RIGHT: Boolean, LEFT: Boolean, UP_RIGHT: Boolean, UP_LEFT: Boolean, DOWN_RIGHT: Boolean, DOWN_LEFT: Boolean): Any {}

enum class MoveBranch {
    UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
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
            validMoves += if (isWhite) {
                getWhitePawnMoves(coordinate)
            } else {
                getBlackPawnMoves(coordinate)
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

    return validMoves
}

private fun getBlackPawnMoves(coordinate: Coordinate, size: Int = BOARD_SIZE): List<Coordinate> {
    val validMoves = ArrayList<Coordinate>()

    validMoves += Coordinate(coordinate.x, coordinate.y - 1)
    validMoves += Coordinate(coordinate.x - 1, coordinate.y - 1)
    validMoves += Coordinate(coordinate.x + 1, coordinate.y - 1)

    return validMoves
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
