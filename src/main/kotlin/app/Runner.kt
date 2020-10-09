package app

import kotlin.system.exitProcess
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import kotlin.concurrent.thread

/**
 * DON'T EDIT THIS FILE (edit AI.kt instead)
 *
 * This file contains the Runner class, which manages the connection with the server
 * It waits for a board to solve, and passes it to the AI when the server gives it one
 */

class SuccessResponse(val success: Boolean, val error: String?)
class NeededResponse(val needed: Boolean?, val error: String?)
class InternalBoardResponse(val board: Array<Array<Int>>)
class BoardResponse(val state: InternalBoardResponse)

class Runner(val ai: AI, val apiUrl: String, val apiKey: String, val gameId: String, val refreshInterval: Long) {
    val gson = Gson()

    fun joinGame() {
        val (_, resp, _) = "${apiUrl}/api/game/${gameId}/join".httpPost().header("X-API-KEY", apiKey).responseString()

        val didError = gson.fromJson(resp.body().asString("application/json"), SuccessResponse::class.java)
        if(didError.success == false) System.err.println("Error joining game: ${didError.error}")
        else println("Joined Game ${gameId} successfully")
    }


    fun moveNeeded(): Boolean {
        val (_, response, _) = "${apiUrl}/api/game/${gameId}/move_needed".httpGet().header("X-API-KEY", apiKey).responseString()
        val needed = gson.fromJson(response.body().asString("application/json"), NeededResponse::class.java)
        if (needed.error != null || needed.needed == null) {
            System.err.println("Error checking move needed: ${needed.error}")
        } else {
            return needed.needed
        }

        return false
    }

    fun getBoard(): Board? {
        val (_, response, _) = "${apiUrl}/api/game/${gameId}".httpGet().header("X-API-KEY", apiKey).responseString()

        if(response.statusCode != 200) {
            System.err.println("Error loading board")
            return null
        }
        val boardJson = gson.fromJson(response.body().asString("application/json"), BoardResponse::class.java) ?: return null

        val res = Array(15) { Array(15) { BoardPlayer.NONE } }
        for(x in 0 until 15) {
            for(y in 0 until 15) {
                if(boardJson.state.board[x][y] == 0) {
                    res[x][y] = BoardPlayer.US
                } else if(boardJson.state.board[x][y] == 1) {
                    res[x][y] = BoardPlayer.THEM
                }
            }
        }

        return Board(res)
    }

    fun makeMove(move: Pair<Int, Int>) {
        val (x, y) = move
        "${apiUrl}/api/game/${gameId}/move".httpPost(
            listOf(
                Pair("x", x),
                Pair("y", y)
            )
        ).header("X-API-KEY", apiKey).response { _, response, _ ->
            val didError = gson.fromJson(response.body().asString("application/json"), SuccessResponse::class.java)
            if(didError.error != null) System.err.println("Error making move: ${didError.error}")
            else println("Successfully made move")
        }
    }

    fun mainLoop() {
        joinGame()
        while(true) {
            if(moveNeeded()) {
                println("Solving Board...")
                val board = getBoard()
                if(board != null) {
                    val move = ai.makeMove(board)
                    println("Making move: ${move.first}, ${move.second}")
                    makeMove(move)
                }
            }
            Thread.sleep(refreshInterval)
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            if(args.size < 3) {
                println("Expected URL as first argument, api key as second argument, and game id as third argument")
                exitProcess(1)
            }

            val url = args[0]
            val key = args[1]
            val game_id = args[2]

            val run = Runner(AI(), url, key, game_id, 1000)
            println("Starting. API URL: ${url}, API KEY: ${key}, Game ID: ${game_id}")
            run.mainLoop()
        }
    }
}