
// package utils.MCTUtils;

// import java.io.PrintWriter;
// import java.time.Duration;
// import java.time.Instant;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.Comparator;
// import java.util.HashSet;
// import java.util.List;
// import java.util.concurrent.Callable;
// import java.util.concurrent.ForkJoinPool;
// import java.util.concurrent.Future;
// import java.util.concurrent.ThreadLocalRandom;
// import java.util.concurrent.atomic.AtomicInteger;

// import utils.Board;
// import utils.Move;
// import utils.PieceMoves;

// /**
//  * A class that constructs and runs a Monte Carlo Tree Search.
//  * 
//  * @author Sebastian Manza
//  */
// public class MCTRAVE {
//     /** The exploration parameter, used to balance exploration vs exploitation */
//     public static final double EXPLORATION_PARAM = Math.sqrt(2);

//     /** The root node of the move. (i.e. the move we are exploring from) */
//     MCNode root;

//     /**
//      * Creates a new Monte Carlo Treee
//      * 
//      * @param currentMove The most recent move made.
//      */
//     public MCTRAVE(Board currentMove) {
//         this.root = new MCNode(currentMove, null);
//     }

//     /**
//      * Searches for the best possible move to be made from the current game state.
//      * 
//      * @param duration The amount of time to run the search for
//      * @return The best possible move
//      */
//     public static void printLikelyScenario(PrintWriter pen, MCNode root) throws Exception {
//         MCNode node = root;
//         while (!node.nextMoves.isEmpty()) {
//             node.currentState.printBoard(pen);
//             pen.println(
//                     "Board was played " + node.playOuts + " times, with a winrate of " + ((node.wins + node.AMAFwins) / (node.playOuts + node.AMAFplayOuts)));
//             node = Collections.max(node.nextMoves, Comparator.comparingInt(n -> n.playOuts));
//         } // while
//     }

//     public Board search(Duration duration) throws Exception {
//         Instant start = Instant.now();
//         Instant deadline = start.plus(duration);
//         AtomicInteger iterations = new AtomicInteger(0);
//         PrintWriter pen = new PrintWriter(System.out, true);
//         try (ForkJoinPool pool = new ForkJoinPool()) {
//             /* Runs for a set duration of time */
//             while (Instant.now().isBefore(deadline)) {
//                 MCNode selectedNode = select(root);
//                 MCNode expandedNode = expand(selectedNode);

//                 /* Runs parallel simulations for the expanded node */
//                 List<Callable<SimResult>> tasks = new ArrayList<>();

//                 int numSimulations = Runtime.getRuntime().availableProcessors();

//                 for (int i = 0; i < numSimulations; i++) {
//                     tasks.add(() -> simulate(expandedNode));
//                     iterations.incrementAndGet();
//                 } // for

//                 List<Future<SimResult>> results = pool.invokeAll(tasks);
//                 for (Future<SimResult> result : results) {
//                     SimResult gameSim = result.get();
//                     backPropagate(expandedNode, gameSim.winPoints, root, gameSim.gameSim);
//                 } // for
//             } // while
//             /* Find the best move based on the node that was played the most */
//             if (root.nextMoves.isEmpty()) {
//                 return null;
//             }
//             MCNode bestNode = Collections.max(root.nextMoves, Comparator.comparingInt(n -> n.playOuts));
//             pool.shutdown();
//             printLikelyScenario(pen, bestNode);
//             pen.println("Simulated " + iterations + " games.");
//             pen.printf("Chosen move was played %d times with a simulated win rate of %.2f%%. \nIt was played an additional %d times in other games, with a win rate of %.2f%%\n",
//                     bestNode.playOuts, (bestNode.wins/ bestNode.playOuts) * 100, bestNode.AMAFplayOuts, (bestNode.AMAFwins / bestNode.AMAFplayOuts) * 100);

//             return bestNode.currentState;
//         }
//     } // search(Duration)

//     /**
//      * Calculates a value for a node to select using a comination of 
//      * UCB (Upper Confidence Bound) and AMAF (all-moves-as-first) methods
//      * 
//      * @param node The node to calculate
//      * @return The value of the node
//      */
//     public static double RAVE(MCNode node) {
//         /* Make sure the last moves playouts isn't null */
//         double lastMovePlayouts = node.lastMove != null ? node.lastMove.playOuts : 1;

//         /* Return a high value if the node has never been played */
//         if (node.playOuts == 0) {
//             return 100.0;
//         } //if

//         /* Calculate the Upper Confidence Bound */
//         double UCB1 = (node.wins / node.playOuts
//         + (EXPLORATION_PARAM * Math.sqrt((Math.log(lastMovePlayouts)) / node.playOuts)));

//         /* Calculate the AMAF value */
//         double AMAF = (node.AMAFplayOuts > 0) ? (node.AMAFwins / node.AMAFplayOuts) : 0;

//         /* Calculate the beta value */
//         double beta = node.AMAFplayOuts / (node.playOuts + node.AMAFplayOuts + 0.001);

//         /* Use the RAVE formula to incorporate both. */
//         double RAVEresult = (1 - beta) * UCB1 + beta * AMAF;
//         return RAVEresult;
//     } // RAVE(node)

//     /**
//      * Selects the best possible node from the current root with current knowledge.
//      * 
//      * @param node The beginning node.
//      * @return The best possible node from the beginning node.
//      */
//     public static MCNode select(MCNode node) {
//         while (!node.currentState.isGameOver()) { //BROKEN
//             if (node.nextMoves.isEmpty() || node.playOuts == 0) {
//                 return node;
//             } // if

//             /*
//              * Shuffle it so if none have been tried it selects something random, then
//              * return the
//              * node with the highest UCT
//              */
//             Collections.shuffle(node.nextMoves);
//             node = Collections.max(node.nextMoves, Comparator.comparingDouble(MCTRAVE::RAVE));
//         } // while
//         return node;
//     } // select(MCNode)

//     /**
//      * Expands the tree one level deeper to continue searching
//      * 
//      * @param node The first node reached with no children.
//      * @return The expanded node.
//      */
//     public static MCNode expand(MCNode node) {
//         int totalWeight = 0;

//         /* Add all possible children to the node */
//         Move[] nextMoves = node.currentState.nextMoves();
//         for (Move move : nextMoves) {
//             totalWeight += move.moveWeight;
//             Board gameState = PieceMoves.movePiece(move, node.currentState);
//             gameState.moveWeight = move.moveWeight;
//             gameState.turnColor = gameState.oppColor();
//             MCNode newNode = new MCNode(gameState, node);
//             newNode.move = move;
//             node.newChild(newNode);
//         } // for

//         /* Return the node if theres no possible next move */
//         if (node.nextMoves.isEmpty()) {
//             return node;
//         } // if
//         int random = ThreadLocalRandom.current().nextInt(totalWeight);
//         totalWeight = 0;

//         /* Return a weighted random node. */
//         for (MCNode curNode : node.nextMoves) {
//             totalWeight += curNode.currentState.moveWeight;
//             if (totalWeight >= random) {
//                 return curNode;
//             } // if
//         } // for
//         return node.nextMoves.get(ThreadLocalRandom.current().nextInt(node.nextMoves.size()));
//     } // expand(node)

//     /**
//      * Randomly simulates the finish of the game from the current game state. Note:
//      * current
//      * implementation creates lists of all future moves and chooses randomly. Could
//      * be made more
//      * efficient by creating just one random move?
//      * 
//      * @param node The terminating node
//      * @return the number of win-points
//      */

//     public static SimResult simulate(MCNode node) throws Exception {
//         Board gameState = node.currentState;
//         int depth = 0;
//         HashSet<Move> gameSim = new HashSet<>();
//         //  PrintWriter pen = new PrintWriter(System.out, true);

//         /* Run the loop while the game is undecided */
//         while (true) {
//             Move nextMove = gameState.ranWeightedMove(ThreadLocalRandom.current());
//             if (nextMove == null) {
//                 double vicPoints = gameState.vicPoints();
//                 // int material = gameState.material();
//                 // vicPoints = vicPoints + 0.1 * Math.signum(material) * Math.min(Math.abs(material), 10);
//                 // return Math.max(0, Math.min(1, vicPoints));
//                 return new SimResult(gameSim, vicPoints);
//             } // if

//             gameState = PieceMoves.movePiece(nextMove, gameState);
//             gameState.turnColor = gameState.oppColor();
//             gameSim.add(nextMove);
//             int material = gameState.material();

//             /*
//              * If it's searched a little bit of depth and the material advantage is clear,
//              * give a
//              * win or a loss.
//              */
//             if (depth >= 10) {
//                 if (gameState.turnColor != gameState.engineColor && material < -9) {
//                     return new SimResult(gameSim, 0.15);
//                 } else if (gameState.turnColor == gameState.engineColor && material > 9) {
//                     return new SimResult(gameSim, 0.85);
//                 } // if
//             }

//             /* If it's searched far in, assume a weighted draw. */
//             if (depth++ >= 200) {
//                 if (material > 3) {
//                     return new SimResult(gameSim, 0.6);
//                 } else if (material < -3) {
//                     return new SimResult(gameSim, 0.4);
//                 } else
//                     return new SimResult(gameSim, 0.5);
//             } // if
//               // gameState.printBoard(pen);
//               // pen.println("Material" + gameState.material());
//         } // while
//           // pen.println("Points" + gameState.vicPoints());
//     } // simulate(MCNode)

//     /**
//      * Increments the total wins of every previous node by 1.0 if won, 0.5 if drawn,
//      * and 0 if lost.
//      * Increments total playouts by 1 for each regardless.
//      * 
//      * @param node      The node to backpropagate from (terminating node)
//      * @param winPoints The number of points to be given.
//      * @param root      The root of the MCT
//      */
//     public static synchronized void backPropagate(MCNode node, double winPoints, MCNode root, HashSet<Move> gameSim) {
//         MCNode curNode = node;
//         while (curNode != null) {
//             curNode.playOuts++;
//             if (curNode.currentState.turnColor != curNode.currentState.engineColor) {
//                 curNode.wins += winPoints;
//             } else {
//                 curNode.wins += (1 - winPoints);
//             } // if/else
//             curNode = curNode.lastMove;
//         } // while

//         propagateAMAF(root, gameSim, winPoints);

//     } // backPropogate(MCNode, double, MCNode)

//     public static void propagateAMAF(MCNode root, HashSet<Move> gameSim, double winPoints) {
//         ArrayList<MCNode> stack = new ArrayList<>();
//         stack.add(root);
    
//         while (!stack.isEmpty()) {
//             MCNode node = stack.remove(stack.size() - 1);
    
//             if (gameSim.contains(node.move)) {
//                 node.AMAFplayOuts++;
//                 if (node.currentState.turnColor != node.currentState.engineColor) {
//                     node.AMAFwins += winPoints;
//                 } else {
//                     node.AMAFwins += (1 - winPoints);
//                 }
//             }

//             stack.addAll(node.nextMoves);
//         }
//     }
// } // MCTRAVE

