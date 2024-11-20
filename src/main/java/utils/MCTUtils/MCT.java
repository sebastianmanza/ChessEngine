package utils.MCTUtils;


import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import utils.Board;

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
    public MCT(Board currentMove) {
        this.root = new MCNode(currentMove, null);
    }


    /**
     * Searches for the best possible move to be made from the current game state.
     * @param iterations The number of iterations to attempt
     * @return The best possible move
     */
    public Board search(Duration duration) throws Exception {
        Instant start = Instant.now();
        int iterations = 0;
        MCNode bestNode = root;
    
        while (Duration.between(start, Instant.now()).compareTo(duration) < 0) {
            MCNode selectedNode = select(root);
            MCNode expandedNode = expand(selectedNode);
            double winPoints = simulate(expandedNode);
            backPropagate(expandedNode, winPoints, root);
    
            /* Find the best move based on win rate */
            bestNode = Collections.max(root.nextMoves, Comparator.comparingDouble(n ->
                n.playOuts == 0 ? 0 : n.wins / n.playOuts));
            iterations++;
        }
    
        System.out.println("Simulated " + iterations + " games");
        System.out.printf("Best move was played %d times with win rate %.2f%%\n",
            bestNode.playOuts, (bestNode.wins / bestNode.playOuts) * 100);
    
        return bestNode.currentState;
    }

    /**
     * Calculates a value for a node to select
     * @param node The node to calculate
     * @return The value of the node
     */
    public static double UCT(MCNode node) {
        if (node.playOuts == 0) {
            return Double.MAX_VALUE;
        }
        double lastMovePlayouts = node.lastMove != null ? node.lastMove.playOuts : 1;
        double result = node.wins / node.playOuts + (EXPLORATION_PARAM * Math.sqrt((Math.log(lastMovePlayouts)) / node.playOuts));
        return result;
    } //UCT(node)
    
    /**
     * Selects the best possible node from the current root with current knowledge.
     * @param node The beginning node.
     * @return The best possible node from the beginning node.
     */
    public static MCNode select(MCNode node) {
        while (!node.currentState.isGameOver()) {
            if (node.nextMoves.isEmpty()) {
                return node;
            } //if
            Collections.shuffle(node.nextMoves);
            node = Collections.max(node.nextMoves, Comparator.comparingDouble(MCT::UCT));
        } //while
        return node;
    } // select(MCNode)

    /**
     * Expands the tree one level deeper to continue searching
     * @param node The first node reached with no children.
     * @return The expanded node.
     */
    public static MCNode expand(MCNode node) {
        Random randomChild = new Random();
        /* Add all possible children to the node */
        Board[] gameStates = node.currentState.nextMoves();
        for (Board gameState : gameStates) {
            MCNode newNode = new MCNode(gameState, node);
            node.newChild(newNode);
        } //for
        /* Grab a random child from the tree */
        if (node.nextMoves.isEmpty()) {
            return node;
        } //if
        return node.nextMoves.get(randomChild.nextInt(node.nextMoves.size()));
    } //expand(node)

    /**
     * Randomly simulates the finish of the game from the current game state. 
     * Note: current implementation creates lists of all future moves and chooses randomly. 
     * Could be made more efficient by creating just one random move?
     * @param node The terminating node
     * @return the number of win-points
     */
    // public static double simulate(MCNode node) throws Exception{
    //     Random ranMove = new Random();
    //     Board gameState = node.currentState;
    //     int n = 0;
    //     /* Run the loop while the game is undecided */
    //     while(!gameState.isGameOver()) {
    //         Board[] gameStates = gameState.nextMoves();
    //         Board nextGameState = gameStates[ranMove.nextInt(gameStates.length)];
    //         gameState = nextGameState;
    //     } // while
    //     return gameState.vicPoints();
    // } //simulate

    public static double simulate(MCNode node) throws Exception{
        Board gameState = node.currentState;
        int depth = 0; 

        /* Run the loop while the game is undecided */
        while(!gameState.isGameOver()) {
            Board nextGameState = gameState.ranMove();
            if (nextGameState == null) {
                break;
            } //if
            gameState = nextGameState;
            if (gameState.turnColor == gameState.engineColor) {
                if (gameState.material() < -10) {
                    return 0.0;
                } else if (gameState.material() > 10) {
                    return 1.0;
                }
            }
            if (depth++ >= 200) {
                return 0.5;
            } //if
        } // while
        return gameState.vicPoints();
    } //simulate

    /**
     * Increments the total wins of every previous node by 1.0 if won, 0.5 if drawn, and 0 if lost. Increments total playouts by 1 for each regardless.
     * @param node The node to backpropagate from (terminating node)
     * @param winPoints The number of points to be given.  
     */
    public static void backPropagate(MCNode node, double winPoints, MCNode root) {
        MCNode curNode = node;
        while (curNode != root) {
            curNode.playOuts++;
            curNode.wins += winPoints;
            curNode = curNode.lastMove;
        } // while
        /* Make sure the root is properly incremented. */
        root.playOuts++;
        root.wins += winPoints;
    } // backPropogate(node, winPoints, root)
} //MCT
