package utils;
import java.util.List;
import utils.MCTUtils.move;
import utils.chessBoard;

/** A generic class piece */
public class piece {
    enum color {
        BLACK,
        WHITE
    }
    /** The color of the piece */
    color pieceColor;
    /** The square the piece is currently on */
    String square;
    /** A list of the possible moves that the piece can make */
    List <move> possibleMoves;

    public piece(String startingSquare, color pieceColor) {
        this.pieceColor = pieceColor;
        this.square = startingSquare;
    } //piece
    
} //piece

class pawn extends piece {
    boolean onStartingSquare;
    public List<move> possibleMoves() {
        if (this.onStartingSquare) {
            possibleMoves.add("move two forward placeholder");
        } //if
        return possibleMoves;
    }


}

class knight extends piece {
    public List<move> possibleMoves() throws Exception{
        int[] startingSquare = chessBoard.convertNotation(this.square);
        int[] move = new int[2];
        move[0] = 



        move moveToAdd = new move(knight this.whichKnight, endingSquare);

        this.possibleMoves.add(moveToAdd)
    }
}
