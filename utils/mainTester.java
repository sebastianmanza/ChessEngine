package utils;

/**
 * A class to test some functions.
 * @author Sebastian Manza
 */
public class mainTester {

    public static void main(String[] args) throws Exception {
        /* Create a new board */
        Board board1 = new Board(PieceTypes.WHITE, PieceTypes.WHITE);

        /* Initialize all starting positions of a board */
        board1.startingPos();

        Board[] nextboards = board1.nextMoves();

        for (int i = 0; i < nextboards.length; i++) {
            nextboards[i].printBoard();
        }

        /* Print a specific location in the array (in this case should print the location representing the a7 
        and a8 squares, which should be a BP and BR). Note that in this case it should print 10011100, however
        in setting the a7 square, seems to bug. */
        System.out.println(board1.getBoard()[0][3]);
        System.out.println(Integer.toBinaryString(board1.getBoard()[0][3]));
        /* Set a square that is represented by an upper nibble to a black piece to exhibit the bug clearly again. */
        board1.setSquare(36, PieceTypes.BLACK_PAWN);

        /* Print the baord, noting that "00" represents an incorrect byte. */
        board1.printBoard();
    }
}
