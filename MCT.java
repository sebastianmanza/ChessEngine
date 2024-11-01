import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.lang.Math;

import utils.MCTUtils.MCNode;
import utils.MCTUtils.move;

/**
 * A class that constructs and runs a Monte Carlo Tree Search.
 * @author Sebastian Manza
 */
public class MCT {
    /** The exploration parameter, used to balance exploration vs exploitation */
    public static final double EXPLORATION_PARAM = Math.sqrt(2);
    /** The root node of the move. */
    MCNode root;

    /** Creates a new Monte Carlo Treee
     * @param currentMove The most recent move made.
     */
    public MCT(move currentMove) {
        this.root = new MCNode(currentMove);
    }
    /* Stores the current best move that can be made */
    move bestMove;

    /**
     * Searches for the best possible move to be made from the current game state.
     * @param iterations The number of iterations to attempt
     * @return The best possible move
     */
    public move search(int iterations) {
        for (int i = 0; i < iterations; i++) {
            MCNode selectedNode = select(root);
            MCNode expandedNode = expand(selectedNode);
            double winPoints = simulate(expandedNode);
            backPropagate(expandedNode, winPoints);

            /* Find and update the best move amongst all futureMoves from the root */
            bestMove = Collections.max(root.nextMoves, Comparator.comparingInt(n -> n.totalPlayOuts)).currentMove;
        }
         return bestMove;
    }

    /**
     * Calculates a value for a node to select
     * @param node The node to calculate
     * @return The value of the node
     */
    public static double UCT(MCNode node) {
        double result = node.totalWins / node.totalPlayOuts + EXPLORATION_PARAM * Math.sqrt((Math.log(node.lastMove.totalPlayOuts)) / node.totalPlayOuts);
        return result;
    } //UCT(node)
    
    /**
     * Selects the best possible node from the current root with current knowledge.
     * @param node The beginning node.
     * @return The best possible node from the beginning node.
     */
    public static MCNode select(MCNode root) {
        MCNode curNode = root;
        while (hasExploredChildren) {
            curNode = Collections.max(curNode.nextMoves, Comparator.comparingDouble(n -> UCT(n)));
        } //while
        return curNode;
    } // select(root)

    /**
     * Expands the tree one level deeper to continue searching
     * @param node The first node reached with no children.
     * @return The expanded node.
     */
    public static MCNode expand(MCNode node) {
        Random randomChild = new Random();
        /* Grab a random child from the tree */
        return node.nextMoves.get(randomChild.nextInt(node.nextMoves.size()));
    } //expand(node)

    /**
     * Randomly simulates the finish of the game from the current game state. 
     * Note: current implementation creates lists of all future moves and chooses randomly. 
     * Could be made more efficient by creating just one random move?
     * @param node The terminating nodegf
     * @return the number of win-points
     */
    public static double simulate(MCNode node) {
        Random ranMove = new Random();
        MCNode curMove = node;
        /* Run the loop while the game is undecided */
        while(!gameState.gameFinished(curMove)) {
            /* take a random move and play from that position */
            curMove = curMove.nextMoves.get(ranMove.nextInt(curMove.nextMoves.size()));
        } // while
        return curMove.vicPoints();
    } //simulate

    /**
     * Increments the total wins of every previous node by 1.0 if won, 0.5 if drawn, and 0 if lost. Increments total playouts by 1 for each regardless.
     * @param node The node to backpropagate from (terminating node)
     * @param winPoints The number of points to be given.  
     */
    public void backPropagate(MCNode node, double winPoints) {
        MCNode curNode = node;
        while(curNode != this.root) {
            curNode.totalPlayOuts++;
            curNode.totalWins += winPoints;
            curNode = curNode.lastMove;
        } // while
    } //backPropogate
}
