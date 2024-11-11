package utils.MCTUtils;

import java.util.List;
import utils.CurrentGameState;

public class MCNode {

    /**
     * The current move/game state
     */
    public CurrentGameState currentState;
    /**
     * The list of all possible nextMoves
     */
    public List<MCNode> nextMoves;
    /**
     * The total wins/draws of the node
     */
    public double totalWins;
    /**
     * The number of times this was attempted
     */
    public int totalPlayOuts;
    /**
     * The parent node (last game state)
     */
    public MCNode lastMove;

}
