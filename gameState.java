import utils.chessBoard;
import utils.MCTUtils.MCNode;
/**
 * Tracks the current state of the game. 
 * @author Sebastian Manza
 */

public class gameState {
    enum color {
        BLACK,
        WHITE
    }
    color turn;
    color engineColor;
    MCNode curNode;

    /**
     * Returns true if the game is completed, i.e. if there are no possible next moves, else return false.
     * @param gameState The current node that the game is on.
     */
    public static boolean gameFinished(MCNode gameState) {
        return gameState.nextMoves().isEmpty();
    } //gameFinished(gameState)

    /**
     * Returns the number of points won by the engine from the current gameState
     * @param board The current chessBoard
     * @throws Exception if the game is unfinished
     */
    public double vicPoints(chessBoard board) throws Exception{
        if (!gameFinished(this.curNode)) {
            throw new Exception("The game is not complete.");
        } // if
        /* Check if it's the engines turn */
        if (turn == engineColor) {
            if (engineColor.inCheck(board)) {
                return 0.0;
            } else {
                return 0.5;
            } //if/else
        } else {
            return 1.0;
        } //if/else
    } //vicPoints

    /** A boolean that returns true if the current color is in check, else false.
     * @param board The current chessboard
     */
    public boolean inCheck(chessBoard board) {
        return true; //STUB
    }
}

