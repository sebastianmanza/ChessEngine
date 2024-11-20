package utils.MCTUtils;

import java.util.ArrayList;
import java.util.List;
import utils.Board;

public class MCNode {

    /**
     * The current move/game state
     */
    public Board currentState;
    /**
     * The list of all possible nextMoves
     */
    public List<MCNode> nextMoves;
    /**
     * The total wins/draws of the node
     */
    public double wins;
    /**
     * The number of times this was attempted
     */
    public int playOuts;
    /**
     * The parent node (last game state)
     */
    public MCNode lastMove;

    /**
     * Create a new Monte Carlo node for use in the tree
     * @param curState The current board.
     * @param parentNode The node that came before
     */
    public MCNode (Board curState, MCNode parentNode) {
        this.currentState = curState;
        this.wins = 0.0;
        this.playOuts = 1;
        this.lastMove = parentNode;
        this.nextMoves = new ArrayList<>();
    } //MCNode(Board, MCNode)

    /**
     * Add a new child to the node.
     * @param childNode The childNode to be added.
     */
    public void newChild(MCNode childNode) {
        this.nextMoves.add(childNode);
    }



}
