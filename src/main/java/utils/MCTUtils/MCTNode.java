package utils.MCTUtils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.AtomicDouble;

import utils.Board;
import utils.Move;

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

    /** Has the board been given children? */
    public boolean isExpanded;

    /** The winrate of the node */
    public AtomicDouble winRate;

    /** The stderr of the winrate */
    public AtomicDouble standardErr;

    /** The average length of a simulation from the node*/
    public AtomicDouble avgLength;

    /** The standard deviation of the lengths */
    public AtomicDouble lengthstdDev;

    public Move move;



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
        this.move = null;
    } //MCNode(Board, MCNode)

    /**
     * Add a new child to the node.
     * @param childNode The childNode to be added.
     */
    public void newChild(MCTNode childNode) {
        this.nextMoves.offer(childNode);
    } //newChild(MCTNode)





}
