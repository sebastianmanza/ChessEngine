package utils.ANNUtils;

import java.util.Arrays;

import utils.Board;
import utils.Move;
import utils.PieceMoves;
import utils.PieceTypes;

/**
 * Create a small vector representing all key aspects of a board.
 * [turnColor, pawnCountW, knightCountW, bishopCountW,
 * rookCountW, queenCountW, pawnCountB, knightCountB, bishopCountB, rookCountB,
 * queenCountB, kingPawnAdjW, kingPawnAdjB, kingDistfromCenterW,
 * kingDistfromCenterB,kingPiecesOnSidesW, kingPiecesOnSidesB, doubPawnCountW,
 * doubPawnCountB, chainedPawnCountW, chainedPawnCountB, centerControlNum,
 * numLegalMovesW, numLegalMovesB, devW, devB, threatW, threatB,
 * controlledFilesW, controlledFilesB, avgPawnRowW, avgPawnRowB, SpaceControlW,
 * SpaceControlB, piecesDefendedW, piecesDefendedB, bishopPairW, bishopPairB]
 */
public class BetterVector {
    public static final int TURN_COLOR = 0;
    public static final int PAWN_COUNT_W = 1;
    public static final int KNIGHT_COUNT_W = 2;
    public static final int BISHOP_COUNT_W = 3;
    public static final int ROOK_COUNT_W = 4;
    public static final int QUEEN_COUNT_W = 5;
    public static final int PAWN_COUNT_B = 6;
    public static final int KNIGHT_COUNT_B = 7;
    public static final int BISHOP_COUNT_B = 8;
    public static final int ROOK_COUNT_B = 9;
    public static final int QUEEN_COUNT_B = 10;
    public static final int KING_PAWN_ADJ_W = 11;
    public static final int KING_PAWN_ADJ_B = 12;
    public static final int KING_DIST_FROM_CENTER_W = 13;
    public static final int KING_DIST_FROM_CENTER_B = 14;
    public static final int KING_PIECES_ON_SIDES_W = 15;
    public static final int KING_PIECES_ON_SIDES_B = 16;
    public static final int DOUB_PAWN_COUNT_W = 17;
    public static final int DOUB_PAWN_COUNT_B = 18;
    public static final int CHAINED_PAWN_COUNT_W = 19;
    public static final int CHAINED_PAWN_COUNT_B = 20;
    public static final int CENTER_CONTROL_W = 21;
    public static final int CENTER_CONTROL_B = 22;
    public static final int NUM_LEGAL_MOVES_W = 23;
    public static final int NUM_LEGAL_MOVES_B = 24;
    public static final int DEV_W = 25;
    public static final int DEV_B = 26;
    public static final int THREAT_W = 27;
    public static final int THREAT_B = 28;
    public static final int CONTROLLED_FILES_W = 29;
    public static final int CONTROLLED_FILES_B = 30;
    public static final int AVG_PAWN_ROW_W = 31;
    public static final int AVG_PAWN_ROW_B = 32;
    public static final int SPACE_CONTROL_W = 33;
    public static final int SPACE_CONTROL_B = 34;
    public static final int PIECES_DEFENDED_W = 35;
    public static final int PIECES_DEFENDED_B = 36;
    public static final int NUM_CHECKS_WHITE = 37;
    public static final int NUM_CHECKS_BLACK = 38;

    public double[] vec = new double[37];

    public double[] normalizeVector() {
        double[] minValues = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0};
        double[] maxValues = {
            1, // TURN_COLOR
            8, // PAWN_COUNT_W
            10, // KNIGHT_COUNT_W (max promotions to knights)
            10, // BISHOP_COUNT_W
            10, // ROOK_COUNT_W
            9, // QUEEN_COUNT_W (8 promotions + 1 original)
            8, // PAWN_COUNT_B
            10, // KNIGHT_COUNT_B
            10, // BISHOP_COUNT_B
            10, // ROOK_COUNT_B
            9, // QUEEN_COUNT_B
            8, // KING_PAWN_ADJ_W
            8, // KING_PAWN_ADJ_B
            4, // KING_DIST_FROM_CENTER_W
            4, // KING_DIST_FROM_CENTER_B
            8, // KING_PIECES_ON_SIDES_W
            8, // KING_PIECES_ON_SIDES_B
            8, // DOUB_PAWN_COUNT_W
            8, // DOUB_PAWN_COUNT_B
            8, // CHAINED_PAWN_COUNT_W
            8, // CHAINED_PAWN_COUNT_B
            32, // CENTER_CONTROL_W
            32, // CENTER_CONTROL_B
            218, // NUM_LEGAL_MOVES_W (all moves for all pieces)
            218, // NUM_LEGAL_MOVES_B
            8, // DEV_W
            8, // DEV_B
            64, // THREAT_W
            64, // THREAT_B
            8, // CONTROLLED_FILES_W
            8, // CONTROLLED_FILES_B
            8, // AVG_PAWN_ROW_W
            8, // AVG_PAWN_ROW_B
            64, // SPACE_CONTROL_W
            64, // SPACE_CONTROL_B
            64, // PIECES_DEFENDED_W
            64, // PIECES_DEFENDED_B
            10,
            10
        };
    
        double[] normalized = new double[vec.length];
        for (int i = 0; i < vec.length; i++) {
            normalized[i] = (vec[i] - minValues[i]) / (maxValues[i] - minValues[i]);
        }
        return normalized;
    }
    

    public double[] createVector(Board board) {
        setVec(board);
        vec = normalizeVector();
        return vec;
    }

    public void setVec(Board board) {
        vec[0] = board.turnColor;
        setPieceCounts(board);
        setKingSafety(board);
        setPawnStruct(board);
        setControlled(board);


    }

    public void setPieceCounts(Board board) {
        for (int i = 0; i < 64; i++) {
            byte piece = board.getSquare(i);

            switch (piece) {
                case PieceTypes.WHITE_PAWN -> this.vec[PAWN_COUNT_W]++;
                case PieceTypes.WHITE_KNIGHT -> this.vec[KNIGHT_COUNT_W]++;
                case PieceTypes.WHITE_BISHOP -> this.vec[BISHOP_COUNT_W]++;
                case PieceTypes.WHITE_ROOK -> this.vec[ROOK_COUNT_W]++;
                case PieceTypes.WHITE_QUEEN -> this.vec[QUEEN_COUNT_W]++;
                case PieceTypes.BLACK_PAWN -> this.vec[PAWN_COUNT_B]++;
                case PieceTypes.BLACK_KNIGHT -> this.vec[KNIGHT_COUNT_B]++;
                case PieceTypes.BLACK_BISHOP -> this.vec[BISHOP_COUNT_B]++;
                case PieceTypes.BLACK_ROOK -> this.vec[ROOK_COUNT_B]++;
                case PieceTypes.BLACK_QUEEN -> this.vec[QUEEN_COUNT_B]++;
            }
        }
    }

    public void setKingSafety(Board board) {
        int Wsquare = board.whiteKingSquare;
        int Bsquare = board.blackKingSquare;

        /* Check if the adjacent squares are pawns protecting it or pieces of its own color on the sides. */
        for (int square : adjSquares(Wsquare)) {
            byte piece = board.getSquare(square);
            if (square == (Wsquare - 8) || square == (Wsquare + 8) && Board.pieceColor(piece) == PieceTypes.WHITE) {
                this.vec[KING_PIECES_ON_SIDES_W]++;
            }
            if (piece == PieceTypes.WHITE_PAWN) {
                this.vec[KING_PAWN_ADJ_W]++;
            }
        }

        for (int square : adjSquares(Bsquare)) {
            byte piece = board.getSquare(square);
            if (square == (Wsquare - 8) || square == (Wsquare + 8) && Board.pieceColor(piece) == PieceTypes.WHITE) {
                this.vec[KING_PIECES_ON_SIDES_B]++;
            }
            if (board.getSquare(square) == PieceTypes.BLACK_PAWN) {
                this.vec[KING_PAWN_ADJ_B]++;
            }
        }

        /* Check the distance from the center */
        int WCol = Wsquare / 8;
        int WRow = Wsquare % 8;
        this.vec[KING_DIST_FROM_CENTER_W] += (Math.abs(WCol - 3.5) + Math.abs(WRow - 3.5));

        int BCol = Bsquare / 8;
        int BRow = Bsquare % 8;
        this.vec[KING_DIST_FROM_CENTER_B] += (Math.abs(BCol - 3.5) + Math.abs(BRow - 3.5));
    }

    public static int[] adjSquares(int square) {
        int[] adjSqu = { -9, -8, -7, -1, 1, 7, 8, 9 };
        int[] adj = new int[8];
        int numFilled = 0;
        for(int ad : adjSqu) {
            int finSqu = ad + square;
            if (finSqu > 0 && finSqu < 64) {
                adj[numFilled++] = finSqu;
            } //if
        }
        return Arrays.copyOfRange(adj, 0, numFilled);

    }

    public void setPawnStruct(Board board) {
        double totalRowsW = 0;
        double totalRowsB = 0;

        for (int i = 0; i < 64; i++) {
            byte piece = board.getSquare(i);

            if (piece == PieceTypes.WHITE_PAWN) {
                totalRowsW += (i % 8) + 1;
                if (board.getSquare(i + 1) == PieceTypes.WHITE_PAWN) {
                    this.vec[DOUB_PAWN_COUNT_W]++;
                }
                if ((i - 7) > 0 && (i - 7) < 64) {
                    if (board.getSquare(i - 7) == PieceTypes.WHITE_PAWN) {
                        this.vec[CHAINED_PAWN_COUNT_W]++;
                    }
                }
                if ((i + 9 ) > 0 && (i + 9) < 64) {
                    if (board.getSquare(i + 9) == PieceTypes.WHITE_PAWN) {
                        this.vec[CHAINED_PAWN_COUNT_W]++;
                    }
                }
            } else if (piece == PieceTypes.BLACK_PAWN) {
                totalRowsB += 8 - (i % 8);
                if (board.getSquare(i - 1) == PieceTypes.BLACK_PAWN) {
                    this.vec[DOUB_PAWN_COUNT_B]++;
                }
                if ((i - 9) > 0 && (i - 9) < 64) {
                    if (board.getSquare(i - 9) == PieceTypes.BLACK_PAWN) {
                        this.vec[CHAINED_PAWN_COUNT_B]++;
                    }
                }
                if ((i + 7) > 0 && (i + 7) < 64) {
                    if (board.getSquare(i + 7) == PieceTypes.BLACK_PAWN) {
                        this.vec[CHAINED_PAWN_COUNT_B]++;
                    }
                }
            }
        }
        this.vec[AVG_PAWN_ROW_W] = totalRowsW / (this.vec[PAWN_COUNT_W] > 0 ? this.vec[PAWN_COUNT_W] : 1);
        this.vec[AVG_PAWN_ROW_B] = totalRowsB / (this.vec[PAWN_COUNT_B] > 0 ? this.vec[PAWN_COUNT_B] : 1);
    }

    public void setControlled(Board board) {
        Move[] WMoves = board.nextMoves(PieceTypes.WHITE);
        Move[] BMoves = board.nextMoves(PieceTypes.BLACK);

        for (Move move : WMoves) {
            if (move.endingSquare == -1) {
                continue;
            }
            /* Add to center control */
            if (isCenterSquare(move.endingSquare)) {
                this.vec[CENTER_CONTROL_W]++;
                if (isCenterSquare(move.startingSquare)) {
                    this.vec[CENTER_CONTROL_W]++;
                }
            } //if

            int row = move.startingSquare % 8;

            /* Check the development. */
            if (move.piece != PieceTypes.WHITE_PAWN) {
                if (row != 0) {
                    this.vec[DEV_W]++;
                }
            } else {
                if (row != 1) {
                    this.vec[DEV_W]++;
                }
            }
            
            /* Check if its defending/attacking a piece */
            byte piece = board.getSquare(move.endingSquare);
            if (piece != PieceTypes.EMPTY) {
                if (Board.pieceColor(piece) == PieceTypes.WHITE) {
                    this.vec[PIECES_DEFENDED_W]++;
                } else {
                    this.vec[THREAT_W]++;
                }
            }
            
        }
        this.vec[NUM_LEGAL_MOVES_W] = WMoves.length;

        for (Move move : BMoves) {
            if (move.endingSquare == -1) {
                continue;
            }
            /* Add to center control */
            if (isCenterSquare(move.endingSquare)) {
                this.vec[CENTER_CONTROL_B]++;
                if (isCenterSquare(move.startingSquare)) {
                    this.vec[CENTER_CONTROL_B]++;
                }
            } //if

            int row = move.startingSquare % 8;

            /* Check the development. */
            if (move.piece != PieceTypes.BLACK_PAWN) {
                if (row != 7) {
                    this.vec[DEV_B]++;
                }
            } else {
                if (row != 6) {
                    this.vec[DEV_B]++;
                }
            }
            
            /* Check if its defending/attacking a piece */
            byte piece = board.getSquare(move.endingSquare);
            if (piece != PieceTypes.EMPTY) {
                if (Board.pieceColor(piece) == PieceTypes.BLACK) {
                    this.vec[PIECES_DEFENDED_B]++;
                } else {
                    this.vec[THREAT_B]++;
                }
            }
            
        }
        this.vec[NUM_LEGAL_MOVES_B] = BMoves.length;

    }

    public static boolean isCenterSquare(int square) {
        for (int i = 0; i < PieceMoves.CENTER_SQUARES.length; i++) {
            if (square == PieceMoves.CENTER_SQUARES[i]) {
                return true;
            } // if
        } // for
        return false;
    } // controlsCenter

}
