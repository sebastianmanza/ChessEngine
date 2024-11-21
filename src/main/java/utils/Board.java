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
     * A boolean representing if the board has legal moves, updated whenever a function that creates moves is called.
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
    } //Board()

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
     * @param moves An array of the possible next moves.
     */
    public void updateLegalMoves(Board[] moves) {
        hasLegalMoves = (moves.length > 0);
    } // updateLegalMoves

    /**
     * Updates the legal moves boolean based off of a nextMoves list.
     * Meant to be run while the list is created.
     * @param moves A list of the possible moves.
     */
    public void updateLegalMoves(ArrayList<Board> moves) {
        hasLegalMoves = !moves.isEmpty();
    } //updateLegalMoves

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
        return (kingCapture(PieceTypes.WHITE_KING) || kingCapture(PieceTypes.BLACK_KING) || !hasLegalMoves);
    } // isGameOver

    /**
     * Adds the value of a piece to a value.
     * @param piece The piece we are evaluating.
     * @param addTo The number to add it to.
     * @return An integer representing the new value.
     */
    public static int addPieceValue(byte piece, int addTo) {
        switch (piece) {
            case PieceTypes.WHITE_PAWN -> addTo += 1;
            case PieceTypes.BLACK_PAWN -> addTo += 1;
            case PieceTypes.WHITE_KNIGHT -> addTo += 3;
            case PieceTypes.BLACK_KNIGHT -> addTo += 3;
            case PieceTypes.WHITE_BISHOP -> addTo += 3;
            case PieceTypes.BLACK_BISHOP -> addTo += 3;
            case PieceTypes.WHITE_ROOK -> addTo += 5;
            case PieceTypes.BLACK_ROOK -> addTo += 5;
            case PieceTypes.WHITE_QUEEN -> addTo += 9;
            case PieceTypes.BLACK_QUEEN -> addTo += 9;
            default -> {
            } //switch
        } //switch
        return addTo;
    } //addPieceValue(byte, int)

    /**
     * Evaluates the material advantage on the board.
     * @return an integer representing the material advantage.
     */
    public int material() {
        int material = 0;
        for (int i = 0; i < 64; i++) {
            byte piece = getSquare(i);
            /* If the square is empty ignore it. */
            if (piece == PieceTypes.EMPTY) {
                continue;
            } //if
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
                } //return nothing for default
            } //switch
            this.pieceCount++;
            if (this.engineColor == PieceTypes.BLACK) {
                material = 0 - material;
            } //if
        } //for
        return material;
    } //material()

    /**
     * Calculates the number of points won or lost by the engine at the end of a
     * game.
     *
     * @return 1.0 if the game was won, 0.0 if the game was lost, or 0.5 in the
     *         event of a stalemate
     *         or draw
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
     * Checks if the game state is a legal position. If the king is in check, the
     * position is not
     * legal.
     */
    public boolean inCheck(byte kingColor) {
        int[] straightMoves = { -8, -1, 1, 8 };
        int[] diagMoves = { -9, -7, 7, 9 };
        int[] LMoves = { -17, -15, -10, -6, 6, 10, 15, 17 };
        int[] pawnCapturesWhite = {-9, 9};
        int[] pawnCapturesBlack = {-7, 7};
        int[] pawnCaptures = (kingColor != PieceTypes.WHITE) ? pawnCapturesWhite : pawnCapturesBlack;
        int[] kingMoves = { -9, -8, -7, -1, 1, 7, 8, 9 };

        int kingSquare = -1;

        /*
         * Set the piece to the king we're looking for, and the opposing pieces
         * correctly.
         */
        byte piece = PieceTypes.BLACK_KING;
        byte oppPawn = PieceTypes.WHITE_PAWN;
        byte oppBishop = PieceTypes.WHITE_BISHOP;
        byte oppKnight = PieceTypes.WHITE_KNIGHT;
        byte oppRook = PieceTypes.WHITE_ROOK;
        byte oppQueen = PieceTypes.WHITE_QUEEN;
        byte oppKing = PieceTypes.WHITE_KING;

        if (kingColor == PieceTypes.WHITE) {
            piece = PieceTypes.WHITE_KING;
            oppPawn = PieceTypes.BLACK_PAWN;
            oppKnight = PieceTypes.BLACK_KNIGHT;
            oppBishop = PieceTypes.BLACK_BISHOP;
            oppRook = PieceTypes.BLACK_ROOK;
            oppQueen = PieceTypes.BLACK_QUEEN;
            oppKing = PieceTypes.BLACK_KING;
        } // if

        /* Find the correct kings square. */
        for (int i = 0; i < 64; i++) {
            if (this.getSquare(i) == piece) {
                kingSquare = i;
                break;
            } // if
        } // if

        /* If the king isn't on the board, its not a legal position */
        if (kingSquare < 0) {
            return true;
        } // if

        int row = kingSquare % 8;
        int col = kingSquare / 8;

        /* Check if the king is in check from any pawns. */
        for (int move : pawnCaptures) {
            if ((kingSquare + move <= 63) && (kingSquare + move >= 0)
                    && (getSquare(kingSquare + move) == oppPawn)) {
                return true;
            } // if
        } // for

        /* Check if the king is in check from any knights */
        for (int move : LMoves) {
            int endingSquare = kingSquare + move;
            int endingRow = endingSquare % 8;
            int endingCol = endingSquare / 8;

            if (endingSquare < 0 || endingSquare > 63) {
                continue;
            } // if
            byte endingPiece = getSquare(endingSquare);

            /*
             * Checks to make sure it doesn't wrap around, is in bounds, and is a opposing
             * knight
             */
            if ((Math.abs(endingRow - row) <= 2) && (Math.abs(endingCol - col) <= 2)
                    && (endingPiece == oppKnight)) {
                return true;
            } // if
        } // for

        /*
         * Check if the king is in check from any rooks or queens. This occurs if the
         * first piece it
         * sees in any straight direction is an opposite rook or queen.
         */
        for (int move : straightMoves) {
            int endingSquare = kingSquare;
            int endingRow;
            int endingCol;
            byte endingPiece;

            /*
             * Checks that it doesn't wrap around, is in legal bounds, and is an opposing R
             * or Q
             */
            /* Continue moving the piece in the direction while it can */
            while (true) {
                endingSquare += move;
                endingRow = endingSquare % 8;
                endingCol = endingSquare / 8;

                /*
                 * Either the columns or the rows must still be the same to avoid a wraparound.
                 */
                if (endingCol != col && endingRow != row) {
                    break;
                } // if

                /* Check that it remains within bounds. */
                if ((endingSquare > 63 || (endingSquare < 0))) {
                    break;
                } // if

                endingPiece = this.getSquare(endingSquare);

                /* Check that the square is either empty or an opposing color. */
                if (endingPiece != PieceTypes.EMPTY && pieceColor(endingPiece) == this.turnColor) {
                    break;
                } // if

                /* If it was an opponents piece, it can't go any further */
                if ((Board.pieceColor(endingPiece) != this.turnColor) && (endingPiece != PieceTypes.EMPTY)) {
                    if (endingPiece == oppQueen || endingPiece == oppRook) {
                        return true;
                    } // if
                    break;
                } // if
            } // while(true)
        }
        /*
         * Check if the king is in check from any bishops or queens. See above, but
         * diagonal
         */
        for (int move : diagMoves) {
            int endingSquare = kingSquare;
            int endingRow;
            int endingCol;
            byte endingPiece;

            /* Continue moving the piece in the direction while it can */
            while (true) {
                endingSquare += move;
                endingRow = endingSquare % 8;
                endingCol = endingSquare / 8;

                /*
                 * The difference between the starting column and ending column and starting row
                 * and
                 * ending row must be equal for it to be a valid move
                 */
                if (Math.abs(endingCol - col) != Math.abs(endingRow - row)) {
                    break;
                } // if

                /* Check that it remains within bounds. */
                if ((endingSquare > 63 || (endingSquare < 0))) {
                    break;
                } // if

                endingPiece = this.getSquare(endingSquare);

                /* Check that the square is either empty or an opposing color. */
                if (endingPiece != PieceTypes.EMPTY && pieceColor(endingPiece) == this.turnColor) {
                    break;
                } // if

                /* If it was an opponents piece, it can't go any further */
                if (Board.pieceColor(endingPiece) != this.turnColor && (endingPiece != PieceTypes.EMPTY)) {
                    if (endingPiece == oppQueen || endingPiece == oppBishop) {
                        return true;
                    } // if
                    break;
                } // if
            } // while(true)
        } // for
        /* Check if it is in check from the opposing king. */
        for (int move : kingMoves) {
            int endingSquare = kingSquare + move;
            int endingRow = endingSquare % 8;
            int endingCol = endingSquare / 8;

            if ((endingSquare < 0) || (endingSquare > 63)) {
                continue;
            } // if
            byte endingPiece = this.getSquare(endingSquare);

            /*
             * The move is valid if it is in bounds, and either to an empty square or to a
             * square
             * with an opponent piece. It also must be within 1 row and 1 column, or it has
             * wrapped
             * around.
             */
            if ((Math.abs(endingRow - row) <= 1) && (Math.abs(endingCol - col) <= 1)
                    && ((endingPiece == PieceTypes.EMPTY)
                            || (Board.pieceColor(endingPiece) != this.turnColor))) {
                if (endingPiece == oppKing) {
                    return true;
                }
            } // if
        } // for
        return false;
    } // isLegal()

    /**
     * Create a random (legal) nextMove
     * 
     * @return a Board of a random move, or null if there are no legal moves.
     */
    public Board ranMove() {
        Board move;
        ArrayList<Integer> onSquares = new ArrayList<>();
        Random rand = new Random();
        for (int square = 0; square < 64; square++) {
            byte piece = getSquare(square);
            if (piece != PieceTypes.EMPTY && isColor(piece, this.turnColor)) {
                onSquares.add(square);
            } // if
        } // for

        while (!onSquares.isEmpty()) {
            int selection = rand.nextInt(onSquares.size());
            int square = onSquares.get(selection);
            byte piece = getSquare(square);
            Board[] pieceMoves = generatePieceMoves(piece, square);
            ArrayList<Board> legalMoves = new ArrayList<>();
            for (Board pieceMove : pieceMoves) {
                if (!pieceMove.inCheck(this.turnColor)) {
                    legalMoves.add(pieceMove);
                } // if
            } // for

            if (legalMoves.isEmpty()) {
                onSquares.remove(selection);
            } else {
                move = legalMoves.get(rand.nextInt(legalMoves.size()));
                move.turnColor = oppColor();
                return move;
            }
            updateLegalMoves(legalMoves);
        }
        return null;
    }

    /**
     * Creates a random weighted move from all possible next moves. More likely to return a capture of higher value.
     * @param rand A random object.
     * @return A board representing the move
     */
    public Board ranWeightedMove(Random rand) {
        Board[] nextMoves = this.nextMoves();
        int totalWeight = 0;

        /* If there aren't any legal moves, return null */
        if (nextMoves.length == 0) {
            return null; 
        } //if

        for (Board move : nextMoves) {
            totalWeight+= move.moveWeight;
        } //for

        int random = rand.nextInt(totalWeight);

        totalWeight = 0;
        /* Return the first move greater than the random number. */
        for (Board move : nextMoves) {
            totalWeight+=move.moveWeight;
            if (totalWeight > random) {
                return move;
            } //if
        } //for
        return null;
    } //ranWeightedMove(Random)

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
                if (!pieceMove.inCheck(this.turnColor)) {
                    pieceMove.turnColor = this.oppColor();
                    nextPositions[numPossibleMoves] = pieceMove;
                    if (pieceMove.inCheck(this.oppColor())) {
                        pieceMove.moveWeight += 3;
                    }
                    numPossibleMoves++;
                    if (numPossibleMoves >= nextPositions.length) {
                        /* Expand the array if it's full. */
                        nextPositions = Arrays.copyOf(nextPositions, numPossibleMoves * 2);
                    } // if
                } // if
            } // for
        } // for

        /* Update if there are legal moves left. */
        updateLegalMoves(nextPositions);
        /* Return only the legal game states in a correctly sized array */
        return Arrays.copyOf(nextPositions, numPossibleMoves);
    } // nextMoves

    /**
     * Generates an array of all possible moves by a particular piece on the board.
     *
     * @param piece The piece to generate moves for
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
            case PieceTypes.WHITE_KING, PieceTypes.BLACK_KING -> pieceMoves = PieceMoves.kingMoves(square, color, this);
            default -> throw new AssertionError();
        } // switch
        return pieceMoves;
    } // generatePieceMoves

    /**
     * Make a deep copy of the board.
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
