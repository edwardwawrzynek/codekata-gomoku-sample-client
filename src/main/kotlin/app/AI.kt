package app

/**
 * YOU SHOULD EDIT THIS FILE
 */

class AI {
    /**
     * Given a board, decide what move to make
     * the board is passed as a Board object. To get the contents of the cell x, y, check state.board[x][y],
     * which is either GamePlayer.NONE, GamePlayer.US, or GamePlayer.THEM
     *
     * Return your move as a Pair(x, y)
     */
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