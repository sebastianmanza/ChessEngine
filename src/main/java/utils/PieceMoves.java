package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class PieceMoves {

    /**
     * An array representing the centersquares of the board.
     */
    public static final int[] CENTER_SQUARES = { 35, 36, 43, 44 };

    /**
     * Returns a weight if the center is controlled
     * 
     * @param square the controlled square we are checking
     * @return an int representing the moveWeight multiplier
     */
    public static int controlsCenter(int square) {
        for (int i = 0; i < CENTER_SQUARES.length; i++) {
            if (square == CENTER_SQUARES[i]) {
                return 3;
            } // if
        } // for
        return 1;
    } // controlsCenter

    /**
     * Promote a pawn.
     * 
     * @param startingSquare The square the pawn starts on
     * @param endingSquare   The promotion square
     * @param originalBoard  The original baord we are given.
     * @return A list of boards with the pawn promoted.
     */
    // public static Board[] promotePiece(int startingSquare, int endingSquare, Board originalBoard) {
    //     Board[] promotionMoves = new Board[4];
    //     Board newBoard = originalBoard.copyBoard();
    //     Board newBoardTwo = originalBoard.copyBoard();
    //     Board newBoardThree = originalBoard.copyBoard();
    //     Board newBoardFour = originalBoard.copyBoard();

    //     /* Need to check for piece color */

    //     if (originalBoard.getSquare(startingSquare) == Board.pieceColor(PieceTypes.BLACK)) {
    //         newBoard.setSquare(endingSquare, PieceTypes.BLACK_KNIGHT);
    //         newBoard.setSquare(startingSquare, PieceTypes.EMPTY);
    //         promotionMoves[0] = newBoard;

    //         newBoardTwo.setSquare(endingSquare, PieceTypes.BLACK_BISHOP);
    //         newBoardTwo.setSquare(startingSquare, PieceTypes.EMPTY);
    //         promotionMoves[1] = newBoardTwo;

    //         newBoardThree.setSquare(endingSquare, PieceTypes.BLACK_ROOK);
    //         newBoardThree.setSquare(startingSquare, PieceTypes.EMPTY);
    //         promotionMoves[2] = newBoardThree;

    //         newBoardFour.setSquare(endingSquare, PieceTypes.BLACK_QUEEN);
    //         newBoardFour.setSquare(startingSquare, PieceTypes.EMPTY);
    //         promotionMoves[3] = newBoardFour;
    //     } else {
    //         newBoard.setSquare(endingSquare, PieceTypes.WHITE_KNIGHT);
    //         newBoard.setSquare(startingSquare, PieceTypes.EMPTY);
    //         promotionMoves[0] = newBoard;

    //         newBoardTwo.setSquare(endingSquare, PieceTypes.WHITE_BISHOP);
    //         newBoardTwo.setSquare(startingSquare, PieceTypes.EMPTY);
    //         promotionMoves[1] = newBoardTwo;

    //         newBoardThree.setSquare(endingSquare, PieceTypes.WHITE_ROOK);
    //         newBoardThree.setSquare(startingSquare, PieceTypes.EMPTY);
    //         promotionMoves[2] = newBoardThree;

    //         newBoardFour.setSquare(endingSquare, PieceTypes.WHITE_QUEEN);
    //         newBoardFour.setSquare(startingSquare, PieceTypes.EMPTY);
    //         promotionMoves[3] = newBoardFour;

    //     }

    //     return promotionMoves;
    // } // promotePiece(int, int, Board)

    /**
     * Weight the move properly.
     * 
     * @param capturedPiece   The piece being captured.
     * @param boardState      The current board
     * @param pieceTypeWeight The amount extra weight being given (pawns have
     *                        higher, and bishops and knights)
     */
    public static void addMoveWeight(byte capturedPiece, Move move, int pieceTypeWeight) {
        switch (capturedPiece) {
            case PieceTypes.WHITE_PAWN -> move.moveWeight += 1;
            case PieceTypes.BLACK_PAWN -> move.moveWeight += 1;
            case PieceTypes.WHITE_KNIGHT -> move.moveWeight += 6;
            case PieceTypes.BLACK_KNIGHT -> move.moveWeight += 6;
            case PieceTypes.WHITE_BISHOP -> move.moveWeight += 7;
            case PieceTypes.BLACK_BISHOP -> move.moveWeight += 7;
            case PieceTypes.WHITE_ROOK -> move.moveWeight += 10;
            case PieceTypes.BLACK_ROOK -> move.moveWeight += 10;
            case PieceTypes.WHITE_QUEEN -> move.moveWeight += 18;
            case PieceTypes.BLACK_QUEEN -> move.moveWeight += 18;
            default -> {
            } // switch
        }
        move.moveWeight *= pieceTypeWeight;
    } // addMoveWeight

    /**
     * Apply a move to a board
     * 
     * @param startingSquare The original square the piece was on
     * @param endingSquare   The square for the piece to end up
     * @param originalBoard  The original board given
     * @return A board with the piece moved.
     */
    public static Board movePiece(Move move, Board originalBoard) {
        Board newBoard;
        /* Make a copy of the original game state */
        newBoard = originalBoard.copyBoard();

        /*
         * Set the ending square to the piece at the starting square. Then clear the
         * start square.
         */
        newBoard.setSquare(move.endingSquare, move.piece);
        newBoard.setSquare(move.startingSquare, PieceTypes.EMPTY);

        if (move.piece == PieceTypes.WHITE_KING) {
            newBoard.whiteKingSquare = move.endingSquare;
        } else if (move.piece == PieceTypes.BLACK_KING) {
            newBoard.blackKingSquare = move.endingSquare;
        }

        if (move.promotePiece == true) {
            if (move.piece == PieceTypes.WHITE_PAWN) {
            newBoard.setSquare(move.endingSquare, PieceTypes.WHITE_QUEEN);
            } else {
                newBoard.setSquare(move.endingSquare, PieceTypes.BLACK_QUEEN);
            }
        }


        return newBoard;

    }

    /**
     * Generates all possible moves of the pawn located on the square.
     *
     * @param square The square the pawn is located on.
     * @param color  The color of the piece.
     * @return A list of possible boards after the pawn has moved.
     */
    public static Move[] pawnMoves(int square, byte color, Board currentState) {
        /*
         * Without promotion, a pawn has a maximum of 4 possible moves: two takes and
         * two forwards(if on starting square.)
         */
        Move[] pawnMoves = new Move[4];
        int numMoves = 0;

        /* Assign some rows and directions based on color: */
        int direction = (color == PieceTypes.WHITE) ? 1 : -1;
        byte pawnType = (color == PieceTypes.WHITE) ? PieceTypes.WHITE_PAWN : PieceTypes.BLACK_PAWN;
        int[] captures = { direction + 8, direction - 8 };
        int startingRow = (color == PieceTypes.WHITE) ? 1 : 6;
        int promotionRow = (color == PieceTypes.WHITE) ? 6 : 1;

        boolean onStartingSquare = (square % 8 == startingRow);
        boolean reachPromotionSquare = (square % 8 == promotionRow);

        int forward = square + direction;

        /* If it can reach the promotion square, expand the number of possible moves. */
        if (reachPromotionSquare) {
            pawnMoves = Arrays.copyOf(pawnMoves, 12);
        } // if

        /* Create the forward moves if nothing is in front. */
        if (currentState.getSquare(forward) == PieceTypes.EMPTY) {
            if (reachPromotionSquare) {
                for (int i = 0; i < 4; i++) {
                    pawnMoves[numMoves] = new Move(square, forward, pawnType, true);
                    pawnMoves[numMoves].moveWeight += 5;
                    numMoves++;
                } // for
            } else {
                pawnMoves[numMoves] = new Move(square, forward, pawnType);
                for (int cap : captures) {
                    pawnMoves[numMoves].moveWeight *= controlsCenter(forward + cap);
                } // for
                numMoves++;

            } // if/else

            /* It can also move 2 squares forward if it's on its starting square. */
            if (onStartingSquare && currentState.getSquare(square + (2 * direction)) == PieceTypes.EMPTY) {
                pawnMoves[numMoves] = new Move(square, (square + (2 * direction)), pawnType);
                for (int cap : captures) {
                    pawnMoves[numMoves].moveWeight *= controlsCenter(forward + cap);
                } // for
                numMoves++;
            } // if
        } // if

        /* Captures (the diagonal directions) */
        for (int cap : captures) {
            int capture = square + cap; // the capture square

            /* Don't check the square if its not valid. */
            if ((capture > 63) || (capture < 0)) {
                continue;
            } // if

            byte piece = currentState.getSquare(capture);
            /* Check if there is an opponents piece on the capture square. */
            if ((piece != PieceTypes.EMPTY) && (Board.pieceColor(piece) != color)) {
                if (reachPromotionSquare) {
                    for (int i = 0; i < 4; i++) {
                        pawnMoves[numMoves] = new Move(square, capture, pawnType, true);
                        pawnMoves[numMoves].moveWeight += 5;
                        addMoveWeight(piece, pawnMoves[numMoves], 3);
                        numMoves++;
                    } // for
                } else {
                    pawnMoves[numMoves] = new Move (square, capture, pawnType);
                    addMoveWeight(piece, pawnMoves[numMoves], 3);
                    for (int capcap : captures) {
                        pawnMoves[numMoves].moveWeight *= controlsCenter(forward + capcap);
                    } // for
                    numMoves++;
                } // if/else
            } // if
        } // for

        // Need to add en passant.

        return Arrays.copyOfRange(pawnMoves, 0, numMoves);
    }

    public static Move[] knightMoves(int square, byte color, Board currentState) {
        /* A knight can only move a maximum of 8 ways. */
        Move[] knightMoves = new Move[8];
        int numMoves = 0;
        int[] LMoves = { -17, -15, -10, -6, 6, 10, 15, 17 };
        int row = square % 8;
        int col = square / 8;
        byte knightType = (color == PieceTypes.WHITE) ? PieceTypes.WHITE_KNIGHT : PieceTypes.BLACK_KNIGHT;

        for (int LMove : LMoves) {
            int endingSquare = square + LMove;
            int endingRow = endingSquare % 8;
            int endingCol = endingSquare / 8;
            /* If it's out of bounds, don't check it. */
            if ((endingSquare > 63) || endingSquare < 0) {
                continue;
            }
            byte endingPiece = currentState.getSquare(endingSquare);
            byte endingPieceColor = Board.pieceColor(endingPiece);

            /*
             * Checks to make sure it doesn't wrap around and stays in bounds and checks to
             * make sure it is not our piece.
             */
            if ((Math.abs(endingRow - row) <= 2)
                    && (Math.abs(endingCol - col) <= 2)
                    && ((endingPiece == PieceTypes.EMPTY) || (endingPieceColor != color))) {
                knightMoves[numMoves] = new Move(square, endingSquare, knightType);

                /* If it is a capture, weight its move a little. */
                if (endingPieceColor != color && endingPiece != PieceTypes.EMPTY) {
                    addMoveWeight(endingPiece, knightMoves[numMoves], 2);
                }
                for (int center : LMoves) {
                    knightMoves[numMoves].moveWeight *= controlsCenter(endingSquare + center);
                }
                numMoves++;
            } // if
        } // for
        return Arrays.copyOfRange(knightMoves, 0, numMoves);
    } // knightMoves

    public static Move[] slideMoves(int square, byte color, byte pieceType, Board currentState) {
        /*
         * The queen can move 28 different ways if placed correctly, so lets dynamically
         * size it
         */
        List<Move> slideMoves = new ArrayList<>();

        int endingSquare;
        int endingCol;
        int endingRow;
        byte pieceOnSquare;

        /* The offsets for the different move types */
        int[] straightMoves = { -8, -1, 1, 8 };
        int[] diagMoves = { -9, -7, 7, 9 };

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

                    pieceOnSquare = currentState.getSquare(endingSquare);
                    byte pieceColor = Board.pieceColor(pieceOnSquare);

                    /* Check that the square is either empty or an opposing color. */
                    if (pieceOnSquare != PieceTypes.EMPTY && pieceColor == color) {
                        break;
                    } // if

                    slideMoves.add(new Move(square, endingSquare, currentState.getSquare(square)));
                    /*
                     * If the piece was starting on h1, it can no longer castle (either there was a
                     * piece not a rook on h1, or the rook moved)
                     */
                    // if ((color == PieceTypes.WHITE && square == 56) || (color == PieceTypes.BLACK && square == 63)) {
                    //     slideMoves.get(slideMoves.size() - 1).canCastle = false;
                    // } // if

                    /* If it was an opponents piece, it can't go any further */
                    if ((pieceColor != color) && (pieceOnSquare != PieceTypes.EMPTY)) {
                        addMoveWeight(pieceOnSquare, slideMoves.get(slideMoves.size() - 1), 1);
                        break;
                    } // if
                } // while(true)

            } // for
        } // if

        /* Generate diagonal moves if the piece is a bishop or a queen */
        if ((pieceType == PieceTypes.WHITE_BISHOP) || (pieceType == PieceTypes.WHITE_QUEEN)) {
            for (int move : diagMoves) {
                endingSquare = square;

                /* Continue moving the piece in the direction while it can */
                while (true) {
                    endingSquare += move;
                    endingRow = endingSquare % 8;
                    endingCol = endingSquare / 8;

                    /*
                     * The difference between the starting column and ending column
                     * and starting row and ending row must be equal for it to be a valid move
                     */
                    if (Math.abs(endingCol - col) != Math.abs(endingRow - row)) {
                        break;
                    } // if

                    /* Check that it remains within bounds. */
                    if ((endingSquare > 63 || (endingSquare < 0))) {
                        break;
                    } // if

                    pieceOnSquare = currentState.getSquare(endingSquare);
                    byte pieceColor = Board.pieceColor(pieceOnSquare);

                    /* Check that the square is either empty or an opposing color. */
                    if (pieceOnSquare != PieceTypes.EMPTY && pieceColor == color) {
                        break;
                    } // if

                    slideMoves.add(new Move(square, endingSquare, currentState.getSquare(square)));

                    /* If it was an opponents piece, it can't go any further */
                    if ((pieceOnSquare != PieceTypes.EMPTY) && pieceColor != color) {
                        addMoveWeight(pieceOnSquare, slideMoves.get(slideMoves.size() - 1), 1);
                        if (pieceType == PieceTypes.WHITE_BISHOP) {
                            addMoveWeight(pieceOnSquare, slideMoves.get(slideMoves.size() - 1), 1);
                        }
                        break;
                    } // if
                } // while(true)
            } // for
        } // if
        return slideMoves.toArray(Move[]::new);
    } // slideMoves

    public static Move[] kingMoves(int square, byte color, Board currentState) {
        Move[] kingMoves = new Move[9];
        int numMoves = 0;
        int row = square % 8;
        int col = square / 8;
        byte kingType = (color == PieceTypes.WHITE) ? PieceTypes.WHITE_KING : PieceTypes.BLACK_KING;

        int[] adjMoves = { -9, -8, -7, -1, 1, 7, 8, 9 };

        for (int move : adjMoves) {
            int endingSquare = square + move;
            int endingRow = endingSquare % 8;
            int endingCol = endingSquare / 8;

            if ((endingSquare < 0) || (endingSquare > 63)) {
                continue;
            } // if
            byte piece = currentState.getSquare(endingSquare);
            byte pieceColor = Board.pieceColor(piece);

            /*
             * The move is valid if it is in bounds, and either to an empty square or to a
             * square with an opponent piece.
             * It also must be within 1 row and 1 column, or it has wrapped around.
             */
            if ((Math.abs(endingRow - row) <= 1)
                    && (Math.abs(endingCol - col) <= 1)
                    && ((piece == PieceTypes.EMPTY) || (pieceColor != color))) {
                kingMoves[numMoves] = new Move(square, endingSquare, kingType);
                kingMoves[numMoves].moveWeight -= 1;
                // kingMoves[numMoves].kingSquare = endingSquare;
                // kingMoves[numMoves].canCastle = false;

                if (pieceColor != color) {
                    Board.addPieceValue(piece, kingMoves[numMoves].moveWeight);
                }
                numMoves++;
            } // if
        } // for

        /*
         * Castling (check if it can castle, the squares are empty, its not in check or
         * going through check
         */
        // if (currentState.canCastle
        // && color == PieceTypes.WHITE
        // && currentState.getSquare(40) == PieceTypes.EMPTY
        // && currentState.getSquare(48) == PieceTypes.EMPTY
        // && !currentState.inCheck(color)
        // && !movePiece(square, 40, currentState).inCheck(color)
        // && !movePiece(square, 48, currentState).inCheck(color)) {
        // Board castle = movePiece(square, 48, currentState);
        // movePiece(56, 40, castle);
        // castle.moveWeight *= 4;
        // castle.canCastle = false;
        // kingMoves[numMoves++] = castle;
        // } else if (currentState.canCastle
        // && color == PieceTypes.BLACK
        // && currentState.getSquare(47) == PieceTypes.EMPTY
        // && currentState.getSquare(55) == PieceTypes.EMPTY
        // && !currentState.inCheck(color)
        // && !movePiece(square, 47, currentState).inCheck(color)
        // && !movePiece(square, 55, currentState).inCheck(color)) {
        // Board castle = movePiece(square, 55, currentState);
        // movePiece(63, 47, castle);
        // castle.moveWeight *= 4;
        // castle.canCastle = false;
        // kingMoves[numMoves++] = castle;

        // }
        return Arrays.copyOfRange(kingMoves, 0, numMoves);

    } // kingMoves

    public static boolean inCheck(Board boardToCheck, byte kingColor) {
        /* The king we want to see if is in check. */
        byte piece = (kingColor == PieceTypes.WHITE) ? PieceTypes.WHITE_KING : PieceTypes.BLACK_KING;
        int kingSquare = (kingColor == PieceTypes.WHITE) ? boardToCheck.whiteKingSquare : boardToCheck.blackKingSquare;

        return (inCheckFromPawns(kingSquare, piece, boardToCheck) || inCheckFromKnights(kingSquare, piece, boardToCheck)
                || inCheckFromSlidePiece(kingSquare, piece, boardToCheck)
                || inCheckfromKings(kingSquare, piece, boardToCheck));
    }

    public static boolean inCheckFromPawns(int kingSquare, byte king, Board board) {
        byte kingColor = Board.pieceColor(king);
        int[] pawnChecks = (kingColor == PieceTypes.WHITE) ? new int[] { -9, 7 } : new int[] { -7, 9 };

        byte oppPawn = (kingColor == PieceTypes.WHITE) ? PieceTypes.BLACK_PAWN : PieceTypes.WHITE_PAWN;
        for (int move : pawnChecks) {
            int checkSquare = kingSquare + move;

            /* If it is out of bounds its not possible */
            if (checkSquare > 63 || checkSquare < 0) {
                continue;
            } // if

            /* If the square you move to is an opposite pawn, youre in check. */
            if (board.getSquare(checkSquare) == oppPawn) {
                return true;
            } // if
        } // for
        return false;
    }

    public static boolean inCheckFromKnights(int kingSquare, byte king, Board board) {
        int[] knightChecks = { -17, -15, -10, -6, 6, 10, 15, 17 };
        int row = kingSquare % 8;
        int col = kingSquare / 8;
        byte kingColor = Board.pieceColor(king);

        byte oppKnight = (kingColor == PieceTypes.WHITE) ? PieceTypes.BLACK_KNIGHT
                : PieceTypes.WHITE_KNIGHT;
        for (int move : knightChecks) {
            int checkSquare = kingSquare + move;
            int checkRow = checkSquare % 8;
            int checkCol = checkSquare / 8;

            /* If it is out of bounds its not possible */
            if (checkSquare > 63 || checkSquare < 0) {
                continue;
            } // if

            byte checkPiece = board.getSquare(checkSquare);

            if ((Math.abs(checkRow - row) > 2) || (Math.abs(checkCol - col)) > 2) {
                continue;
            } // if

            if (checkPiece == oppKnight)
                return true;
        } // for
        return false;
    } // inCheckFromKnights(int, byte, Board

    public static boolean inCheckFromSlidePiece(int kingSquare, byte king, Board board) {
        int[] horizChecks = { -8, -1, 1, 8 };
        int[] diagChecks = { -9, -7, 7, 9 };
        int row = kingSquare % 8;
        int col = kingSquare / 8;
        byte kingColor = Board.pieceColor(king);

        byte oppBishop = (kingColor == PieceTypes.WHITE) ? PieceTypes.BLACK_BISHOP
                : PieceTypes.WHITE_BISHOP;
        byte oppRook = (kingColor == PieceTypes.WHITE) ? PieceTypes.BLACK_ROOK : PieceTypes.WHITE_ROOK;
        byte oppQueen = (kingColor == PieceTypes.WHITE) ? PieceTypes.BLACK_QUEEN : PieceTypes.WHITE_QUEEN;

        /* Check for bishops and queens */
        for (int move : diagChecks) {
            int checkSquare = kingSquare;
            /* Move in the diagonal direction */
            while (true) {
                checkSquare += move;
                int checkRow = checkSquare % 8;
                int checkCol = checkSquare / 8;

                /* Col and row must be the same */
                if (Math.abs(checkRow - row) != Math.abs(checkCol - col)) {
                    break;
                } // if

                if (checkSquare > 63 || checkSquare < 0) {
                    break;
                }

                byte checkPiece = board.getSquare(checkSquare);

                /* If it is empty, skip to the next */
                if (checkPiece == PieceTypes.EMPTY) {
                    continue;
                }

                /* If it is a piece of the same color, break the loop */
                if (Board.pieceColor(checkPiece) == board.turnColor) {
                    break;
                } // if

                /* If it is an opposite bishop or queen, youre in check */
                if (checkPiece == oppQueen || checkPiece == oppBishop) {
                    return true;
                } else {
                    break;
                } // if/else
            } // while(true)
        } // for

        for (int move : horizChecks) {
            int checkSquare = kingSquare;
            /* Move in the horizontal direction */
            while (true) {
                checkSquare += move;
                int checkRow = checkSquare % 8;
                int checkCol = checkSquare / 8;

                /* Col must be the same and row must be the same */
                if (checkCol != col && checkRow != row) {
                    break;
                } // if

                if (checkSquare > 63 || checkSquare < 0) {
                    break;
                }

                byte checkPiece = board.getSquare(checkSquare);

                if (checkPiece == PieceTypes.EMPTY) {
                    continue;
                }

                if (Board.pieceColor(checkPiece) == board.turnColor) {
                    break;
                }

                if (checkPiece == oppQueen || checkPiece == oppRook) {
                    return true;
                } else {
                    break;
                } // if/else
            } // while(true)
        } // for
        /* If nothing has been found, its not in check from a slide piece */
        return false;
    } // inCheckFromSlidePiece(int, byte, board)

    public static boolean inCheckfromKings(int kingSquare, byte king, Board board) {
        int[] kingChecks = { -9, -8, -7, -1, 1, 7, 8, 9 };
        int row = kingSquare % 8;
        int col = kingSquare / 8;

        byte oppKing = (Board.pieceColor(king) == PieceTypes.WHITE) ? PieceTypes.BLACK_KING : PieceTypes.WHITE_KING;

        for (int move : kingChecks) {
            int checkSquare = kingSquare + move;
            int checkRow = checkSquare % 8;
            int checkCol = checkSquare / 8;

            /* Check if its in bounds */
            if (checkSquare < 0 || checkSquare > 63) {
                break;
            }

            byte checkPiece = board.getSquare(checkSquare);

            if (Math.abs(checkRow - row) > 1 || Math.abs(checkCol - col) > 1) {
                break;
            }

            if (checkPiece == oppKing) {
                return true;
            }

        }
        return false;
    }
} // class PieceMoves
