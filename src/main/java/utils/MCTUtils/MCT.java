package utils.MCTUtils;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import utils.Board;
import utils.Move;
import utils.PieceMoves;
import utils.UIutils;

/**
 * A class that constructs and runs a Monte Carlo Tree Search.
 * 
 * @author Sebastian Manza
 */
public class MCT {
    /** The exploration parameter, used to balance exploration vs exploitation */
    private static final double EXPLORATION_PARAM = 0.8;

    /** The root node of the move. (i.e. the move we are exploring from) */
    MCTNode root;

    /**
     * Creates a new Monte Carlo Treee
     * 
     * @param currentMove The most recent move made.
     */
    public MCT(Board currentMove) {
        this.root = new MCTNode(currentMove, null);
    } // MCT(Board)

    /**
     * Prints the computers most likely scenario.
     * 
     * @param pen  The printwriter object to print with
     * @param root The root of the tree
     * @throws Exception if the printWriter object fails
     */
    private static void printLikelyScenario(PrintWriter pen, MCTNode root) throws Exception {
        MCTNode node = root;
        while (!node.nextMoves.isEmpty()) {
            node.currentState.printBoard(pen);
            pen.printf(
                    "Board was played %d times with a winrate of %.2f%% \n",
                    node.playOuts.get(), node.winRate.get() * 100);
            node = Collections.max(node.nextMoves, Comparator.comparingInt(n -> n.playOuts.get()));
        } // while
    } // printLikelyScenario(PrintWriter, MCTNode)

    /**
     * Searches for the best possible move in the tree.
     * 
     * @param duration The amount of time to search for
     * @return A board representing the best possible move
     * @throws Exception if something goes wrong with the PrintWriter or
     *                   ExecutorService.
     */
    public Board search(Duration duration) throws Exception {
        Instant start = Instant.now();
        Instant deadline = start.plus(duration);
        PrintWriter pen = new PrintWriter(System.out, true);

        int processors = Runtime.getRuntime().availableProcessors();

        ExecutorService executor = Executors.newFixedThreadPool(processors);

        Runnable MCTSworker = () -> {
            while (Instant.now().isBefore(deadline)) {
                try {
                    MCTNode selectedNode = select(root);
                    MCTNode expandedNode = expand(selectedNode);
                    simResultMCT winPoints = simulate(expandedNode, root);
                    backPropagate(expandedNode, winPoints.winPoints, winPoints.length);
                } catch (Exception e) {
                    e.printStackTrace();
                } // try/catch
            } // while
        };
        for (int i = 0; i < processors; i++) {
            executor.submit(MCTSworker);
        } // for
        executor.shutdown();
        executor.awaitTermination(duration.toMillis(), TimeUnit.MILLISECONDS);

        /* Find the best move based on the node that was played the most */
        if (root.nextMoves.isEmpty()) {
            return null;
        } // if
        MCTNode bestNode = Collections.max(root.nextMoves, Comparator.comparingInt(n -> n.playOuts.get()));

        /* Print information and return the move. */
        printLikelyScenario(pen, bestNode);
        pen.println("Simulated " + root.playOuts.get() + " games.");
        pen.printf("Chosen move was played %d times with a simulated win rate of %.2f%%\n",
                bestNode.playOuts.get(), bestNode.winRate.get() * 100);

        int size = root.nextMoves.size();
        for (int i = 0; i < size; i++) {
            MCTNode worst = Collections.min(root.nextMoves, Comparator.comparingInt(n -> n.playOuts.get()));
            System.out.printf(
                    "Move: %s Win CI [%.2f, %.2f] Playouts: %d, Length CI [%.2f, %.2f]\n",
                    UIutils.toNotation(worst.move), ((worst.winRate.get() * 100) - (worst.standardErr.get() * 100)), ((worst.winRate.get() * 100) + (worst.standardErr.get() * 100)), worst.playOuts.get(),
                    (worst.avgLength.get() - Math.sqrt(worst.lengthstdDev.get())), (worst.avgLength.get() + Math.sqrt(worst.lengthstdDev.get())));
            root.nextMoves.remove(worst);
        } //for


        return bestNode.currentState;
    } // search(Duration)

    /**
     * Calculates a value for a node to select using UCB1
     * 
     * @param node The node to calculate
     * @return The value of the node
     */
    private static double UCT(MCTNode node) {

        /* Make sure the last moves playouts isn't null */
        double lastMovePlayouts = node.lastMove != null ? node.lastMove.playOuts.get() : 1;

        /* Return a high value if the node has never been played */
        if (node.playOuts.get() == 0) {
            return 1000.0;
        } // if

        /* Calculate the Upper Confidence Bound */
        double UCB1 = (node.winRate.get()
                + (EXPLORATION_PARAM * Math.sqrt((Math.log(lastMovePlayouts)) / node.playOuts.get())));
        return UCB1;
    } // UCT(node)

    /**
     * Selects the best possible node from the current root with current knowledge.
     * 
     * @param node The beginning node.
     * @return The best possible node from the beginning node.
     */
    private static MCTNode select(MCTNode node) {
        while (!node.currentState.isGameOver()) {
            if (node.nextMoves.isEmpty() || node.playOuts.get() == 0) {
                return node;
            } // if

            /* Return the node with the highest UCB */
            node = Collections.max(node.nextMoves, Comparator.comparingDouble(MCT::UCT));
        } // while
        return node;
    } // select(MCTNode)

    /**
     * Expands the tree one level deeper to continue searching
     * 
     * @param node The first node reached with no children.
     * @return The expanded node.
     */
    private static MCTNode expand(MCTNode node) {
        /*
         * Synchronize the expansion process so multiple threads don't attempt to expand
         * the same node
         */
        if (!node.isExpanded) {
            synchronized (node) {
                if (!node.isExpanded) {
                    node.isExpanded = true;

                    /* Add all possible children to the node */
                    Move[] nextMoves = node.currentState.nextMoves();
                    for (Move move : nextMoves) {
                        Board gameState = PieceMoves.movePiece(move, node.currentState);
                        gameState.moveWeight = move.moveWeight;
                        gameState.turnColor = gameState.oppColor();
                        MCTNode newNode = new MCTNode(gameState, node);
                        newNode.move = move;
                        node.newChild(newNode);
                    } // for
                }
            }
        }
        /* Return the node if theres no possible next move */
        if (node.nextMoves.isEmpty()) {
            return node;
        } // if

        return node.nextMoves.peek();
    } // expand(node)

    /**
     * Randomly simulates the finish of the game from the current game state.
     * 
     * @param node The terminating node
     * @param root The original root of the tree
     * @return the number of win-points
     */

    private static simResultMCT simulate(MCTNode node, MCTNode root) {
        /* Set the current board and the maximum depth to simulate to */
        Board gameState = node.currentState;
        int depthThresh = 300;
        int depth = 0;

        /* Run the loop while the game is undecided */
        while (true) {
            Move nextMove = gameState.ranNextMove(ThreadLocalRandom.current());

            /*
             * If the game is checkmate/stalemate or if the depth is too far, end the game
             */
            if (nextMove == null || ++depth > depthThresh) {
                double vicPoints = gameState.vicPoints();
                double rew = reward(depth, vicPoints, gameState, root, root.playOuts.get());
                return new simResultMCT(rew, depth - 1);
            } // if
            gameState = PieceMoves.movePiece(nextMove, gameState);
            gameState.turnColor = gameState.oppColor();
        } // while
    } // simulate(MCTNode)

    /**
     * Increments the total wins of every previous node by the points based on
     * reward.
     * Increments total playouts by 1 for each regardless.
     * 
     * @param node      The node to backpropagate from (terminating node)
     * @param winPoints The number of points to be given.
     * @param length    The length of the simulation
     */
    private static void backPropagate(MCTNode node, double winPoints, int length) {
        MCTNode curNode = node;

        /* Synchronize the calculations */
        while (curNode != null) {
            synchronized (curNode) {

                /* Add the rewards. */
                int playOuts = curNode.playOuts.incrementAndGet();
                double wins;
                if (curNode.currentState.turnColor != curNode.currentState.engineColor) {
                    wins = curNode.wins.addAndGet(winPoints);
                } else {
                    wins = curNode.wins.addAndGet(1 - winPoints);
                } // if/else

                /* Set the new win rate */
                double winRate = wins / playOuts;
                curNode.winRate.set(winRate);

                /* Set the new standard Error */
                if (winRate == 0 || winRate == 1 || playOuts == 0) {
                    curNode.standardErr.set(0.0);
                } else {
                    double stdError = Math.sqrt((winRate * (1 - winRate)) / playOuts);
                    curNode.standardErr.set(stdError);
                } // if/else

                double oldMean = curNode.avgLength.get();
                double delta = length - oldMean;

                /* Update the mean length */
                double newMean = oldMean + delta / playOuts;
                curNode.avgLength.set(newMean);

                /* Update the length variance */
                if (playOuts > 1) {
                    double oldVariance = curNode.lengthstdDev.get();
                    double newVariance = ((playOuts - 1) * oldVariance / playOuts)
                            + (delta * (length - newMean)) / playOuts;
                    curNode.lengthstdDev.set(newVariance);
                } else {
                    curNode.lengthstdDev.set(0.0);
                } // if/else

            } // synchronized(node)
            curNode = curNode.lastMove;
        } // while
    } // backPropogate(MCTNode, double, MCTNode)

    /**
     * Returns a reward based on some heuristics and the game result.
     * 
     * @param simLength The length of the simulation
     * @param result    The result of the game (1.0, 0.5, 0.0)
     * @param gameState The ending board state
     * @param rootNode  The root of the MCT
     * @param playOuts  The number of times the root has been played
     * @return A double representing the adjusted reward.
     */
    private static double reward(int simLength, double result, Board gameState, MCTNode rootNode, int playOuts) {
        if (result == 0.5 || playOuts < 25000) {
            return result;
        } //if

        double newResult;
        double k = 1; // A double representing a weight for the sigmoid function

        double stdLength = (rootNode.avgLength.get() - simLength) / Math.sqrt(rootNode.lengthstdDev.get());
        double lengthStdDev = Math.sqrt(rootNode.lengthstdDev.get());
        if (lengthStdDev == 0) {
            return result;
        } //if
        double stdMaterial = Math.abs(gameState.material());
        double sigmoidLength = 1 / (1 + (Math.exp(-k * stdLength)));
        double sigmoidWins = 1 / (1 + (Math.exp(-k * stdMaterial)));
        double a = 1;
        double signum = (result == 0) ? -1 : 1;

        newResult = result + ((signum * a * sigmoidLength + signum * a * sigmoidWins) / 2);

        /* Map the [-1, 2] ranged function to [0, 1] */
        return (newResult + 1) / 3;
    }

} // MCT