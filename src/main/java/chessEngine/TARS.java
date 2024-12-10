package chessEngine;

import java.util.Arrays;
import java.util.Random;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import utils.ANNUtils.BetterVector;
import utils.ANNUtils.boardResult;
import utils.Board;
import utils.Move;
import utils.PieceMoves;
import utils.PieceTypes;
import utils.setBoards;

public class TARS {

    public static boardResult[] simRanBoard() {
        boardResult[] res = new boardResult[256];
        int resNum = 0;
        Random rand = new Random();
        Board board = new Board(PieceTypes.WHITE, (byte) rand.nextInt(1));
        board.startingPos();
        int depth = 0;
        int depthThresh = 255;

        while (true) {
            Move nextMove = board.ranNextMove(rand);

            /*
             * If the game is checkmate/stalemate or if the depth is too far, end the game
             */
            if (nextMove == null || depth++ > depthThresh) {
                double vicPoints = board.vicPoints();
                if (board.engineColor == PieceTypes.BLACK) {
                    vicPoints = 1 - vicPoints;
                }
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

    public static DataSet trainANNDataSet(int numSamples) {
        double[][] testInputs = new double[numSamples][37];
        double[][] testOutputs = new double[numSamples][1];

        int filled = 0;
        while (filled < numSamples) {
            boardResult[] results = simRanBoard();

            for (boardResult res : results) {
                Board board = res.board;
                testInputs[filled] = new BetterVector().createVector(board);
                testOutputs[filled] = new double[] { res.vicPoints };
                filled++;
                if (filled >= numSamples) {
                    break;
                }
            }
        }

        INDArray inputs = Nd4j.create(testInputs);
        INDArray expOutputs = Nd4j.create(testOutputs);

        DataSet data = new DataSet(inputs, expOutputs);
        return data;
    }

    public static void main(String[] args) {
        int epochs = 100;
        int numTrainingData = 131072;

        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(0.01))
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(new DenseLayer.Builder().nIn(37).nOut(18).activation(Activation.RELU).build())
                // .layer(new
                // DenseLayer.Builder().nIn(37).nOut(37).activation(Activation.RELU).build())
                .layer(new OutputLayer.Builder().nIn(18).nOut(1).activation(Activation.IDENTITY)
                        .lossFunction(LossFunctions.LossFunction.MSE).build())
                .build();

        MultiLayerNetwork ANN = new MultiLayerNetwork(config);
        ANN.init();


        /* A small training set. */
        double[][] testInputs = new double[3][37];
        double[][] testOutputs = new double[3][37];

        Board board = setBoards.setBoardW2M1582108();
        Board losingBoard = setBoards.setBoardFoolsMate();
        Board startingBoard = new Board(PieceTypes.WHITE, PieceTypes.WHITE);
        startingBoard.startingPos();

        double[] inputArray = new BetterVector().createVector(losingBoard);
        double[] inputArraystart = new BetterVector().createVector(startingBoard);
        double[] inputArrayPuz = new BetterVector().createVector(board);

        testInputs[0] = inputArray;
        testOutputs[0] = new double[] { 0.0 };
        testInputs[1] = inputArraystart;
        testOutputs[1] = new double[] {0.5};
        testInputs[2] = inputArrayPuz;
        testOutputs[2] = new double[] {1.0};

        INDArray inputs = Nd4j.create(testInputs);
        INDArray expOutputs = Nd4j.create(testOutputs);

        DataSet data = new DataSet(inputs, expOutputs);

        for (int i = 0; i < epochs; i++) {
            ANN.fit(data);
        }

        // for (int i = 0; i < epochs; i++) {
        //     DataSetIterator iterator = new ListDataSetIterator<>(trainANNDataSet(numTrainingData).asList(), 64);

        //     while (iterator.hasNext()) {
        //         DataSet batch = iterator.next();
        //         ANN.fit(batch);
        //     }
        //     System.out.printf("\r%.1f%%", i / (double) epochs * 100);
        // }

        System.out.println(Arrays.toString(inputArray));

        INDArray reshapedInput = Nd4j.create(inputArray).reshape(1, inputArray.length);
        INDArray reshapedInputstart = Nd4j.create(inputArraystart).reshape(1, inputArraystart.length);
        INDArray reshapedInputpuz = Nd4j.create(inputArrayPuz).reshape(1, inputArrayPuz.length);
        INDArray pred = ANN.output(reshapedInput);
        INDArray predstart = ANN.output(reshapedInputstart);
        INDArray predpuz = ANN.output(reshapedInputpuz);

        System.out.println("\nPrediction: " + pred);
        System.out.println("Prediction start: " + predstart);
        System.out.println("Prediction puzzle: " + predpuz);
    }

}
