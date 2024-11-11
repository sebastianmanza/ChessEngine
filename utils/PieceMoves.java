package utils;
import java.util.Arrays;

public class PieceMoves {

    public static GameState[] promotePiece(int startingSquare, int endingSquare, GameState originalGameState) {
        GameState[] promotionMoves = new GameState[4];
        GameState newGameState = originalGameState;
        GameState newGameStateTwo = originalGameState;
        GameState newGameStateThree = originalGameState;
        GameState newGameStateFour = originalGameState;


        /*Need to check for piece color */
        newGameState.setSquare(endingSquare, PieceTypes.WHITE_KNIGHT);
        newGameState.setSquare(startingSquare, PieceTypes.EMPTY);
        promotionMoves[0] = newGameState;

        newGameStateTwo.setSquare(endingSquare, PieceTypes.WHITE_BISHOP);
        newGameStateTwo.setSquare(startingSquare, PieceTypes.EMPTY);
        promotionMoves[1] = newGameState;

        newGameStateThree.setSquare(endingSquare, PieceTypes.WHITE_ROOK);
        newGameStateThree.setSquare(startingSquare, PieceTypes.EMPTY);
        promotionMoves[2] = newGameState;

        newGameStateFour.setSquare(endingSquare, PieceTypes.WHITE_QUEEN);
        newGameStateFour.setSquare(startingSquare, PieceTypes.EMPTY);
        promotionMoves[3] = newGameState;

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
            pawnMoves = Arrays.copyOf(pawnMoves, 12);
        } //if

        /* Create the forward moves if nothing is in front. */
        if (currentState.getSquare(forward) == PieceTypes.EMPTY) {
            if (reachPromotionSquare) {
                for (int i = 0; i < 4; i++) {
                    pawnMoves[numMoves] = promotePiece(square, forward, currentState)[i];
                    numMoves++;
                } //for
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
                if (reachPromotionSquare) {
                    for (int i = 0; i < 4; i++) {
                        pawnMoves[numMoves] = promotePiece(square, capture, currentState)[i];
                        numMoves++;
                    } //for
                } else { 
                    pawnMoves[numMoves] = movePiece(square, capture, currentState);
                    numMoves++;
                } //if/else
            } //if
        } //for


        //Need to add en passant.


        return Arrays.copyOfRange(pawnMoves, 0, numMoves);
    }

    public static GameState[] knightMoves(int square, byte color, GameState currentState) {
        /* A knight can only move a maximum of 8 ways. */
        GameState[] knightMoves = new GameState[8];
        int numMoves = 0;
        int[] LMoves = {-17, -15, -10, -6, 6, 10, 15, 17};
        int row = square % 8;
        int col = square / 8;

        for (int LMove : LMoves) {
            int endingSquare = square + LMove;
            int endingRow = endingSquare % 8;
            int endingCol = endingSquare / 8;
            byte endingPiece = currentState.getSquare(endingSquare);

            /* Checks to make sure it doesn't wrap around and stays in bounds and checks to make sure it is not our piece. */
            if ((Math.abs(endingRow - row) <= 2) && 
                (Math.abs(endingCol - col) <= 2) && 
                ((endingSquare >= 0) && (endingSquare <= 63)) &&
                ((endingPiece == PieceTypes.EMPTY) || (Board.pieceColor(endingPiece) != color))
            ) {
                knightMoves[numMoves] = movePiece(square, endingSquare, currentState);
                numMoves++;
            } //if
        } //for
        return Arrays.copyOfRange(knightMoves, 0, numMoves);
    } //knightMoves

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
