package utils.MCTUtils;

import java.util.List;

public class MCNode {

    /**
     * The current move/game state
     */
    public move currentMove;
    /**
     * The list of all possible nextMoves
     */
    public List<MCNode> nextMoves;
    /**
     * The total wins of the node
     */
    public double totalWins;
    /**
     * The number of times this woas attempted
     */
    public int totalPlayOuts;
    /**
     * The parent node (last game state)
     */
    public MCNode lastMove;

    /**
     * Constructor for MCNode
     */
    public MCNode(move move) {
        this.currentMove = move;

    }

}
