package utils.MCTUtils;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import utils.Board;
import utils.Move;
import utils.PieceMoves;

/**
 * A class that constructs and runs a Monte Carlo Tree Search.
 * 
 * @author Sebastian Manza
 */
public class MCTPPR {
    /** The exploration parameter, used to balance exploration vs exploitation */
    public static final double EXPLORATION_PARAM = Math.sqrt(2);

    /** The ratio of node playouts to total playouts that should be reached  in order to prune */
    public static final double TN = 0.1;

    /** The minimum win rate for us to try a move */
    public static final double TW = 0.3;

    public static final double P = 0.4;

    public static AtomicInteger numSimulations = new AtomicInteger(0);

    /** The root node of the move. (i.e. the move we are exploring from) */
    MCNode root;

    /* The number of nodes we pruned. */
    public static AtomicInteger prunedNodes = new AtomicInteger(0);

    /**
     * Creates a new Monte Carlo Tree using PPR (pruning)
     * 
     * @param currentMove The most recent move made.
     */
    public MCTPPR(Board currentMove) {
        this.root = new MCNode(currentMove, null);
        prunedNodes = new AtomicInteger(0);
        numSimulations = new AtomicInteger(0);
    } // MCTPPR(currentMove)

    /**
     * Print the most likely sequence of movesas selected by the engine;
     * 
     * @param pen  The printwriter object to write with.
     * @param root The node to start the printing with
     * @throws Exception if pen fails in some way
     */
    public static void printLikelyScenario(PrintWriter pen, MCNode root) throws Exception {
        MCNode node = root;
        while (!node.nextMoves.isEmpty()) {
            node.currentState.printBoard(pen);
            pen.println(
                    "Board was played " + node.playOuts + " times, with a winrate of " + (node.wins / node.playOuts));
            node = Collections.max(node.nextMoves, Comparator.comparingInt(n -> n.playOuts));
        } // while
    } // printLikelyScenario(PrintWriter, MCNode)

    /**
     * Searches for the best node in the tree.
     * 
     * @param duration the time to run it for
     * @return A board representing the best node
     * @throws Exception if the printwriter messes up
     */
    public Board search(Duration duration) throws Exception {
        Instant start = Instant.now();
        Instant deadline = start.plus(duration);
        AtomicInteger iterations = new AtomicInteger(0);
        PrintWriter pen = new PrintWriter(System.out, true);

        try (ForkJoinPool pool = new ForkJoinPool()) {
            /* Runs for a set duration of time */
            while (Instant.now().isBefore(deadline)) {
                MCNode selectedNode = select(root);
                MCNode expandedNode = expand(selectedNode);
                ArrayList<Move> movesToPlay = prune(expandedNode, root);

                /* Runs parallel simulations for the expanded node */
                List<Callable<SimResult>> tasks = new ArrayList<>();

                int numSims = Runtime.getRuntime().availableProcessors();

                for (int i = 0; i < numSims; i++) {
                    tasks.add(() -> simulate(expandedNode, movesToPlay));
                    iterations.incrementAndGet();
                } // for

                List<Future<SimResult>> results = pool.invokeAll(tasks);
                for (Future<SimResult> result : results) {
                    SimResult sim = result.get();
                    backPropagate(expandedNode, sim.winPoints, root, sim.gameSim);
                } // for
            } // while
            /* Find the best move based on the node that was played the most */
            if (root.nextMoves.isEmpty()) {
                return null;
            } // if
            MCNode bestNode = Collections.max(root.nextMoves, Comparator.comparingInt(n -> n.playOuts));
            pool.shutdown();
            printLikelyScenario(pen, bestNode);
            pen.println("Simulated " + numSimulations.get() + " games.");
            pen.printf("Chosen move was played %d times with a simulated win rate of %.2f%%. \nIt was played an additional %d times in other games, with a win rate of %.2f%%\n",
                    bestNode.playOuts, (bestNode.wins/ bestNode.playOuts) * 100, bestNode.AMAFplayOuts, (bestNode.AMAFwins / bestNode.AMAFplayOuts) * 100);

            return bestNode.currentState;
        } // try
    } // search(Duration)

    /**
     * Calculates a value for a node to select using UCB1.
     * 
     * @param node The node to calculate
     * @return The value of the node
     */
    public static double RAVE(MCNode node) {
        /* Make sure the last moves playouts isn't null */
        double lastMovePlayouts = node.lastMove != null ? node.lastMove.playOuts : 1;

        /* Return a high value if the node has never been played */
        if (node.playOuts == 0) {
            return 100.0;
        } //if

        /* Calculate the Upper Confidence Bound */
        double UCB1 = (node.wins / node.playOuts
        + (EXPLORATION_PARAM * Math.sqrt((Math.log(lastMovePlayouts)) / node.playOuts)));

        /* Calculate the AMAF value */
        double AMAF = (node.AMAFplayOuts > 0) ? (node.AMAFwins / node.AMAFplayOuts) : 0;

        /* Calculate the beta value */
        double beta = node.AMAFplayOuts / (node.playOuts + node.AMAFplayOuts + 0.001);

        /* Use the RAVE formula to incorporate both. */
        double RAVEresult = (1 - beta) * UCB1 + beta * AMAF;
        return RAVEresult;
    } // RAVE(node)

    /**
     * Selects the best possible node from the current root with current knowledge.
     * 
     * @param node The beginning node.
     * @return The best possible node from the beginning node.
     */
    public static MCNode select(MCNode node) {
        while (!node.currentState.isGameOver()) {
            if (node.nextMoves.isEmpty() || node.playOuts == 0) {
                return node;
            } // if

            double numSim = Math.log(node.playOuts);
            double topLBound = Double.NEGATIVE_INFINITY;
            double topUBound = Double.NEGATIVE_INFINITY;

            /* Find the node with the highest UCB */
            for (MCNode move : node.nextMoves) {
                double winRate = (move.wins / (move.playOuts + 0.001));
                double CI = EXPLORATION_PARAM * Math.sqrt(numSim / (move.playOuts + 0.001));
                double UBound = winRate + CI;

                if (UBound > topUBound) {
                    topUBound = UBound;
                    topLBound = winRate - CI;
                } // if
            } // for

            ArrayList<MCNode> prune = new ArrayList<>();

            /* Prune any node thats upper bound is lower than the best nodes lower bound */
            for (MCNode checkNode : node.nextMoves) {
                double winRate = (checkNode.wins / (checkNode.playOuts + 0.001));
                double CI = EXPLORATION_PARAM * Math.sqrt(numSim / (checkNode.playOuts + 0.001));
                double Ubound = winRate + CI;
                if (Ubound < topLBound) {
                    prune.add(checkNode);
                    prunedNodes.incrementAndGet();
                } // if
            } // for
            /* Filter the nodes */
            node.nextMoves = node.nextMoves.stream()
                    .filter(checkNode -> !prune.contains(checkNode))
                    .collect(Collectors.toList());

            /* Return the node with the highest RAVE */
            Collections.shuffle(node.nextMoves);
            node = Collections.max(node.nextMoves, Comparator.comparingDouble(MCTPPR::RAVE));
        } // while
        return node;
    } // select(MCNode)

    /**
     * Expands the tree one level deeper to continue searching.
     * 
     * @param node The first node reached with no children.
     * @return The expanded node.
     */
    public static MCNode expand(MCNode node) {
        int totalWeight = 0;

        /* Add all possible children to the node */
        Move[] nextMoves = node.currentState.nextMoves();
        for (Move move : nextMoves) {
            totalWeight += move.moveWeight;
            Board gameState = PieceMoves.movePiece(move, node.currentState);
            gameState.moveWeight = move.moveWeight;
            gameState.turnColor = gameState.oppColor();
            MCNode newNode = new MCNode(gameState, node);
            newNode.move = move;
            node.newChild(newNode);
        } // for

        /* Return the node if theres no possible next move */
        if (node.nextMoves.isEmpty()) {
            return node;
        } // if
        int random = ThreadLocalRandom.current().nextInt(totalWeight);
        totalWeight = 0;

        /* Return a weighted random node. */
        for (MCNode curNode : node.nextMoves) {
            totalWeight += curNode.currentState.moveWeight;
            if (totalWeight >= random) {
                return curNode;
            } // if
        } // for
        /* Backup: grab a random node */
        return node.nextMoves.get(ThreadLocalRandom.current().nextInt(node.nextMoves.size()));
    } // expand(node)

    public static ArrayList<Move> prune(MCNode expandedNode, MCNode root) {
        ArrayList<Move> movesToPlay= new ArrayList<>();

        for (MCNode move : root.nextMoves) {
            if ((move.wins + move.AMAFwins) / (move.playOuts + move.AMAFplayOuts + 0.001) > TW) {
                movesToPlay.add(move.move);
            }
              
        } //for

        return movesToPlay;
    } //prune(MCNode, MCNode)

    /**
     * Randomly simulates the finish of the game from the current game state.
     * 
     * @param node The terminating node
     * @return the number of win-points
     */

    public static SimResult simulate(MCNode node, ArrayList<Move> moves) throws Exception {
        numSimulations.incrementAndGet();
        Board gameState = node.currentState;
        int depth = 0;
        HashSet<Move> moveList = new HashSet<>(100);
        ArrayList<Move> movesToPlay = new ArrayList<>(moves);

        /* Run the loop while the game is undecided */
        while (true) {
            double move = ThreadLocalRandom.current().nextDouble(1);
            int PPRmove = 0;
            if (!movesToPlay.isEmpty()) {
            PPRmove = ThreadLocalRandom.current().nextInt(movesToPlay.size());
            } //if
            Move nextMove = null;
            boolean legalMove = false;

            if (move <= P && !movesToPlay.isEmpty()) {
                nextMove = movesToPlay.remove(PPRmove);
                Move[] nextMoves = gameState.nextMoves();
                for (Move legal : nextMoves) {
                    if (legal.equals(nextMove)) {
                        legalMove = true;
                        break;
                    } //if
                } //for
            }  //if
            if (!legalMove || move > P) {
                nextMove = gameState.ranWeightedMove(ThreadLocalRandom.current());
            } //if
            if (nextMove == null) {
                double vicPoints = gameState.vicPoints();
                
                return new SimResult(moveList, vicPoints);
            } // if
            /* Apply the move. */
            moveList.add(nextMove);
            gameState = PieceMoves.movePiece(nextMove, gameState);
            gameState.turnColor = gameState.oppColor();
            int material = gameState.material();
            /* Search to sufficient depth and return a win/loss.draw */
            if (depth++ > 100) {
                if (material > 5) {
                    return new SimResult(moveList, 0.75);
                } else if (material < -5) {
                    return new SimResult(moveList, 0.25);
                } else {
                    return new SimResult(moveList, 0.5);
                } // if/else
            } // if
        } // while
    } // simulate(MCNode)

    /**
     * Increments the total wins of every previous node by the points won.
     * Increments total playouts by 1 for each regardless.
     * 
     * @param node      The node to backpropagate from (terminating node)
     * @param winPoints The number of points to be given.
     * @param root      The root of the MCT
     */
    public static synchronized void backPropagate(MCNode node, double winPoints, MCNode root, HashSet<Move> sim) {
        MCNode curNode = node;
        while (curNode != null) {
            curNode.playOuts++;
            if (curNode.currentState.turnColor != curNode.currentState.engineColor) {
                curNode.wins += winPoints;
            } else {
                curNode.wins += (1 - winPoints);
            } // if/else
            curNode = curNode.lastMove;
        } // while
        propagateAMAF(root, sim, winPoints);

        
    } // backPropogate(MCNode, double, MCNode)

    public static void propagateAMAF(MCNode root, HashSet<Move> gameSim, double winPoints) {
        for (MCNode move : root.nextMoves) {
            if (gameSim.contains(move.move)) {
                move.AMAFplayOuts++;
                if (move.currentState.turnColor != move.currentState.engineColor) {
                    move.AMAFwins += winPoints;
                } else {
                    move.AMAFwins += (1 - winPoints);
                } //if
            } //if
        } //for
    } //propagateAMAF
} // MCT