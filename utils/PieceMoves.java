package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PieceMoves {

    public static Board[] promotePiece(int startingSquare, int endingSquare, Board originalBoard) {
        Board[] promotionMoves = new Board[4];
        Board newBoard = originalBoard.copyBoard();
        Board newBoardTwo = originalBoard.copyBoard();
        Board newBoardThree = originalBoard.copyBoard();
        Board newBoardFour = originalBoard.copyBoard();


        /*Need to check for piece color */
        newBoard.setSquare(endingSquare, PieceTypes.WHITE_KNIGHT);
        newBoard.setSquare(startingSquare, PieceTypes.EMPTY);
        promotionMoves[0] = newBoard;

        newBoardTwo.setSquare(endingSquare, PieceTypes.WHITE_BISHOP);
        newBoardTwo.setSquare(startingSquare, PieceTypes.EMPTY);
        promotionMoves[1] = newBoard;

        newBoardThree.setSquare(endingSquare, PieceTypes.WHITE_ROOK);
        newBoardThree.setSquare(startingSquare, PieceTypes.EMPTY);
        promotionMoves[2] = newBoard;

        newBoardFour.setSquare(endingSquare, PieceTypes.WHITE_QUEEN);
        newBoardFour.setSquare(startingSquare, PieceTypes.EMPTY);
        promotionMoves[3] = newBoard;

        return promotionMoves;
    }

    public static Board movePiece(int startingSquare, int endingSquare, Board originalBoard) {
        Board newBoard;
        /* Make a copy of the original game state */
        newBoard = originalBoard.copyBoard();

        /* Set the ending square to the piece at the starting square. Then clear the start square.*/
        newBoard.setSquare(endingSquare, newBoard.getSquare(startingSquare));
        newBoard.setSquare(startingSquare, PieceTypes.EMPTY);

        return newBoard;

    }

    /**
     * Generates all possible moves of the pawn located on the square.
     *
     * @param square The square the pawn is located on.
     * @param color The color of the piece.
     * @return A list of possible boards after the pawn has moved.
     */
    public static Board[] pawnMoves(int square, byte color, Board currentState) {
        /* Without promotion, a pawn has a maximum of 4 possible moves: two takes and two forwards(if on starting square.) */
        Board[] pawnMoves = new Board[4];
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

            /* Don't check the square if its not valid. */
            if ((capture > 63) || (capture < 0)) {
                continue;
            } //if
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

    public static Board[] knightMoves(int square, byte color, Board currentState) {
        /* A knight can only move a maximum of 8 ways. */
        Board[] knightMoves = new Board[8];
        int numMoves = 0;
        int[] LMoves = {-17, -15, -10, -6, 6, 10, 15, 17};
        int row = square % 8;
        int col = square / 8;

        for (int LMove : LMoves) {
            int endingSquare = square + LMove;
            int endingRow = endingSquare % 8;
            int endingCol = endingSquare / 8;
            /* If it's out of bounds, don't check it. */
            if ((endingSquare > 63) || endingSquare < 0) {
                continue;
            }
            byte endingPiece = currentState.getSquare(endingSquare);

            /* Checks to make sure it doesn't wrap around and stays in bounds and checks to make sure it is not our piece. */
            if ((Math.abs(endingRow - row) <= 2)
                    && (Math.abs(endingCol - col) <= 2)
                    && ((endingPiece == PieceTypes.EMPTY) || (Board.pieceColor(endingPiece) != color))) {
                knightMoves[numMoves] = movePiece(square, endingSquare, currentState);
                numMoves++;
            } //if
        } //for
        return Arrays.copyOfRange(knightMoves, 0, numMoves);
    } //knightMoves

    public static Board[] slideMoves(int square, byte color, byte pieceType, Board currentState) {
        /* The queen can move 28 different ways if placed correctly, so lets dynamically size it */
        List<Board> slideMoves = new ArrayList<>();

        int endingSquare;
        int endingCol;
        int endingRow;
        byte pieceOnSquare;


        /* The offsets for the different move types */
        int[] straightMoves = {-8, -1, 1, 8};
        int[] diagMoves = {-9, -7, 7, 9};

        int row = square % 8;
        int col = square / 8;

        /* Generate horizontal mvoes if the piece is a rook or a queen */
        if ((pieceType == PieceTypes.WHITE_ROOK) || (pieceType == PieceTypes.WHITE_QUEEN)) {
            for (int move : straightMoves) {
                endingSquare = square;

                /* Continue moving the piece in the direction while it can */
                while (true) {
                    endingSquare += move;
                    endingRow = endingSquare % 8;
                    endingCol = endingSquare / 8;

                    /* Either the columns or the rows must still be the same to avoid a wraparound. */
                    if (endingCol != col && endingRow != row) {
                        break;
                    } //if

                    /* Check that it remains within bounds. */
                    if ((endingSquare > 63 || (endingSquare < 0))) {
                        break;
                    } //if

                    pieceOnSquare = currentState.getSquare(endingSquare);

                    /* Check that the square is either empty or an opposing color. */
                    if (pieceOnSquare != PieceTypes.EMPTY && Board.pieceColor(pieceOnSquare) == color) {
                        break;
                    } //if

                    slideMoves.add(movePiece(square, endingSquare, currentState));

                    /* If it was an opponents piece, it can't go any further */
                    if (Board.pieceColor(pieceOnSquare) != color) {
                        break;
                    } //if
                } //while(true)

            } //for
        } //if 

        /* Generate diagonal moves if the piece is a bishop or a queen */
        if ((pieceType == PieceTypes.WHITE_BISHOP) || (pieceType == PieceTypes.WHITE_QUEEN)) {
            for (int move : diagMoves) {
                endingSquare = square;

                /* Continue moving the piece in the direction while it can */
                while (true) {
                    endingSquare += move;
                    endingRow = endingSquare % 8;
                    endingCol = endingSquare / 8;

                    /* The difference between the starting column and ending column 
                and starting row and ending row must be equal for it to be a valid move */
                    if (Math.abs(endingCol - col) != Math.abs(endingRow - row)) {
                        break;
                    } //if

                    /* Check that it remains within bounds. */
                    if ((endingSquare > 63 || (endingSquare < 0))) {
                        break;
                    } //if

                    pieceOnSquare = currentState.getSquare(endingSquare);

                    /* Check that the square is either empty or an opposing color. */
                    if (pieceOnSquare != PieceTypes.EMPTY && Board.pieceColor(pieceOnSquare) == color) {
                        break;
                    } //if

                    slideMoves.add(movePiece(square, endingSquare, currentState));

                    /* If it was an opponents piece, it can't go any further */
                    if (pieceOnSquare != PieceTypes.EMPTY && Board.pieceColor(pieceOnSquare) != color) {
                        break;
                    } //if
                } //while(true)
            } //for
        } //if
        return slideMoves.toArray(new Board[0]);
    } //slideMoves

    public static Board[] kingMoves(int square, byte color, Board currentState) {
        Board[] kingMoves = new Board[8];
        int numMoves = 0;
        int row = square % 8;
        int col = square / 8;

        int[] adjMoves = {-9, -8, -7, -1, 1, 7, 8, 9};

        for (int move : adjMoves) {
            int endingSquare = square + move;
            int endingRow = endingSquare % 8;
            int endingCol = endingSquare / 8;

            if ((endingSquare < 0) && (endingSquare > 63)) {
                continue;
            } //if
            byte piece = currentState.getSquare(endingSquare);

            /* The move is valid if it is in bounds, and either to an empty square or to a square with an opponent piece.
             * It also must be within 1 row and 1 column, or it has wrapped around. */
            if ((Math.abs(endingRow - row) <= 1)
                    && (Math.abs(endingCol - col) <= 1)
                    && ((piece == PieceTypes.EMPTY) || (Board.pieceColor(piece) != color))) {
                kingMoves[numMoves] = movePiece(square, endingSquare, currentState);
                numMoves++;
            } //if
        } //for
        return Arrays.copyOfRange(kingMoves, 0, numMoves);
    } //kingMoves
} //class PieceMoves
