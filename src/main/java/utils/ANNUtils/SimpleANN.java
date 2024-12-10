package utils.ANNUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.ejml.simple.SimpleMatrix;

public class SimpleANN {
    SimpleMatrix weightsInputHidden;
    SimpleMatrix weightsHiddenOutput;
    SimpleMatrix biasHidden;
    SimpleMatrix biasOutput;

    double learningRate = 0.001;

    public SimpleANN(int input, int hidden, int output) {
        this.weightsInputHidden = SimpleMatrix.random_DDRM(hidden, input, -Math.sqrt(6 / (input + output)),
                Math.sqrt(6 / (input + output)), new java.util.Random());
        this.weightsHiddenOutput = SimpleMatrix.random_DDRM(output, hidden, -Math.sqrt(6 / (input + output)),
        Math.sqrt(6 / (input + output)), new java.util.Random());
        this.biasHidden = new SimpleMatrix(hidden, 1);
        this.biasOutput = new SimpleMatrix(output, 1);
    }

    public double[] predict(double[] vector) {
        /* Create the FastMatrix and pass it through the hidden layer */
        SimpleMatrix input = new SimpleMatrix(vector.length, 1, true, vector);
        SimpleMatrix hidden = this.weightsInputHidden.mult(input);
        hidden = hidden.plus(biasHidden);
        hidden = sigmoid(hidden);

        /* Pass it through */
        SimpleMatrix output = this.weightsHiddenOutput.mult(hidden);
        output = output.plus(biasOutput);
        output = sigmoid(output);

        return output.getDDRM().getData();
    }

    public void train(double[] vectorInput, double[] vectorTarget) {
        SimpleMatrix input = new SimpleMatrix(vectorInput.length, 1, true, vectorInput);

        // Forward pass
        SimpleMatrix hidden = this.weightsInputHidden.mult(input);
        hidden = hidden.plus(this.biasHidden);
        hidden = sigmoid(hidden);

        SimpleMatrix output = this.weightsHiddenOutput.mult(hidden);
        output = output.plus(this.biasOutput);
        output = sigmoid(output);

        // Calculate error
        SimpleMatrix target = new SimpleMatrix(vectorTarget.length, 1, true, vectorTarget);
        SimpleMatrix error = output.minus(target);

        // Backpropagation
        SimpleMatrix gradient = derivativeSigmoid(output).elementMult(error).scale(this.learningRate);
        SimpleMatrix weightsHODelta = gradient.mult(hidden.transpose());
        weightsHiddenOutput = weightsHiddenOutput.plus(weightsHODelta);
        biasOutput = biasOutput.plus(gradient);

        SimpleMatrix hiddenErrors = weightsHiddenOutput.transpose().mult(error);
        SimpleMatrix hiddenGradient = derivativeSigmoid(hidden).elementMult(hiddenErrors).scale(this.learningRate);
        SimpleMatrix weightsIHDelta = hiddenGradient.mult(input.transpose());
        weightsInputHidden = weightsInputHidden.plus(weightsIHDelta);
        biasHidden = biasHidden.plus(hiddenGradient);

        System.out.println("Weights Hidden-Output: " + weightsHiddenOutput.get(0, 0) + ", " + weightsHiddenOutput.get(0, 1));
    }

    public void fit(double[][] inputs, double[][] expected, int numIterations) {
        for (int i = 0; i < numIterations; i++) {
            int sampleN = (int) (Math.random() * inputs.length);
            this.train(inputs[sampleN], expected[sampleN]);
            // int printer = numIterations / 10000;
            // if (i % printer == 0) {
            //     System.out.printf("\rTraining %.2f%% complete", ((float) i / (float) numIterations) * 100);

            // }
        }
    }

    public void saveModel(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this.weightsInputHidden.getDDRM().getData());
            oos.writeObject(this.weightsHiddenOutput.getDDRM().getData());
            oos.writeObject(this.biasHidden.getDDRM().getData());
            oos.writeObject(this.biasOutput.getDDRM().getData());
        }
    }

    public void loadModel(String filename, int input, int hidden, int output)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            this.weightsInputHidden = new SimpleMatrix(hidden, input, true, (double[]) ois.readObject());
            this.weightsHiddenOutput = new SimpleMatrix(output, hidden, true, (double[]) ois.readObject());
            this.biasHidden = new SimpleMatrix(hidden, 1, true, (double[]) ois.readObject());
            this.biasOutput = new SimpleMatrix(output, 1, true, (double[]) ois.readObject());
        }
    }

    /**
     * Applies the derivative of the sigmoid function, necessary for backProp.
     * 
     * @return
     */
    private static SimpleMatrix derivativeSigmoid(SimpleMatrix M) {
        SimpleMatrix temp = new SimpleMatrix(M);
        for (int i = 0; i < temp.numRows(); i++) {
            for (int j = 0; j < temp.numCols(); j++) {
                double data = M.get(i, j);
                temp.set(i, j, data * (1 - data));
            } // for
        } // for
        return temp;
    } // derivativeSigmoid

    /**
     * Applies the sigmoid function to the values of the matrix.
     */
    private static SimpleMatrix sigmoid(SimpleMatrix M) {
        SimpleMatrix temp = new SimpleMatrix(M);
        for (int i = 0; i < M.numRows(); i++) {
            for (int j = 0; j < M.numCols(); j++) {
                double data = M.get(i, j);
                temp.set(i, j, 1 / (1 + Math.exp(-data)));
            } // for
        }
        return temp;
    }

    private static double[] softmax(double[] logits) {
        double sum = 0.0;
        double[] expValues = new double[logits.length];

        // Compute exponentials and their sum
        for (int i = 0; i < logits.length; i++) {
            expValues[i] = Math.exp(logits[i]);
            sum += expValues[i];
        }

        // Normalize to get probabilities
        for (int i = 0; i < logits.length; i++) {
            expValues[i] /= sum;
        }

        return expValues;
    }

    public double computeCrossEntropyLoss(double[] predicted, double[] target) {
        double loss = 0.0;
        for (int i = 0; i < predicted.length; i++) {
            loss -= target[i] * Math.log(predicted[i] + 1e-15); // Add small value to avoid log(0)
        }
        return loss;
    }
}
