package utils;

import java.io.PrintWriter;

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

} //CurrentGameState

