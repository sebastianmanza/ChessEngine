package utils;

import java.io.PrintWriter;
import java.util.Arrays;

public class Board {

    /**
     * An integer representing the number of columns.
     */
    private static final int NUM_COLS = 8;
    /**
     * An integer representing the number of rows.
     */
    private static final int NUM_ROWS = 4;

    /**
     * The game board.
     */
    byte[][] board = new byte[NUM_COLS][NUM_ROWS];

    /**
     * The color of the turn
     */
    public byte turnColor;

    /**
     * The color of the engine
     */
    public byte engineColor;

    /**
     * Whether the game is over.
     */
    public boolean gameOver = false;

    /**
     * Builds a new board representing the games current state.
     *
     * @param turnCol representing who's turn it currently is
     * @param engineCol representing the color the engine is playing as
     */
    public Board(byte turnCol, byte engineCol) {
        this.turnColor = turnCol;
        this.engineColor = engineCol;
    }

    /**
     * Sets a square on the board to a piece.
     * @param square The square index to be set (0-63 inclusive)
     * @param piece The piece type (defined in PieceTypes)
     */

    public void setSquare(int square, byte piece) {
        /* Interpret the row and column based off of the square */
        int col = square / 8;
        int row = (square % 8) / 2;
        /* Create a boolean checking which nibble the square is in. */
        boolean highNibble = (square % 2 == 0);

        /* Clears the half of the byte that the piece will be stored on, and shifts the piece appropriately */
        if (highNibble) {
            this.board[col][row] = (byte) ((this.board[col][row] & 0x0f) | ((piece & 0x0F) << 4));
        } else {
            this.board[col][row] = (byte) ((this.board[col][row] & 0xF0) | piece & 0x0f);
        } // if/else
    } // setSquare

    /**
     * Gets the piece on a specified square
     * @param square The square index (0-63)
     * @return The piece located on the square
     */
    public byte getSquare(int square) {
        /* Interpret the row and column based off of the square */
        int col = square / 8;
        int row = (square % 8) / 2;
        /* Create a boolean checking which nibble the square is in. */
        boolean highNibble = (square % 2 == 0);

        /* Builds the piece by returning the proper half of the byte */
        if (highNibble) {
            return (byte) ((this.board[col][row] & 0xF0) >>> 4);
        } else {
            return (byte) (this.board[col][row] & 0x0F);
        }
    } //getSquare

    /**
     * Sets the board to chess's default starting position.
     */
    public void startingPos() {
        for (int i = 1; i < 58; i += 8) {
            setSquare(i, PieceTypes.WHITE_PAWN);
            setSquare(i + 5, PieceTypes.BLACK_PAWN);
        }
        /* Set the rook squares */
        setSquare(0, PieceTypes.WHITE_ROOK);
        setSquare(56, PieceTypes.WHITE_ROOK);
        setSquare(7, PieceTypes.BLACK_ROOK);
        setSquare(63, PieceTypes.BLACK_ROOK);

        /* Set the knight squares */
        setSquare(8, PieceTypes.WHITE_KNIGHT);
        setSquare(48, PieceTypes.WHITE_KNIGHT);
        setSquare(15, PieceTypes.BLACK_KNIGHT);
        setSquare(55, PieceTypes.BLACK_KNIGHT);

        /* Set the bishop squares */
        setSquare(16, PieceTypes.WHITE_BISHOP);
        setSquare(40, PieceTypes.WHITE_BISHOP);
        setSquare(23, PieceTypes.BLACK_BISHOP);
        setSquare(47, PieceTypes.BLACK_BISHOP);

        /* Set the queens and kings */
        setSquare(24, PieceTypes.WHITE_QUEEN);
        setSquare(31, PieceTypes.BLACK_QUEEN);
        setSquare(32, PieceTypes.WHITE_KING);
        setSquare(39, PieceTypes.BLACK_KING);
    } //startingPos

    /**
     * Prints the board in a basic textual form
     * @param pen The printwriter object to write with
     * @throws Exception if the piece on the square is not found
     */
    public void printBoard(PrintWriter pen) throws Exception {
        for (int col = 7; col >= 0; col--) {
            for (int row = 0; row < 8; row++) {
                int squareIndex = row * 8 + col;
                char piece;
                switch (getSquare(squareIndex)) {
                    case PieceTypes.EMPTY ->
                        piece = '-';
                    case PieceTypes.WHITE_PAWN ->
                        piece = '\u2659';
                    case PieceTypes.BLACK_PAWN ->
                        piece = '\u265F';
                    case PieceTypes.WHITE_ROOK ->
                        piece = '\u2656';
                    case PieceTypes.BLACK_ROOK ->
                        piece = '\u265C';
                    case PieceTypes.WHITE_KNIGHT ->
                        piece = '\u2658';
                    case PieceTypes.BLACK_KNIGHT ->
                        piece = '\u265E';
                    case PieceTypes.WHITE_BISHOP ->
                        piece = '\u2657';
                    case PieceTypes.BLACK_BISHOP ->
                        piece = '\u265D';
                    case PieceTypes.WHITE_QUEEN ->
                        piece = '\u2655';
                    case PieceTypes.BLACK_QUEEN ->
                        piece = '\u265B';
                    case PieceTypes.WHITE_KING ->
                        piece = '\u2654';
                    case PieceTypes.BLACK_KING ->
                        piece = '\u265A';
                    default ->
                        throw new Exception("Piece not found.");
                } //switch
                pen.print(piece);
                pen.print(' ');
            } //for
            pen.print('\n');
        } //for
    } //printBoard

    /**
     * Resets the board to empty squares.
     */
    public void clearBoard() {
        for (int i = 0; i < 64; i++) {
            setSquare(i, PieceTypes.EMPTY);
        } //for
    } //clearBoard

    /**
     * Gets the board.
     * @return the array representing just the board
     */
    public byte[][] getBoard() {
        return this.board;
    } //getBoard();

    /**
     * Gets the color of the piece.
     *
     * @param piece The byte representing the piece
     * @return A byte representation of the pieces color
     */
    public static byte pieceColor(byte piece) {
        return (byte) ((piece >> 3) & 1);
    } //pieceColor

    /**
     * Checks if the color of the piece is the same as the color of the turn.
     *
     * @param piece The byte representing the piece
     * @param turnColor The byte representing the turn color
     * @return true if it is the same, otherwise false.
     */
    public static boolean isColor(byte piece, byte color) {
        return (pieceColor(piece) == color);
    } //isColor

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, else false
     */
    public boolean isGameOver() {
        return (this.nextMoves().length == 0);
    } //isGameOver

    /**
     * Calculates the number of points won or lost by the engine at the end of a game.
     * @return 1.0 if the game was won, 0.0 if the game was lost, or 0.5 in the event of a stalemate or draw
     */
    public double vicPoints() {
        if (kingCapture(PieceTypes.WHITE_KING) && engineColor == PieceTypes.WHITE) {
            return 0.0;
        } else if (kingCapture(PieceTypes.BLACK_KING) && engineColor == PieceTypes.BLACK) {
            return 0.0;
        } else if (kingCapture(PieceTypes.WHITE_KING) && engineColor == PieceTypes.BLACK) {
            return 1.0;
        } else if (kingCapture(PieceTypes.BLACK_KING) && engineColor == PieceTypes.WHITE) {
            return 1.0;
        } //if/else
        return 0.5;
    } //vicPoints()

    /**
     * Checks if the king has been captured
     * @param kingColor The color of the king we are looking for
     * @return true if the king is gone, otherwise false.
     */
    public boolean kingCapture(byte kingColor) {
        for (int i = 0; i < 64; i++) {
            if (getSquare(i) == kingColor) {
                return false;
            } //if
        } //for
        return true;
    } //kingCapture

    /**
     * Checks if the game state is a legal position. If the king is in check,
     * the position is not legal.
     */
    public boolean isLegal() {
        int[] straightMoves = {-8, -1, 1, 8};
        int[] diagMoves = {-9, -7, 7, 9};
        int[] LMoves = {-17, -15, -10, -6, 6, 10, 15, 17};
        int[] pawnCaptures = {-7, 7};
        int[] kingMoves = {-9, -8, -7, -1, 1, 7, 8, 9};

        int kingSquare = 0;

        /* Set the piece to the king we're looking for, and the opposing pieces correctly. */
        byte piece = PieceTypes.BLACK_KING;
        byte oppPawn = PieceTypes.WHITE_PAWN;
        byte oppKnight = PieceTypes.WHITE_KNIGHT;
        byte oppRook = PieceTypes.WHITE_ROOK;
        byte oppQueen = PieceTypes.WHITE_QUEEN;

        if (this.turnColor == PieceTypes.WHITE) {
            piece = PieceTypes.WHITE_KING;
            oppPawn = PieceTypes.BLACK_PAWN;
            oppKnight = PieceTypes.BLACK_KNIGHT;
            oppRook = PieceTypes.BLACK_ROOK;
            oppQueen = PieceTypes.BLACK_QUEEN;

        } //if

        /* Find the correct kings square. */
        for (int i = 0; i < 64; i++) {
            if (this.getSquare(i) == piece) {
                kingSquare = i;
                break;
            } //if
        } //if

        int row = kingSquare % 8;
        int col = kingSquare / 8;

        /* Check if the king is in check from any pawns. */
        for (int move : pawnCaptures) {
            if ((kingSquare + move <= 63) && (kingSquare + move >= 0) && (getSquare(kingSquare + move) == oppPawn)) {
                return false;
            } //if
        } //for

        /* Check if the king is in check from any knights */
        for (int move : LMoves) {
            int endingSquare = kingSquare + move;
            int endingRow = endingSquare % 8;
            int endingCol = endingSquare / 8;
            byte endingPiece = getSquare(endingSquare);

            /* Checks to make sure it doesn't wrap around, is in bounds, and is a opposing knight*/
            if ((Math.abs(endingRow - row) <= 2)
                    && (Math.abs(endingCol - col) <= 2)
                    && ((endingSquare >= 0) && (endingSquare <= 63))
                    && (endingPiece == oppKnight)) {
                return false;
            } //if
        } //for

        /* Check if the king is in check from any rooks or queens. This occurs if the first piece it sees in any straight direction
         * is an opposite rook or queen.
         */
        for (int move : straightMoves) {
            int endingSquare = kingSquare + move;
            int endingRow = endingSquare % 8;
            int endingCol = endingSquare / 8;
            byte endingPiece = getSquare(endingSquare);

            /*Checks that it doesn't wrap around, is in legal bounds, and is an opposing R or Q*/
 /* Continue moving the piece in the direction hile it can */
            while (true) {
                endingSquare += move;
                endingRow = endingSquare % 8;
                endingCol = endingSquare / 8;
                endingPiece = this.getSquare(endingSquare);

                /* Either the columns or the rows must still be the same to avoid a wraparound. */
                if (endingCol != col && endingRow != row) {
                    break;
                } //if

                /* Check that it remains within bounds. */
                if ((endingSquare > 63 || (endingSquare < 0))) {
                    break;
                } //if

                /* Check that the square is either empty or an opposing color. */
                if (endingSquare != PieceTypes.EMPTY && pieceColor(endingPiece) == this.turnColor) {
                    break;
                } //if

                /* If it was an opponents piece, it can't go any further */
                if (Board.pieceColor(endingPiece) != this.turnColor) {
                    if (endingPiece == oppQueen || endingPiece == oppRook) {
                        return false;
                    } //if
                    break;
                } //if
            } //while(true)

            return false;
        }
        /* Check if the king is in check from any bishops or queens. See above, but diagonal */
        for (int move : diagMoves) {
            return false;
        }
        /* Check if it is in check from the opposing king. */
        for (int move : kingMoves) {
            return false;
        }
        return true;

    }

    /**
     * Creates an array of all possible next moves from this position.
     * @return All possible next moves.
     */
    public Board[] nextMoves() {
        /* Create an array to store all possible next moves, and an integer to store the current number of moves in the array. */
        Board[] nextPositions = new Board[50];
        int numPossibleMoves = 0;
        /* Loop through the board to check all the pieces */
        for (int square = 0; square < 64; square++) {
            byte piece = getSquare(square);
            /* If the square is empty, or its an opponents piece, don't bother looking at the moves. */
            if (piece == PieceTypes.EMPTY || !isColor(piece, this.turnColor)) {
                continue;
            } //if
            /* Create a new array for all the possible moves of that piece */
            Board[] pieceMoves = this.generatePieceMoves(piece, square);
            /* Skip if no moves are generated */
            if (pieceMoves == null || pieceMoves.length == 0) {
                continue;
            } //if
            /* Add legal game states (those where the king is not in check) to the master list of nextPositions */
            for (Board pieceMove : pieceMoves) {
                // if (pieceMove.isLegal()) {
                    nextPositions[numPossibleMoves] = pieceMove;
                    numPossibleMoves++;
                    if (numPossibleMoves >= nextPositions.length) {
                        /* Expand the array if it's full. */
                        nextPositions = Arrays.copyOf(nextPositions, numPossibleMoves * 2);
                    } //if
                //} //if
            } //for
        } //for
        /* Return only the legal game states in a correctly sized array */
        return Arrays.copyOf(nextPositions, numPossibleMoves);
    } //nextMoves

    /**
     * Generates an array of all possible moves by a particular piece on the board. 
     * @param piece
     * @param square
     * @return
     */
    public Board[] generatePieceMoves(byte piece, int square) {
        /* Create a new array that will return the types. */
        Board[] pieceMoves;
        byte color = pieceColor(piece);

        /* Check the type of the piece and generate the appropriate moves. Note that for slideMoves,
         the specific color given as pieceType is irrelevant, since that bit will not be looked at */
        switch (piece) {
            case PieceTypes.WHITE_PAWN, PieceTypes.BLACK_PAWN ->
                pieceMoves = PieceMoves.pawnMoves(square, color, this);
            case PieceTypes.WHITE_KNIGHT, PieceTypes.BLACK_KNIGHT ->
                pieceMoves = PieceMoves.knightMoves(square, color, this);
            case PieceTypes.WHITE_BISHOP, PieceTypes.BLACK_BISHOP ->
                pieceMoves = PieceMoves.slideMoves(square, color, PieceTypes.WHITE_BISHOP, this);
            case PieceTypes.WHITE_ROOK, PieceTypes.BLACK_ROOK ->
                pieceMoves = PieceMoves.slideMoves(square, color, PieceTypes.WHITE_ROOK, this);
            case PieceTypes.WHITE_QUEEN, PieceTypes.BLACK_QUEEN ->
                pieceMoves = PieceMoves.slideMoves(square, color, PieceTypes.WHITE_QUEEN, this);
            case PieceTypes.WHITE_KING, PieceTypes.BLACK_KING ->
                pieceMoves = PieceMoves.kingMoves(square, color, this);
            default ->
                throw new AssertionError();
        } //switch
        return pieceMoves;
    } //generatePieceMoves

    public Board copyBoard() {
        Board returnState = new Board(this.turnColor, this.engineColor);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                returnState.board[i][j] = this.board[i][j];
            } //for
        } //for
        return returnState;
    } //copyBoard()

} //CurrentBoard
