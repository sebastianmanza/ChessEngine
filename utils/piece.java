package utils;
import com.sun.nio.sctp.PeerAddressChangeNotification;
import java.util.List;
import utils.MCTUtils.move;

/** A generic class piece */
public class piece {
    enum color {
        BLACK,
        WHITE
    }
    color pieceColor;
    List <move> possibleMoves;
    
}

class pawn extends piece {
    boolean onStartingSquare;
    public List<move> possibleMoves() {
        if (this.onStartingSquare) {
            possibleMoves.add("move two forward placeholder");
        } //if


        return possibleMoves;
    }


}
