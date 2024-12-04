import static org.junit.Assert.assertEquals;
import org.junit.Test;

import utils.Board;
import utils.MCTUtils.MCT;
import utils.MCTUtils.MCTNode;
import utils.PieceTypes;

public class BackPropTest {

    @Test
    public void testBackPropagateBasic() {
        MCTNode node = new MCTNode(new Board(PieceTypes.WHITE, PieceTypes.WHITE), null);

        // Simulated lengths
        int[] lengths = {250, 260, 240};
        double[] expectedMean = {250.0, 255.0, 250.0};  // Expected means after each update
        double[] expectedVariance = {0.0, 25.0, 66.67}; // Expected variances (manually calculated)

        for (int i = 0; i < lengths.length; i++) {
            MCT.backPropagate(node, 1.0, lengths[i]);

            // Validate mean
            assertEquals(expectedMean[i], node.avgLength.get(), 0.01);

            // Validate variance
            assertEquals(expectedVariance[i], node.lengthstdDev.get(), 0.01);
        }

        // Validate standard deviation
        assertEquals(Math.sqrt(expectedVariance[2]), Math.sqrt(node.lengthstdDev.get()), 0.01);
    }

    @Test
    public void testBackPropagateExtremeLengths() {
        MCTNode node = new MCTNode(new Board(PieceTypes.WHITE, PieceTypes.WHITE), null);

        // Simulated extreme lengths
        int[] lengths = {10, 5000, 300};
        double[] expectedMean = {10.0, 2505.0, 1770.0};
        double[] expectedVariance = {0.0, 6005000.0, 2166800.0}; // Example variance values

        for (int i = 0; i < lengths.length; i++) {
            MCT.backPropagate(node, 1.0, lengths[i]);

            // Validate mean
            assertEquals(expectedMean[i], node.avgLength.get(), 1.0);

            // Validate variance
            assertEquals(expectedVariance[i], node.lengthstdDev.get(), 1.0);
        }

        // Validate standard deviation
        assertEquals(Math.sqrt(expectedVariance[2]), Math.sqrt(node.lengthstdDev.get()), 1.0);
    }

    @Test
    public void testBackPropagateMultipleUpdates() {
        MCTNode node = new MCTNode(new Board(PieceTypes.WHITE, PieceTypes.WHITE), null);

        // Generate a sequence of lengths
        int numUpdates = 100;
        double sum = 0.0, sumSq = 0.0;

        for (int i = 1; i <= numUpdates; i++) {
            int length = 200 + (i % 50); // Lengths between 200 and 249
            sum += length;
            sumSq += length * length;

            MCT.backPropagate(node, 1.0, length);

            double mean = sum / i;
            double variance = (sumSq / i) - (mean * mean);

            // Validate mean
            assertEquals(mean, node.avgLength.get(), 0.01);

            // Validate variance
            assertEquals(variance, node.lengthstdDev.get(), 0.01);

            // Validate standard deviation
            assertEquals(Math.sqrt(variance), Math.sqrt(node.lengthstdDev.get()), 0.01);
        }
    }

    @Test
    public void testBackPropagateSingleSimulation() {
        MCTNode node = new MCTNode(new Board(PieceTypes.WHITE, PieceTypes.WHITE), null);

        // Simulate one update
        int length = 250;
        MCT.backPropagate(node, 1.0, length);

        // Validate mean
        assertEquals(250.0, node.avgLength.get(), 0.01);

        // Validate variance
        assertEquals(0.0, node.lengthstdDev.get(), 0.01);

        // Validate standard deviation
        assertEquals(0.0, Math.sqrt(node.lengthstdDev.get()), 0.01);
    }
}

