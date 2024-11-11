package utils;

import java.io.PrintWriter;
import java.util.Arrays;

public class CurrentGameState implements GameState {

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
    private byte[][] board = new byte[NUM_COLS][NUM_ROWS];

    /**
     * The color of the turn
     */
    public byte turnColor;

    /**
     * The color of the engine
     */
    public byte engineColor;

    /**
     * Builds a new board representing the games current state.
     *
     * @param turnCol representing who's turn it currently is
     * @param engineCol representing the color the engine is playing as
     */
    public CurrentGameState(byte turnCol, byte engineCol) {
        this.turnColor = turnCol;
        this.engineColor = engineCol;
    }

    public void setSquare(int square, byte piece) {
        /* Interpret the row and column based off of the square */
        int col = square / 8;
        int row = (square % 8) / 2;
        /* Create a boolean checking which nibble the square is in. */
        boolean highNibble = (square % 2 == 0);

        /* Clears the half of the byte that the piece will be stored on, and shifts the piece appropriately */
        if (highNibble) {
            this.board[col][row] = (byte) ((this.board[col][row] & 0x0f) | (piece << 4));
        } else {
            this.board[col][row] = (byte) ((this.board[col][row] & 0xF0) | piece);
        } // if/else
    } // setSquare

    public byte getSquare(int square) {
        /* Interpret the row and column based off of the square */
        int col = square / 8;
        int row = (square % 8) / 2;
        /* Create a boolean checking which nibble the square is in. */
        boolean highNibble = (square % 2 == 0);
        byte piece;

        /* Builds the piece by returning the proper half of the byte */
        if (highNibble) {
            piece = (byte) (this.board[col][row] & 0xf0);
            return (byte) (piece >> 4);
        } else {
            piece = (byte) (this.board[col][row] & 0x0f);
            return piece;
        } //if/else
    } //getSquare

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

    public void printBoard() throws Exception {
        PrintWriter pen = new PrintWriter(System.out, true);
        for (int col = 7; col >= 0; col--) {
            for (int row = 0; row < 8; row++) {
                int squareIndex = row * 8 + col;
                String piece;
                switch (getSquare(squareIndex)) {
                    case PieceTypes.EMPTY ->
                        piece = "--";
                    case PieceTypes.WHITE_PAWN ->
                        piece = "WP";
                    case PieceTypes.BLACK_PAWN ->
                        piece = "BP";
                    case PieceTypes.WHITE_ROOK ->
                        piece = "WR";
                    case PieceTypes.BLACK_ROOK ->
                        piece = "BR";
                    case PieceTypes.WHITE_KNIGHT ->
                        piece = "WN";
                    case PieceTypes.BLACK_KNIGHT ->
                        piece = "BN";
                    case PieceTypes.WHITE_BISHOP ->
                        piece = "WB";
                    case PieceTypes.BLACK_BISHOP ->
                        piece = "BB";
                    case PieceTypes.WHITE_QUEEN ->
                        piece = "WQ";
                    case PieceTypes.BLACK_QUEEN ->
                        piece = "BQ";
                    case PieceTypes.WHITE_KING ->
                        piece = "WK";
                    case PieceTypes.BLACK_KING ->
                        piece = "BK";
                    default ->
                        piece = "OO";
                } //switch
                pen.print("|" + piece);
            } //for
            pen.print("|\n");
        } //for
        pen.close();
    } //printBoard

    public void clearBoard() {
        for (int i = 0; i < 64; i++) {
            setSquare(i, PieceTypes.EMPTY);
        } //for
    } //clearBoard

    public byte[][] getBoard() {
        return this.board;
    } //getBoard();

    /**
     * Gets the color of the piece.
     * @param piece The byte representing the piece
     * @return A byte representation of the pieces color
     */
    public static byte pieceColor(byte piece) {
        return (byte)((piece & ((byte) 7)) >> 3);
    } //pieceColor

    /**
     * Checks if the color of the piece is the same as the color of the turn.
     * @param piece The byte representing the piece
     * @param turnColor The byte representing the turn color
     * @return true if it is the same, otherwise false.
     */
    public static boolean isColor(byte piece, byte turnColor) {
        return (pieceColor(piece) == turnColor);
    } //isColor

    public GameState[] generatePieceMoves(byte piece, int square) {
        return new GameState[10]; //STUB
    }

    public GameState[] nextMoves() {
        /* Create an array to store all possible next moves, and an integer to store the current number of moves in the array. */
        GameState[] nextPositions = new GameState[50];
        int numPossibleMoves = 0;

        /* Loop through the board to check all the pieces */
        for (int square = 0; square < 64; square++) {
            byte piece = getSquare(square);

            /* If the square is empty, or its an opponents piece, don't bother looking at the moves. */
            if (piece == PieceTypes.EMPTY || !isColor(piece, this.turnColor)) {
                continue;
            } //if
            
            /* Create a new array for all the possible moves of that piece */
            GameState[] pieceMoves = this.generatePieceMoves(piece, square);

            /* Add legal game states (those where the king is not in check) to the master list of nextPositions */
            for (GameState pieceMove : pieceMoves) {
                if (pieceMove.isLegal()) {
                    nextPositions[numPossibleMoves] = pieceMove;
                    numPossibleMoves++;
                    if (numPossibleMoves >= nextPositions.length) {
                        /* Expand the array if its full. */
                        Arrays.copyOf(nextPositions, numPossibleMoves * 2);
                    } //if
                } //if
                
            } //for

        return nextPositions;
    }

} //CurrentGameState

