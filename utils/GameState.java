package utils;

/**
 * Stores the current game state.
 *
 * @author Sebastian Manza
 */
public interface GameState {

    /**
     * Sets a piece on a square.
     *
     * @param square The square (0-63 for a1-h8 [a1, a2...h7, h8])
     * @param piece The type of piece (black pawn, white knight...)
     */
    void setSquare(int square, byte piece);

    /**
     * Gets the piece on a square.
     *
     * @param square The square index (0-63)
     * @return The byte representation of what is on the square.
     */
    byte getSquare(int square);

    /**
     * Gets the byte array representing the board.
     *
     * @return the representation of the board.
     */
    byte[][] getBoard();

    /**
     * Removes all pieces on the board.
     */
    void clearBoard();

    /**
     * Creates a board at standard chess starting position.
     */
    void startingPos();

    /**
     * Prints the board.
     *
     * @throws Exception when piece type does not exist.
     */
    void printBoard() throws Exception;

} //GameState
