package utils.ANNUtils;

import java.util.Arrays;
import java.util.Random;

import utils.Board;
import utils.Move;
import utils.PieceMoves;
import utils.PieceTypes;

public class ANNTrainer {
    int numSamples;
    int numToTrain;
    SimpleANN ann;

    public ANNTrainer (SimpleANN ann, int numSamples, int numToTrain) {
        this.ann = ann;
        this.numSamples = numSamples;
        this.numToTrain = numToTrain;
    }

    public boardResult[] simRanBoard() {
        boardResult[] res = new boardResult[401];
        int resNum = 0;
        Random rand = new Random();
        Board board = new Board(PieceTypes.WHITE, (byte) rand.nextInt(1));
        board.startingPos();
        int depth = 0;
        int depthThresh = 400;

        while (true) {
            Move nextMove = board.ranNextMove(rand);

            /*
             * If the game is checkmate/stalemate or if the depth is too far, end the game
             */
            if (nextMove == null || depth++ > depthThresh) {
                double vicPoints = board.vicPoints();
                for (int i = 0; i < resNum; i++) {
                    res[i].vicPoints = vicPoints;
                }
                return Arrays.copyOfRange(res, 0, resNum);
            } // if
            board = PieceMoves.movePiece(nextMove, board);
            board.turnColor = board.oppColor();
            res[resNum] = new boardResult();
            res[resNum].board = board;
            resNum++;
        } // while
    } // simulate(MCTNode)

    public double[] getOutcome(double vicPoints) {
        if (vicPoints == 1.0) {
            return new double[] {1, 0, 0};
        } else if (vicPoints == 0.5) {
            return new double[] {0, 1, 0};
        } else return new double[] {0, 0, 1};
    }


    public SimpleANN trainANN() {
        double[][] inputs = new double[numSamples][834];
        double[][] outputs = new double[numSamples][1];

        int filled = 0;
        while(filled < numSamples) {
            boardResult[] results = simRanBoard();

            for (boardResult res : results) {
                Board board = res.board;
                inputs[filled] = ANNVector.createVector(board);
                outputs[filled] = new double[]{ res.vicPoints };
                filled++;
                this.ann.fit(inputs, outputs, numToTrain);
                if (filled >= numSamples) {
                    break;
                }
            }
        }


        return this.ann;
    }
}

