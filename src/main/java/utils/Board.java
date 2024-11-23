package utils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
     * A boolean representing if the board has legal moves, updated whenever a
     * function that creates moves is called.
     */
    public boolean hasLegalMoves;

    /**
     * The weight of the move made (higher for captures)
     */
    public int moveWeight;

    /**
     * The number of pieces on the board.
     */
    public int pieceCount;

    /**
     * Builds a new board representing the games current state.
     *
     * @param turnCol   representing who's turn it currently is
     * @param engineCol representing the color the engine is playing as
     */
    public Board(byte turnCol, byte engineCol) {
        this.turnColor = turnCol;
        this.engineColor = engineCol;
        this.hasLegalMoves = true;
        this.moveWeight = 1;
        this.pieceCount = 0;
    } // Board()

    /**
     * Sets a square on the board to a piece.
     *
     * @param square The square index to be set (0-63 inclusive)
     * @param piece  The piece type (defined in PieceTypes)
     */
    public void setSquare(int square, byte piece) {
        /* Interpret the row and column based off of the square */
        int col = square / 8;
        int row = (square % 8) / 2;
        /* Create a boolean checking which nibble the square is in. */
        boolean highNibble = (square % 2 == 0);

        /*
         * Clears the half of the byte that the piece will be stored on, and shifts the
         * piece
         * appropriately
         */
        if (highNibble) {
            this.board[col][row] = (byte) ((this.board[col][row] & 0x0f) | ((piece & 0x0F) << 4));
        } else {
            this.board[col][row] = (byte) ((this.board[col][row] & 0xF0) | piece & 0x0f);
        } // if/else
    } // setSquare

    /**
     * Gets the piece on a specified square
     *
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
    } // getSquare

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
    } // startingPos

    /**
     * Updates the legal moves boolean based off of a nextMoves array.
     * Meant to be run while the array is created.
     * 
     * @param moves An array of the possible next moves.
     */
    public void updateLegalMoves(int moveNum) {
        hasLegalMoves = (moveNum > 0);
    } // updateLegalMoves

    /**
     * Updates the legal moves boolean based off of a nextMoves list.
     * Meant to be run while the list is created.
     * 
     * @param moves A list of the possible moves.
     */
    public void updateLegalMoves(ArrayList<Board> moves) {
        hasLegalMoves = !moves.isEmpty();
    } // updateLegalMoves

    /**
     * Prints the board in a basic textual form
     *
     * @param pen The printwriter object to write with
     * @throws Exception if the piece on the square is not found
     */
    public void printBoard(PrintWriter pen) throws Exception {
        for (int col = 7; col >= 0; col--) {
            for (int row = 0; row < 8; row++) {
                int squareIndex = row * 8 + col;
                char piece;
                switch (getSquare(squareIndex)) {
                    case PieceTypes.EMPTY -> piece = '-';
                    case PieceTypes.WHITE_PAWN -> piece = '\u2659';
                    case PieceTypes.BLACK_PAWN -> piece = '\u265F';
                    case PieceTypes.WHITE_ROOK -> piece = '\u2656';
                    case PieceTypes.BLACK_ROOK -> piece = '\u265C';
                    case PieceTypes.WHITE_KNIGHT -> piece = '\u2658';
                    case PieceTypes.BLACK_KNIGHT -> piece = '\u265E';
                    case PieceTypes.WHITE_BISHOP -> piece = '\u2657';
                    case PieceTypes.BLACK_BISHOP -> piece = '\u265D';
                    case PieceTypes.WHITE_QUEEN -> piece = '\u2655';
                    case PieceTypes.BLACK_QUEEN -> piece = '\u265B';
                    case PieceTypes.WHITE_KING -> piece = '\u2654';
                    case PieceTypes.BLACK_KING -> piece = '\u265A';
                    default -> throw new Exception("Piece not found.");
                } // switch
                pen.print(piece);
                pen.print(' ');
            } // for
            pen.print('\n');
        } // for
    } // printBoard

    /**
     * Resets the board to empty squares.
     */
    public void clearBoard() {
        for (int i = 0; i < 64; i++) {
            setSquare(i, PieceTypes.EMPTY);
        } // for
    } // clearBoard

    /**
     * Gets the board.
     *
     * @return the array representing just the board
     */
    public byte[][] getBoard() {
        return this.board;
    } // getBoard();

    /**
     * Gets the color of the piece.
     *
     * @param piece The byte representing the piece
     * @return A byte representation of the pieces color
     */
    public static byte pieceColor(byte piece) {
        return (byte) ((piece >> 3) & 1);
    } // pieceColor

    /**
     * Checks if the color of the piece is the same as the color of the turn.
     *
     * @param piece     The byte representing the piece
     * @param turnColor The byte representing the turn color
     * @return true if it is the same, otherwise false.
     */
    public static boolean isColor(byte piece, byte color) {
        return (pieceColor(piece) == color);
    } // isColor

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, else false
     */
    public boolean isGameOver() {
        /* Make sure the kings are still there and there are legal moves left */
        return (!hasLegalMoves);
    } // isGameOver

    /**
     * Adds the value of a piece to a value.
     * 
     * @param piece The piece we are evaluating.
     * @param addTo The number to add it to.
     * @return An integer representing the new value.
     */
    public static int addPieceValue(byte piece, int addTo) {
        switch (piece) {
            case PieceTypes.WHITE_PAWN -> addTo += 2;
            case PieceTypes.BLACK_PAWN -> addTo += 2;
            case PieceTypes.WHITE_KNIGHT -> addTo += 6;
            case PieceTypes.BLACK_KNIGHT -> addTo += 6;
            case PieceTypes.WHITE_BISHOP -> addTo += 6;
            case PieceTypes.BLACK_BISHOP -> addTo += 6;
            case PieceTypes.WHITE_ROOK -> addTo += 10;
            case PieceTypes.BLACK_ROOK -> addTo += 10;
            case PieceTypes.WHITE_QUEEN -> addTo += 18;
            case PieceTypes.BLACK_QUEEN -> addTo += 18;
            default -> {
            } // switch
        } // switch
        return addTo;
    } // addPieceValue(byte, int)

    /**
     * Evaluates the material advantage on the board.
     * 
     * @return an integer representing the material advantage.
     */
    public int material() {
        int material = 0;
        for (int i = 0; i < 64; i++) {
            byte piece = getSquare(i);
            /* If the square is empty ignore it. */
            if (piece == PieceTypes.EMPTY) {
                continue;
            } // if
            switch (piece) {
                case PieceTypes.WHITE_PAWN -> material += 1;
                case PieceTypes.BLACK_PAWN -> material -= 1;
                case PieceTypes.WHITE_KNIGHT -> material += 3;
                case PieceTypes.BLACK_KNIGHT -> material -= 3;
                case PieceTypes.WHITE_BISHOP -> material += 3;
                case PieceTypes.BLACK_BISHOP -> material -= 3;
                case PieceTypes.WHITE_ROOK -> material += 5;
                case PieceTypes.BLACK_ROOK -> material -= 5;
                case PieceTypes.WHITE_QUEEN -> material += 9;
                case PieceTypes.BLACK_QUEEN -> material -= 9;
                default -> {
                } // return nothing for default
            } // switch
            this.pieceCount++;
            if (this.engineColor == PieceTypes.BLACK) {
                material = 0 - material;
            } // if
        } // for
        return material;
    } // material()

    /**
     * Calculates the number of points won or lost by the engine at the end of a
     * game.
     *
     * @return 1.0 if the game was won, 0.0 if the game was lost, or 0.5 in the
     *         event of a stalemate
     *         or draw
     */
    public double vicPoints() {
        if (PieceMoves.inCheck(this, PieceTypes.WHITE) && engineColor == PieceTypes.WHITE) {
            return 0.0;
        } else if (PieceMoves.inCheck(this, PieceTypes.BLACK) && engineColor == PieceTypes.BLACK) {
            return 0.0;
        } else if (PieceMoves.inCheck(this, PieceTypes.WHITE) && engineColor == PieceTypes.BLACK) {
            return 1.0;
        } else if (PieceMoves.inCheck(this, PieceTypes.BLACK) && engineColor == PieceTypes.WHITE) {
            return 1.0;
        } // if/else
        return 0.5;
    } // vicPoints()

    /**
     * Checks if the king has been captured
     *
     * @param kingColor The color of the king we are looking for
     * @return true if the king is gone, otherwise false.
     */
    public boolean kingCapture(byte king) {
        for (int i = 0; i < 64; i++) {
            if (getSquare(i) == king) {
                return false;
            } // if
        } // for
        return true;
    }// kingCapture

    /**
     * Gets the opposite color of the current turn
     * 
     * @return A byte representation of the opposite color.
     */
    public byte oppColor() {
        if (this.turnColor == PieceTypes.WHITE) {
            return PieceTypes.BLACK;
        } else {
            return PieceTypes.WHITE;
        } // if/else
    } // oppColor()

    /**
     * Creates a random weighted move from all possible next moves. More likely to
     * return a capture of higher value.
     * 
     * @param rand A random object.
     * @return A board representing the move
     */
    public Board ranWeightedMove(Random rand) throws Exception {
        Board[] nextMoves = this.nextMoves();
        int totalWeight = 0;
        PrintWriter pen = new PrintWriter(System.out, true);

        /* If there aren't any legal moves, return null */
        if (nextMoves.length == 0) {
            return null;
        } // if

        for (Board move : nextMoves) {
            totalWeight += move.moveWeight;
        } // for

        int random = rand.nextInt(totalWeight);

        totalWeight = 0;
        /* Return the first move greater than the random number. */
        for (Board move : nextMoves) {
            totalWeight += move.moveWeight;
            if (totalWeight >= random) {
                move.turnColor = this.oppColor();
                // move.printBoard(pen);
                // pen.println(move.moveWeight);
                return move;
            } // if
        } // for
        return null;
    } // ranWeightedMove(Random)

    /**
     * Creates an array of all possible next moves from this position.
     *
     * @return All possible next moves.
     */
    public Board[] nextMoves() {
        /*
         * Create an array to store all possible next moves, and an integer to store the
         * current
         * number of moves in the array.
         */
        Board[] nextPositions = new Board[50];
        int numPossibleMoves = 0;
        /* Loop through the board to check all the pieces */
        for (int square = 0; square < 64; square++) {
            byte piece = getSquare(square);
            /*
             * If the square is empty, or its an opponents piece, don't bother looking at
             * the moves.
             */
            if (piece == PieceTypes.EMPTY || !isColor(piece, this.turnColor)) {
                continue;
            } // if
            /* Create a new array for all the possible moves of that piece */
            Board[] pieceMoves = this.generatePieceMoves(piece, square);
            /* Skip if no moves are generated */
            if (pieceMoves == null || pieceMoves.length == 0) {
                continue;
            } // if
            /*
             * Add legal game states (those where the king is not in check) to the master
             * list of
             * nextPositions
             */
            for (Board pieceMove : pieceMoves) {
                pieceMove.turnColor = this.oppColor();
                if (!PieceMoves.inCheck(pieceMove, this.turnColor)) {
                    if (PieceMoves.inCheck(pieceMove, this.oppColor())) {
                        pieceMove.moveWeight += 5;
                    } // if
                    nextPositions[numPossibleMoves] = pieceMove;
                    numPossibleMoves++;
                    if (numPossibleMoves >= nextPositions.length) {
                        /* Expand the array if it's full. */
                        nextPositions = Arrays.copyOf(nextPositions, numPossibleMoves * 2);
                    } // if
                } // if
            } // for
        } // for

        /* Update if there are legal moves left. */
        updateLegalMoves(numPossibleMoves);
        /* Return only the legal game states in a correctly sized array */
        return Arrays.copyOf(nextPositions, numPossibleMoves);
    } // nextMoves

    /**
     * Generates an array of all possible moves by a particular piece on the board.
     *
     * @param piece  The piece to generate moves for
     * @param square The square the piece is located on
     * @return An array of all possible moves for the piece.
     */
    public Board[] generatePieceMoves(byte piece, int square) {
        /* Create a new array that will return the types. */
        Board[] pieceMoves;
        byte color = pieceColor(piece);

        /*
         * Check the type of the piece and generate the appropriate moves. Note that for
         * slideMoves,
         * the specific color given as pieceType is irrelevant, since that bit will not
         * be looked at
         */
        switch (piece) {
            case PieceTypes.WHITE_PAWN, PieceTypes.BLACK_PAWN -> pieceMoves = PieceMoves.pawnMoves(square, color, this);
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
            default -> throw new AssertionError();
        } // switch
        return pieceMoves;
    } // generatePieceMoves

    /**
     * Make a deep copy of the board.
     * 
     * @return the copied board
     */
    public Board copyBoard() {
        Board returnState = new Board(this.turnColor, this.engineColor);
        for (int i = 0; i < this.board.length; i++) {
            System.arraycopy(this.board[i], 0, returnState.board[i], 0, this.board[0].length);
        } // for
        return returnState;
    } // copyBoard()

} // CurrentBoard
