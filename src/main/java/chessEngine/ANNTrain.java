package chessEngine;
import java.io.IOException;
import java.util.Arrays;

import utils.ANNUtils.ANNTrainer;
import utils.ANNUtils.ANNVector;
import utils.ANNUtils.SimpleANN;
import utils.Board;
import utils.PieceTypes;
import utils.setBoards;

public class ANNTrain {
    public static void main(String[] args) {
        // Configuration
        int inputSize = 834;  // Example input size (board vector length)
        int hiddenSize = 418; // Number of hidden neurons
        int outputSize = 1;   // Number of output neurons (win, draw, loss)
        int numSamples = 10000; // Number of training samples
        int numToTrain = 1000; // Number of training iterations

        // Initialize the FastANN
       // SimpleANN SimpleANN = new SimpleANN(inputSize, hiddenSize, outputSize);
        SimpleANN loadedANN = new SimpleANN(inputSize, hiddenSize, outputSize);

        try {
            loadedANN.loadModel("chess_SimpleANN_Model.dat", inputSize, hiddenSize, outputSize);
            System.out.println("Model loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Initialize the FastANNTrainer
        ANNTrainer trainer = new ANNTrainer(loadedANN, numSamples, numToTrain);

        // Train the FastANN
        System.out.println("Training the ANN...");
        trainer.trainANN();
        System.out.println("\nTraining complete.");

        // Save the trained model
        try {
            loadedANN.saveModel("chess_SimpleANN_Model.dat");
            System.out.println("Model saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Test the FastANN with a position
        System.out.println("Testing the SimpleANN...");

        Board board = setBoards.setBoardW2M1582108();
        Board losingBoard = setBoards.setBoardFoolsMate();
        Board startingBoard = new Board(PieceTypes.WHITE, PieceTypes.WHITE);
        startingBoard.startingPos();
        
        double[] prediction = loadedANN.predict(ANNVector.createVector(board));

        double[] startingPrediction = loadedANN.predict(ANNVector.createVector(startingBoard));

        double[] losingPrediction = loadedANN.predict(ANNVector.createVector(losingBoard));
        
        System.out.println("Prediction: " + Arrays.toString(prediction));
        System.out.println("Prediction: " + Arrays.toString(startingPrediction));
        System.out.println("Prediction" + Arrays.toString(losingPrediction));

    }
}
