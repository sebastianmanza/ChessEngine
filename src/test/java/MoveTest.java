import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import utils.Move;

public class MoveTest {
    @Test
    public void testConstructor() {
        Move move = new Move(1, 2, (byte) 3);
        assertEquals(1, move.startingSquare);
        assertEquals(2, move.endingSquare);
        assertEquals(3, move.piece);
        assertEquals(2, move.moveWeight);
        assertFalse(move.promotePiece);
    }

    @Test
    public void testEqualsAndHashCode() {
        Move move1 = new Move(1, 2, (byte) 3);
        Move move2 = new Move(1, 2, (byte) 3);
        Move move3 = new Move(1, 3, (byte) 3);
        Move move4 = new Move(4, 5, (byte) 3);

        assertTrue(move1.equals(move2));
        assertFalse(move1.equals(move3));
        assertFalse(move1.equals(move4));
        assertEquals(move1.hashCode(), move2.hashCode());
        assertNotEquals(move1.hashCode(), move3.hashCode());
    }
}

