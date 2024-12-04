package utils;
import java.util.Objects;

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

    public Move(String str) {
        
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move other = (Move) obj;
        return startingSquare == other.startingSquare &&
               endingSquare == other.endingSquare &&
               piece == other.piece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingSquare, endingSquare, piece, promotePiece);
    }
}
