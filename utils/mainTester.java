package utils;
import java.io.PrintWriter;
/**
 * A class to test some functions.
 * @author Sebastian Manza
 */
public class mainTester {

    public static void main(String[] args) throws Exception {
        PrintWriter pen = new PrintWriter(System.out, true);
        /* Create a new board */
        Board board1 = new Board(PieceTypes.WHITE, PieceTypes.WHITE);
        Board board2 = new Board(PieceTypes.WHITE, PieceTypes.WHITE);


        /* Initialize all starting positions of a board */
        board1.startingPos();
        board2.startingPos();
        board2.setSquare(25, PieceTypes.EMPTY);
        board2.setSquare(27, PieceTypes.WHITE_PAWN);
        board2.setSquare(30, PieceTypes.EMPTY);
        board2.setSquare(28, PieceTypes.BLACK_PAWN);

        Board[] nextboards1 = board1.nextMoves();

        for (int i = 0; i < nextboards1.length; i++) {
            nextboards1[i].printBoard(pen);
            pen.println("");
        }

        Board[] nextboards2 = board2.nextMoves();

        for (int i = 0; i < nextboards2.length; i++) {
            nextboards2[i].printBoard(pen);
            pen.println("");
        }
        
        /* Attempt to print all possible next boards. */
        // for (int i = 0; i < nextboards.length; i++) {
        //     nextboards[i].printBoard();
        // }
        System.out.println(nextboards1.length);
        System.out.println(nextboards2.length);

        /* Print the baord, noting that "00" represents an incorrect byte. */
        //board1.printBoard();
        pen.close();
    }
}
