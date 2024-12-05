package utils;

public class UIutils {
    public static int tosquareIndex(String input) {
        int squareIndex = 0;
        squareIndex += ((input.charAt(0) - (int) 'a') * 8);
        squareIndex += ((Character.getNumericValue(input.charAt(1))) - 1);
        return squareIndex;
    }

    public static String toNotation(Move move) {
        int col;
        int row;
        col = move.startingSquare / 8;
        row = move.startingSquare % 8;
        StringBuilder str = new StringBuilder();
        str.append((char) (col + (int) 'a'));
        str.append(Integer.toString(row + 1));
        str.append("-");

        col = move.endingSquare / 8;
        row = move.endingSquare % 8;
        str.append((char) (col + (int) 'a'));
        str.append(Integer.toString(row + 1));

        return str.toString();
    }

    // public static String toClassicNotation(Move move) {
    //     int col;
    //     int row;

    //     col = move.endingSquare / 8;
    //     row = move.endingSquare % 8;
    //     String piece = "";
    //     switch(move.piece) {
    //         case PieceTypes.WHITE_KNIGHT, PieceTypes.BLACK_KNIGHT -> piece = "N";
    //         case PieceTypes.WHITE_ROOK, PieceTypes.BLACK_ROOK -> piece = "R";
    //         case PieceTypes.WHITE_BISHOP, PieceTypes.BLACK_BISHOP -> piece = "B";
    //         case PieceTypes.BLACK_QUEEN, PieceTypes.WHITE_QUEEN -> piece = "Q";
    //         case PieceTypes.WHITE_KING, PieceTypes.BLACK_KING -> piece = "K";
    //         case default -> 
    //     }
    //     StringBuilder str = new StringBuilder();
    //     str.append(piece);
    //     str.append((char) (col + (int) 'a'));
    //     str.append(Integer.toString(row + 1));

    //     return str.toString();
    // }
}
