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
    public int enginePieceCount;

    public int playerPieceCount;

    public boolean canCastle;

    public int whiteKingSquare;

    public int blackKingSquare;

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
        this.moveWeight = 2;
        this.enginePieceCount = 0;
        this.playerPieceCount = 0;
        this.canCastle = true;
        this.whiteKingSquare = 32;
        this.blackKingSquare = 39;
    } // Board()

    /**
     * Sets a square on the board to a piece.
     *
     * @param square The square index to be set (0-63 inclusive)
     * @param piece  The piece type (defined in PieceTypes)
     */
    public void setSquare(int square, byte piece) {
        /* Interpret the row and column based off of the square */
        int col = square >> 3;
        int row = (square & 7) >> 1;

        /*
         * Clears the half of the byte that the piece will be stored on, and shifts the
         * piece
         * appropriately
         */
        if ((square & 1) == 0) {
            this.board[col][row] = (byte) ((this.board[col][row] & 0x0f) | piece << 4);
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
        int col = square >> 3;
        int row = (square & 7) >> 1;

        /* Builds the piece by returning the proper half of the byte */
        if ((square & 1) == 0) {
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

    public boolean inCheck(byte color) {
        return PieceMoves.inCheck(this, color);
    } // inCheck(byte color)

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
        } // for
        if (this.engineColor == PieceTypes.BLACK) {
            material = 0 - material;
        } // if
        return material;
    } // material()
    public int whiteMaterial() {
        int material = 0;
        for (int i = 0; i < 64; i++) {
            byte piece = getSquare(i);
            /* If the square is empty ignore it. */
            if (piece == PieceTypes.EMPTY && pieceColor(piece) == PieceTypes.WHITE) {
                continue;
            } // if
            switch (piece) {
                case PieceTypes.WHITE_PAWN -> material += 1;
                case PieceTypes.WHITE_KNIGHT -> material += 3;
                case PieceTypes.WHITE_BISHOP -> material += 3;
                case PieceTypes.WHITE_ROOK -> material += 5;
                case PieceTypes.WHITE_QUEEN -> material += 9;
                default -> {
                } // return nothing for default
            } // switch
            if (engineColor == PieceTypes.WHITE) {
            this.enginePieceCount++;
            } else {
                this.playerPieceCount++;
            }
        } // for
        return material;
    } // material()

    public int blackMaterial() {
        int material = 0;
        for (int i = 0; i < 64; i++) {
            byte piece = getSquare(i);
            /* If the square is empty ignore it. */
            if (piece == PieceTypes.EMPTY && pieceColor(piece) == PieceTypes.BLACK) {
                continue;
            } // if
            switch (piece) {
                case PieceTypes.BLACK_PAWN -> material += 1;
                case PieceTypes.BLACK_KNIGHT -> material += 3;
                case PieceTypes.BLACK_BISHOP -> material += 3;
                case PieceTypes.BLACK_ROOK -> material += 5;
                case PieceTypes.BLACK_QUEEN -> material += 9;
                default -> {
                } // return nothing for default
            } // switch
            if (engineColor == PieceTypes.BLACK) {
            this.enginePieceCount++;
            } else {
                this.playerPieceCount++;
            }
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
        if (inCheck(PieceTypes.WHITE) && engineColor == PieceTypes.WHITE) {
            return 0;
        } else if (inCheck(PieceTypes.BLACK) && engineColor == PieceTypes.BLACK) {
            return 0;
        } else if (inCheck(PieceTypes.WHITE) && engineColor == PieceTypes.BLACK) {
            return 1.0;
        } else if (inCheck(PieceTypes.BLACK) && engineColor == PieceTypes.WHITE) {
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

    public boolean isCheckmate(byte color) {
        if (!inCheck(color)) return false;
        Move[] legalMoves = nextMoves();
        return legalMoves.length == 0;
    }

    public Move ranNextMove(Random rand) {
        Move[] possMoves = nextMoves();
        if (possMoves.length == 0) {
            return null;
        }
    
        int totalWeight = 0;
        for (Move move : possMoves) {
            if (move.moveWeight <= 0) {
                move.moveWeight = 1;
            }
            totalWeight += move.moveWeight;
        }
    
        if (totalWeight <= 0) {
            System.out.println("Invalid total weight: " + totalWeight);
            return null;
        }
    
        int pick = rand.nextInt(totalWeight);
        int cumulativeWeight = 0;
    
        for (Move move : possMoves) {
            cumulativeWeight += move.moveWeight;
            if (pick < cumulativeWeight) {
                return move;
            }
        }
    
        return possMoves[possMoves.length - 1];
    }
    

    /**
     * Creates a random weighted move from all possible next moves. More likely to
     * return a capture of higher value.
     * 
     * @param rand A random object.
     * @return A board representing the move
     */
    public Move ranWeightedMove(Random rand) {
        int[] moveSquares = new int[128];
        int mvsqr = 0;

        //PrintWriter pen = new PrintWriter(System.out, true);

        for (int square = 0; square < 64; square++) {
            byte piece = this.getSquare(square);

            /* Ignore it if its not our piece */
            if (piece == PieceTypes.EMPTY || !isColor(piece, this.turnColor)) {
                continue;
            }

            Move[] pieceMoves = generatePieceMoves(piece, square);
            if (pieceMoves.length != 0) {
                for (Move move : pieceMoves) {
                    for (int i = 0; i < move.moveWeight; i++) {
                        if (mvsqr > (moveSquares.length / 2)) {
                            moveSquares = Arrays.copyOf(moveSquares, moveSquares.length * 2);
                        }
                        moveSquares[mvsqr++] = square;
                    }
                }
            }
        }

        if (mvsqr == 0) {
            return null;
        }

        int random = rand.nextInt(mvsqr);
        int square = moveSquares[random];

        byte piecetoMove = getSquare(square);
        Move[] options = generatePieceMoves(piecetoMove, square);

        Move playMove = null;
        Move bestMove = options[0];
        for (Move move : options) {
            if (move.moveWeight > bestMove.moveWeight) {
                Board bestBoard = PieceMoves.movePiece(move, this);
                if (!bestBoard.inCheck(this.turnColor)) {
                    playMove = move;
                    bestMove = move;
                } // if
            } // if
        } // for
        if (playMove == null) {
            random = rand.nextInt(mvsqr);
            square = moveSquares[random];

            piecetoMove = getSquare(square);
            options = generatePieceMoves(piecetoMove, square);

            bestMove = options[0];
            for (Move move : options) {
                if (move.moveWeight > bestMove.moveWeight) {
                    Board bestBoard = PieceMoves.movePiece(move, this);
                    if (!bestBoard.inCheck(this.turnColor)) {
                        playMove = move;
                        bestMove = move;
                    } // if
                } // if
            } // for
        } // if

        if (playMove == null) {
            Move[] nextMoves = this.nextMoves();
            if (nextMoves.length == 0) {
                return null;
            } // if
            random = rand.nextInt(nextMoves.length);
            return nextMoves[random];
        }
        return playMove;
    } // ranWeightedMove(Random)

    /**
     * Creates an array of all possible next moves from this position.
     *
     * @return All possible next moves.
     */
    public Move[] nextMoves() {
        /*
         * Create an array to store all possible next moves, and an integer to store the
         * current
         * number of moves in the array.
         */
        Move[] nextPositions = new Move[50];
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
            Move[] pieceMoves = this.generatePieceMoves(piece, square);
            /* Skip if no moves are generated */
            if (pieceMoves == null || pieceMoves.length == 0) {
                continue;
            } // if
            /*
             * Add legal game states (those where the king is not in check) to the master
             * list of
             * nextPositions
             */
            for (Move pieceMove : pieceMoves) {
                Board newBoard = PieceMoves.movePiece(pieceMove, this);

                if (!newBoard.inCheck(this.turnColor)) {
                    nextPositions[numPossibleMoves] = pieceMove;
                    if (newBoard.inCheck(this.oppColor())) {
                        newBoard.turnColor = this.oppColor();
                        if (newBoard.nextMoves().length == 0) {
                            pieceMove.moveWeight *= 10000;
                        } //if
                        pieceMove.moveWeight *= 10;
                        newBoard.turnColor = this.turnColor;
                    } //if
                
                    numPossibleMoves++;
                    if (numPossibleMoves >= nextPositions.length) {
                        /* Expand the array if it's full. */
                        nextPositions = Arrays.copyOf(nextPositions, numPossibleMoves * 2);
                    } // if
                }
            } // for
        } // for
        /* Update if there are legal moves left. */
        updateLegalMoves(numPossibleMoves);
        /* Return only the legal game states in a correctly sized array */
        return Arrays.copyOf(nextPositions, numPossibleMoves);
    } // nextMoves

    public Move[] nextMoves(byte pieceColor) {
        byte oppColor = (pieceColor == PieceTypes.WHITE) ? PieceTypes.BLACK : PieceTypes.WHITE;
        /*
         * Create an array to store all possible next moves, and an integer to store the
         * current
         * number of moves in the array.
         */
        Move[] nextPositions = new Move[50];
        int numPossibleMoves = 0;
        /* Loop through the board to check all the pieces */
        for (int square = 0; square < 64; square++) {
            byte piece = getSquare(square);
            /*
             * If the square is empty, or its an opponents piece, don't bother looking at
             * the moves.
             */
            if (piece == PieceTypes.EMPTY || !isColor(piece, pieceColor)) {
                continue;
            } // if
            /* Create a new array for all the possible moves of that piece */
            Move[] pieceMoves = this.generatePieceMoves(piece, square);
            /* Skip if no moves are generated */
            if (pieceMoves == null || pieceMoves.length == 0) {
                continue;
            } // if
            /*
             * Add legal game states (those where the king is not in check) to the master
             * list of
             * nextPositions
             */
            for (Move pieceMove : pieceMoves) {
                Board newBoard = PieceMoves.movePiece(pieceMove, this);

                if (!newBoard.inCheck(pieceColor)) {
                    nextPositions[numPossibleMoves] = pieceMove;
                
                    numPossibleMoves++;
                    if (numPossibleMoves >= nextPositions.length) {
                        /* Expand the array if it's full. */
                        nextPositions = Arrays.copyOf(nextPositions, numPossibleMoves * 2);
                    } // if
                }
            } // for
        } // for
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
    public Move[] generatePieceMoves(byte piece, int square) {
        /* Create a new array that will return the types. */
        Move[] pieceMoves;
        byte color = pieceColor(piece);

        /*
         * Check the type of the piece and generate the appropriate moves. Note that for
         * slideMoves,
         * the specific color given as pieceType is irrelevant, since that bit will not
         * be looked at
         */
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
        returnState.canCastle = this.canCastle;
        returnState.whiteKingSquare = this.whiteKingSquare;
        returnState.blackKingSquare = this.blackKingSquare;
        for (int i = 0; i < this.board.length; i++) {
            System.arraycopy(this.board[i], 0, returnState.board[i], 0, this.board[0].length);
        } // for
        return returnState;
    } // copyBoard()

} // Board
