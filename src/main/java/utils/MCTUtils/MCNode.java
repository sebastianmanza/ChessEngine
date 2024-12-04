package utils.MCTUtils;

import java.util.ArrayList;
import java.util.List;

import utils.Board;
import utils.Move;

public class MCNode {

    /**
     * The current boardstate
     */
    public Board currentState;

    /**
     * The last move applied.
     */
    public Move move;

    /**
     * The list of all possible next nodes
     */
    public List<MCNode> nextMoves;
    /**
     * The total wins/draws of the node
     */
    public double wins;

    /**
     * The total wins of the move played at any time
     */
    public double AMAFwins;

    /**
     * The total times this move was played
     */
    public int AMAFplayOuts;
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
        this.AMAFwins = 0.0;
        this.playOuts = 0;
        this.AMAFplayOuts = 0;
        this.lastMove = parentNode;
        this.move = null;
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
