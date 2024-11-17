package utils.MCTUtils;

import utils.Board;
public class MCNode {

    public MCNode (Board curState, MCNode parentState) {
        this.currentState = curState;
        this.wins = 0.0;
        this.playOuts = 0;
        this.lastState = parentState;

    }
    /**
     * The current move/game state
     */
    public Board currentState;
    /**
     * The list of all possible nextMoves
     */
    public MCNode[] nextMoves;
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
    public MCNode lastState;

}
