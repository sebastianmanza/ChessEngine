import utils.chessBoard;
import utils.chessBoard.piece;

package main;
public class mainTester {
    public static void main(String[] args) throws Exception{
        chessBoard board1 = new chessBoard();
        board1.setNewBoard();
        board1.setSquare("e2", chessBoard.piece.EMPTY);
        board1.setSquare("e4", chessBoard.piece.WHITE_PAWN);
        board1.setSquare("e7", chessBoard.piece.EMPTY);
        board1.setSquare("e5", chessBoard.piece.BLACK_PAWN);

        board1.printBoard();
    }
}