package utils;

public class Move {
    public int startingSquare;
    public int endingSquare;
    public byte piece;
    public int moveWeight;
    public boolean promotePiece;

    public Move(int startSquare, int endSquare, byte piece) {
        this.startingSquare = startSquare;
        this.endingSquare = endSquare;
        this.piece = piece;
        this.moveWeight = 2;
        this.promotePiece = false;
    }

    public Move(int startSquare, int endSquare, byte piece, boolean promote) {
        this.startingSquare = startSquare;
        this.endingSquare = endSquare;
        this.piece = piece;
        this.moveWeight = 2;
        this.promotePiece = promote;
    }
}
