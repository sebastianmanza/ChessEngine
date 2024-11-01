package utils.MCTUtils;
import java.util.List;
public class MCNode {
    move currentMove;
    List<MCNode> nextMoves;
    double totalWins;
    int totalPlayOuts;
    MCNode lastMove;

    public MCNode(move move) {
        this.currentMove = move;
    }

    /** Returns a list of all possible next moves */
    public List<MCNode> nextMoves() {
        return nextMoves;
    } //nextMoves


}