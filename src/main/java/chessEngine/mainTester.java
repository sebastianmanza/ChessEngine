package chessEngine;

import java.io.PrintWriter;
import java.time.Duration;

import utils.Board;
import utils.MCTUtils.MCT;
import utils.PieceTypes;

/**
 * A class to test some functions.
 * 
 * @author Sebastian Manza
 */
public class mainTester {

    public static void main(String[] args) throws Exception {
        /* Create a new board */
        try (PrintWriter pen = new PrintWriter(System.out, true)) {
            /* Create a new board */
            Board board1 = new Board(PieceTypes.WHITE, PieceTypes.WHITE);
            Board board2 = new Board(PieceTypes.BLACK, PieceTypes.WHITE);
            /* Initialize all starting positions of the boards */
            board1.startingPos();
            board2.startingPos();
            board2.setSquare(33, PieceTypes.EMPTY);
            board2.setSquare(35, PieceTypes.WHITE_PAWN);
            board2.setSquare(38, PieceTypes.EMPTY);
            board2.setSquare(36, PieceTypes.BLACK_PAWN);
            board2.setSquare(48, PieceTypes.EMPTY);
            board2.setSquare(42, PieceTypes.WHITE_KNIGHT);
            board2.setSquare(15, PieceTypes.EMPTY);
            board2.setSquare(21, PieceTypes.BLACK_KNIGHT);
            board2.setSquare(40, PieceTypes.EMPTY);
            board2.setSquare(19, PieceTypes.WHITE_BISHOP);
            Board[] nextboards1 = board1.nextMoves();

            // for (int i = 0; i < nextboards1.length; i++) {
            // nextboards1[i].printBoard(pen);
            // pen.println("");
            // }

            // Board[] nextboards2 = board2.nextMoves();

            // pen.println("----------------");
            // pen.println("----------------");

            // board2.printBoard(pen);
            // for (int i = 0; i < nextboards2.length; i++) {
            // nextboards2[i].printBoard(pen);
            // pen.println("----------------");
            // }
            // pen.println(nextboards2.length);

            MCT tree = new MCT(board1);
            Duration duration = Duration.ofSeconds(30);
            Board bestMove = tree.search(duration);

            bestMove.printBoard(pen);

            // double winPoints = 0;
            // MCNode board1Node = new MCNode(board1, null);

            // for (int i = 0; i < 1000; i++) {
            //     winPoints += MCT.simulate(board1Node);
            // }
            // pen.println(winPoints);
        }
    }
}
