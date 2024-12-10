package utils.ANNUtils;

import utils.Board;
import utils.PieceTypes;

public class ANNVector {
    

    public static double[] createVector(Board board) {
        /* We have 13 possible pieces and 64 squares + xtra data */
        int inputSize = 834;
        int index = 0;

        double[] vector = new double[inputSize];

        for (int i = 0; i < 64; i++) {
            byte piece = board.getSquare(i);
            for (int j = 0; j < 7; j++) {
                vector[index++] = (piece == j) ? 1.0 : 0.0;
            } //for
            for (int j = 9; j < 15; j++) {
                vector[index++] = (piece == j) ? 1.0 : 0.0;
            } //for
        } //for

        vector[index++] = (board.engineColor == PieceTypes.WHITE) ? 0.0 : 1.0;
        vector[index++] = (board.turnColor == PieceTypes.WHITE) ? 0.0 : 1.0;

        return vector;

        


    }
}
