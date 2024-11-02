package utils.MCTUtils;

/** A class that represents a possible move that can be made
 * @author Sebastian Manza
 */
public class move {
    String startingSquare;
    String endingSquare;
    
    public move (piece piece, String moveTo) {
        this.endingSquare = moveTo;
        this.startingSquare = piece.square;
    } //move

    /**
     * Distinctly different from move in that move is a possibility, movePiece is the execution.
     * @param move Takes in a move to execute
     */
    public void executeMove(move move) {
        return; //STUB
    }

}