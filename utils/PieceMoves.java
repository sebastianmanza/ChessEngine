package utils;
import java.util.Arrays;

public class PieceMoves {

    public static GameState[] promotePiece(int startingSquare, int endingSquare, GameState originalGameState) {
        GameState[] promotionMoves = new GameState[10];
        return promotionMoves;
    }


    public static GameState movePiece(int startingSquare, int endingSquare, GameState originalGameState) {
        GameState newGameState;
        /* Not sure if this copies the game state or not, but will definitely need to. */
        newGameState = originalGameState;
        
        /* Set the ending square to the piece at the starting square. Then clear the start square.*/
        newGameState.setSquare(endingSquare, newGameState.getSquare(startingSquare));
        newGameState.setSquare(startingSquare, PieceTypes.EMPTY);

        return newGameState;

    }

    /**
     * Generates all possible moves of the pawn located on the square.
     *
     * @param square The square the pawn is located on.
     * @param color The color of the piece.
     * @return A list of possible boards after the pawn has moved.
     */
    public static GameState[] pawnMoves(int square, byte color, GameState currentState) {
        /* Without promotion, a pawn has a maximum of 4 possible moves: two takes and two forwards(if on starting square.) */
        GameState[] pawnMoves = new GameState[4];
        int numMoves = 0;

        /* Assign some rows and directions based on color: */
        int direction = (color == PieceTypes.WHITE) ? 1 : -1;
        int startingRow = (color == PieceTypes.WHITE) ? 1 : 6;
        int promotionRow = (color == PieceTypes.WHITE) ? 6 : 1;

        boolean onStartingSquare = (square % 8 == startingRow);
        boolean reachPromotionSquare = (square % 8 == promotionRow);

        int forward = square + direction;

        /* If it can reach the promotion square, expand the number of possible moves. */
        if (reachPromotionSquare) {
            pawnMoves = Arrays.copyOf(pawnMoves, 10);
        } //if

        /* Create the forward moves if nothing is in front. */
        if (currentState.getSquare(forward) == PieceTypes.EMPTY) {
            if (reachPromotionSquare) {
                promotePiece(square, forward, currentState);
            } else {
                pawnMoves[numMoves] = movePiece(square, forward, currentState);
                numMoves++;
            } // if/else

            /* It can also move 2 squares forward if it's on its starting square. */
            if (onStartingSquare && (currentState.getSquare(forward) == PieceTypes.EMPTY)) {
                pawnMoves[numMoves] = movePiece(square, (square + (2 * direction)), currentState);
                numMoves++;
            } //if
        } //if

        /* Captures (the diagonal directions) */
        int[] captures = {direction + 8, direction - 8};
        for (int cap : captures) {
            int capture = square + cap; //the capture square
            byte piece = currentState.getSquare(capture);
            /* Check if there is an opponents piece on the capture square. */
            if ((piece != PieceTypes.EMPTY) && (Board.pieceColor(piece) != color)) {
                pawnMoves[numMoves] = movePiece(square, capture, currentState);
            } //if
        } //for

        //Need to add en passant.
        return pawnMoves;
    }

    public static GameState[] knightMoves(int square, byte color) {
        /* A knight can only move a maximum of 8 ways. */
        GameState[] knightMoves = new GameState[8];
        return knightMoves;
    }

    public static GameState[] slideMoves(int square, byte color, byte pieceType) {
        /* The queen can move 28 different ways if placed correctly. */
        GameState[] slidingMoves = new GameState[28];
        return slidingMoves;
    }

    public static GameState[] kingMoves(int square, byte color) {
        GameState[] kingMoves = new GameState[8];
        return kingMoves;
    }

}
