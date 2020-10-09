package app

/**
 * YOU SHOULD EDIT THIS FILE
 */

class AI {
    fun makeMove(state: Board): Pair<Int, Int> {
        // find the first legal move
        for(x in 0 until 15) {
            for(y in 0 until 15) {
                if(state.board[x][y] == BoardPlayer.NONE) {
                    return Pair(x, y)
                }
            }
        }

        System.err.println("No Valid moves found")
        return Pair(0, 0)
    }
}