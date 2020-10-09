package app

/**
 * YOU CAN EDIT THIS FILE
 * Utility functions for representing and operating on the game board
 * You may want to add helper methods to the Board class
 */

// a player on a board
enum class BoardPlayer {
    NONE, US, THEM
}

// a 15x15 gomoku board
// you could add utility functions here
// such as finding legal moves, checking wins, etc
// board is indexed [x][y]
class Board(val board: Array<Array<BoardPlayer>>) {

}