package utils;
public class PieceMoves {

    public static GameState[] pawnMoves(int square, byte color) {
        GameState[] pawnMoves = new GameState[5];
        return pawnMoves;
    }

    public static GameState[] knightMoves(int square, byte color) {
        GameState[] knightMoves = new GameState[8];
        return knightMoves;
    }

    public static GameState[] slideMoves(int square, byte color, byte pieceType) {
        GameState[] slidingMoves = new GameState[5];
        return slidingMoves;
    }

    public static GameState[] kingMoves(int square, byte color) {
        GameState[] kingMoves = new GameState[5];
        return kingMoves;
    }

}