package utils.MCTUtils;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import utils.Board;
import utils.Move;
import utils.PieceMoves;

/**
 * A class that constructs and runs a Monte Carlo Tree Search.
 * 
 * @author Sebastian Manza
 */
public class MCT {
    /** The exploration parameter, used to balance exploration vs exploitation */
    public static final double EXPLORATION_PARAM = 0.8;

    /** The root node of the move. (i.e. the move we are exploring from) */
    MCTNode root;

    /**
     * Creates a new Monte Carlo Treee
     * 
     * @param currentMove The most recent move made.
     */
    public MCT(Board currentMove) {
        this.root = new MCTNode(currentMove, null);
    }

    /**
     * Searches for the best possible move to be made from the current game state.
     * 
     * @param duration The amount of time to run the search for
     * @return The best possible move
     */
    public static void printLikelyScenario(PrintWriter pen, MCTNode root) throws Exception {
        MCTNode node = root;
        while (!node.nextMoves.isEmpty()) {
            node.currentState.printBoard(pen);
            pen.println(
                    "Board was played " + node.playOuts.get() + " times, with a winrate of "
                            + (node.winRate.get() * 100));
            node = Collections.max(node.nextMoves, Comparator.comparingInt(n -> n.playOuts.get()));
        } // while
    }

    public Board search(Duration duration) throws Exception {
        ConcurrentLinkedQueue<MCTNode> workQ = new ConcurrentLinkedQueue<>();
        workQ.offer(root);

        Instant start = Instant.now();
        Instant deadline = start.plus(duration);
        PrintWriter pen = new PrintWriter(System.out, true);

        int processors = Runtime.getRuntime().availableProcessors();
        //int processors = 16;

        ExecutorService executor = Executors.newFixedThreadPool(processors);

        Runnable MCTSworker = () -> {
            while (Instant.now().isBefore(deadline)) {
                // MCTNode node = workQ.poll();
                // if (node == null) {
                // continue;
                // } //if
                try {
                    MCTNode selectedNode = select(root);
                    MCTNode expandedNode = expand(selectedNode);
                    simResultMCT winPoints = simulate(expandedNode, root.playOuts.get());
                    backPropagate(expandedNode, winPoints.winPoints, winPoints.length, root);

                    // /* Add the children of the expanded node to the queue */
                    // workQ.addAll(expandedNode.nextMoves);
                    // System.out.println("Children added to queue: " +
                    // expandedNode.nextMoves.size());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // while
        };

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            executor.submit(MCTSworker);
        } // for

        executor.shutdown();
        executor.awaitTermination(duration.toMillis(), TimeUnit.MILLISECONDS);
        /* Find the best move based on the node that was played the most */
        if (root.nextMoves.isEmpty()) {
            return null;
        }
        MCTNode bestNode = Collections.max(root.nextMoves, Comparator.comparingInt(n -> n.playOuts.get()));
        printLikelyScenario(pen, bestNode);
        pen.println("Simulated " + root.playOuts + " games.");
        pen.printf("Chosen move was played %d times with a simulated win rate of %.2f%%\n",
                bestNode.playOuts.get(), bestNode.winRate.get() * 100);
                int i = 0;
        for (MCTNode child : root.nextMoves) {
            System.out.printf("Move: %d Win rate: %.2f, Standard dev: %.2f Playouts: %d, Avg length: %.2f, Length sd: %.2f\n", i++, (child.winRate.get() * 100), child.standardErr.get() * 100, child.playOuts.get(), child.avgLength.get(), Math.sqrt(child.lengthstdDev.get()));
        }

        return bestNode.currentState;
    } // search(Duration)

    /**
     * Calculates a value for a node to select using UCB1
     * 
     * @param node The node to calculate
     * @return The value of the node
     */
    public static double UCT(MCTNode node) {
        /* Make sure the last moves playouts isn't null */
        double lastMovePlayouts = node.lastMove != null ? node.lastMove.playOuts.get() : 1;

        /* Return a high value if the node has never been played */
        if (node.playOuts.get() == 0) {
            return 100.0;
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
    public static MCTNode select(MCTNode node) {
        while (!node.currentState.isGameOver()) {
            if (node.nextMoves.isEmpty() || node.playOuts.get() == 0) {
                return node;
            } // if

            /*
             * Shuffle it so if none have been tried it selects something random, then
             * return the
             * node with the highest UCT
             */
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
    public static MCTNode expand(MCTNode node) {
        //int totalWeight = 0;

        if (!node.isExpanded) {
            synchronized (node) {
                if (!node.isExpanded) {
                    node.isExpanded = true;

                    /* Add all possible children to the node */
                    Move[] nextMoves = node.currentState.nextMoves();
                    for (Move move : nextMoves) {
                        //totalWeight += move.moveWeight;
                        Board gameState = PieceMoves.movePiece(move, node.currentState);
                        gameState.moveWeight = move.moveWeight;
                        gameState.turnColor = gameState.oppColor();
                        MCTNode newNode = new MCTNode(gameState, node);
                        node.newChild(newNode);
                    } // for
                }
            }
        }
        /* Return the node if theres no possible next move */
        if (node.nextMoves.isEmpty()) {
            return node;
        }
        // } // if
        // int random = ThreadLocalRandom.current().nextInt(totalWeight);
        // totalWeight = 0;

        // /* Return a weighted random node. */
        // for (MCTNode curNode : node.nextMoves) {
        //     totalWeight += curNode.currentState.moveWeight;
        //     if (totalWeight >= random) {
        //         return curNode;
        //     } // if
        // } // for
        return node.nextMoves.peek();
    } // expand(node)

    /**
     * Randomly simulates the finish of the game from the current game state. Note:
     * current
     * implementation creates lists of all future moves and chooses randomly. Could
     * be made more
     * efficient by creating just one random move?
     * 
     * @param node The terminating node
     * @return the number of win-points
     */

    public static simResultMCT simulate(MCTNode node, int playOuts) {
        Board gameState = node.currentState;
        int depthThresh = 300;
        int depth = 0;
        // PrintWriter pen = new PrintWriter(System.out, true);

        /* Run the loop while the game is undecided */
        while (true) {
            Move nextMove = gameState.ranWeightedMove(ThreadLocalRandom.current());
            if (nextMove == null || ++depth > depthThresh) {
                double vicPoints = gameState.vicPoints();
                double rew = reward(depth, vicPoints, gameState, node, playOuts);
                return new simResultMCT(rew, depth - 1);
            } // if
            gameState = PieceMoves.movePiece(nextMove, gameState);
            gameState.turnColor = gameState.oppColor();
            // if (depth++ > depthThresh) {
            //     if (material > 5) {
            //         return 0.75;
            //     } else if (material < -5) {
            //         return 0.25;
            //     } else {
            //         return 0.5;
            //     } // if/else
            //} // if
        } // while
          // pen.println("Points" + gameState.vicPoints());

    } // simulate(MCTNode)

    /**
     * Increments the total wins of every previous node by 1.0 if won, 0.5 if drawn,
     * and 0 if lost.
     * Increments total playouts by 1 for each regardless.
     * 
     * @param node      The node to backpropagate from (terminating node)
     * @param winPoints The number of points to be given.
     * @param root      The root of the MCT
     */
    public static void backPropagate(MCTNode node, double winPoints, int length, MCTNode root) {
        MCTNode curNode = node;
        while (curNode != null) {
            synchronized (curNode) {
      
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

            if (winRate == 0 || winRate == 1 || playOuts == 0) {
                curNode.standardErr.set(0.0);
            } else {
                double stdError = Math.sqrt((winRate * (1 - winRate)) / playOuts);
                curNode.standardErr.set(stdError);
            }


            double prvMean = curNode.avgLength.get();
            double delta = length - prvMean;
            double curMean = prvMean + delta / curNode.playOuts.get();
            curNode.avgLength.set(curMean);
            curNode.lengthstdDev.addAndGet(delta * (length - curMean));
        }
            curNode = curNode.lastMove;
        } // while
    } // backPropogate(MCTNode, double, MCTNode)

    public static double reward(int simLength, double result, Board gameState, MCTNode endingNode, int playOuts) {
            if (result == 0.5) {
                return 0.5;
            } 
            if (playOuts < 25000) {
                return result;
            }
        
            double newResult;
            double k = 1; // A double representing a weight for the sigmoid function
            

            double stdLength = (endingNode.avgLength.get() - simLength) / Math.sqrt(endingNode.lengthstdDev.get());
            double stdMaterial = gameState.material();
            double sigmoidLength = 1 / (1 + (Math.exp(-k * stdLength)));
            double sigmoidWins = 1 / (1 + (Math.exp(-k * stdMaterial)));
            double a = 1;
            double signum = (result == 0) ? -1 : 1;

            newResult = result + ((signum * a * sigmoidLength + signum * a * sigmoidWins) / 2) ;
            return (newResult + 1) / 3;
        }
        
} // MCT