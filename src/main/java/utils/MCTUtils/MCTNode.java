package utils.MCTUtils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.AtomicDouble;

import utils.Board;

/**
 * A thread-safe variation of MCNode for asynchronous work.
 */
public class MCTNode {

    /**
     * The current boardstate
     */
    public Board currentState;

    /**
     * The list of all possible next nodes
     */
    public ConcurrentLinkedQueue<MCTNode> nextMoves;
    /**
     * The total wins/draws of the node
     */
    public AtomicDouble wins;
    /**
     * The number of times this was attempted
     */
    public AtomicInteger playOuts;
    /**
     * The parent node (last game state)
     */
    public MCTNode lastMove;

    public boolean isExpanded;

    public AtomicDouble winRate;

    public AtomicDouble standardErr;

    public AtomicDouble avgLength;

    public AtomicDouble lengthstdDev;



    /**
     * Create a new Monte Carlo node for use in the tree
     * @param curState The current board.
     * @param parentNode The node that came before
     */
    public MCTNode (Board curState, MCTNode parentNode) {
        this.currentState = curState;
        this.wins = new AtomicDouble(0.0);
        this.playOuts = new AtomicInteger(0);
        this.lastMove = parentNode;
        this.nextMoves = new ConcurrentLinkedQueue<>();
        this.isExpanded = false;
        this.winRate = new AtomicDouble(0.0);
        this.standardErr = new AtomicDouble(0.0);
        this.avgLength = new AtomicDouble(0.0);
        this.lengthstdDev = new AtomicDouble(0.0);

    } //MCNode(Board, MCNode)

    /**
     * Add a new child to the node.
     * @param childNode The childNode to be added.
     */
    public void newChild(MCTNode childNode) {
        this.nextMoves.offer(childNode);
    }





}
