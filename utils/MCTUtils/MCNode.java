package utils.MCTUtils;

import utils.GameState;
public class MCNode {

    public MCNode (GameState curState, MCNode parentState) {
        this.currentState = curState;
        this.wins = 0.0;
        this.playOuts = 0;
        this.lastState = parentState;

    }
    /**
     * The current move/game state
     */
    public GameState currentState;
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
