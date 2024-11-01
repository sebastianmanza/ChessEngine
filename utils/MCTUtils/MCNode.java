package utils.MCTUtils;
import java.util.List;
public class MCNode {
    public move currentMove;
    public List<MCNode> nextMoves;
    public double totalWins;
    public int totalPlayOuts;
    public MCNode lastMove;

    public MCNode(move move) {
        this.currentMove = move;

    }

    


}