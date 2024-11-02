package utils;
import java.io.PrintWriter;
/**
 * The underlying implementation on how the engine percieves the chess board - as a 
 * 2d-array.
 * 
 * @author Sebastian Manza
 */
public class chessBoard {

    /** An enumeration storing all the pieces of the chessboard. */
    enum piece {
        EMPTY,
        WHITE_PAWN,
        BLACK_PAWN,
        WHITE_KNIGHT,
        BLACK_KNIGHT,
        WHITE_BISHOP,
        BLACK_BISHOP,
        WHITE_ROOK,
        BLACK_ROOK,
        WHITE_QUEEN,
        BLACK_QUEEN,
        WHITE_KING,
        BLACK_KING
    } // piece

    /** The underlying array storing the current state of the board */
    piece[][] board = new piece[8][8];

    /** Sets up a new chessboard with pieces in default starting positions. [0][0] is considered the lower righthand corner
     */
    public chessBoard setNewBoard() throws Exception{
        this.setSquare("a1", piece.WHITE_ROOK);
        this.setSquare("a8", piece.BLACK_ROOK);
        this.setSquare("h1", piece.WHITE_ROOK);
        this.setSquare("h8", piece.BLACK_ROOK);

        this.setSquare("b1", piece.WHITE_KNIGHT);
        this.setSquare("b8", piece.BLACK_KNIGHT);
        this.setSquare("g1", piece.WHITE_KNIGHT);
        this.setSquare("g8", piece.BLACK_KNIGHT);

        this.setSquare("c1", piece.WHITE_BISHOP);
        this.setSquare("c8", piece.BLACK_BISHOP);
        this.setSquare("f1", piece.WHITE_BISHOP);
        this.setSquare("f8", piece.BLACK_BISHOP);

        this.setSquare("d1", piece.WHITE_QUEEN);
        this.setSquare("d8", piece.BLACK_QUEEN);

        this.setSquare("e1", piece.WHITE_KING);
        this.setSquare("e8", piece.BLACK_KING);

        for (int i = 0; i < 8; i++) {
            this.board[i][1] = piece.WHITE_PAWN;
            this.board[i][6] = piece.BLACK_PAWN;
        } // for

        for (int i = 0; i < 8; i++) {
            for (int j = 2; j < 6; j++) {
                this.board[i][j] = piece.EMPTY;
            } // for
        } // for
        return this;
    } //setNewBoard

    /** Sets a square of the board to a piece (or lack of piece)
     * @param square The square (in traditional notation)
     * @param setTo The piece to set the square to.
     * @throws Exception if square is out of bounds
     */
    chessBoard setSquare(String square, piece setTo) throws Exception{
        int[] i = convertNotation(square);
        this.board[i[0]][i[1]] = setTo;
        return this; 
    } // setSquare(square, setTo)

    /** Returns the numeric equivalent of the piece (or lack of) on the input square
     * @param square The square we want to get
     * @throws Exception if the square is out of bounds
     */
    piece getSquare(String square) throws Exception{
        int[] arr = convertNotation(square);
        return this.board[arr[0]][arr[1]];
    } // getSquare

    /** Converts tradional square notation into an array with a row and a column num
     * @param notation The original notation of the square. i.e "a1".
     * @throws Exception if the row or column are out of bounds.
     */
    public static int[] convertNotation(String notation) throws Exception{
        /* Create a new array to store the notation */
        int[] arr = new int[2];
        /* Set the rows and columns */
        int row = ((int) notation.toLowerCase().charAt(0)) - 97;
        int col = ((int) notation.charAt(1) - 49);
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new Exception("Attempted notation is out of bounds");
        } //if
        arr[0] = row;
        arr[1] = col;
        return arr;
    } // convertNotation(notation)


    /** Converts array notation into traditional notation
     * @param arr The array describing a row and col.
     * @throws Exception if the row or column are out of bounds.
     */
    public static String convertNotation(int[] arr) throws Exception{
        StringBuilder str = new StringBuilder();
        return str.toString(); //STUB
    } 

    public void printBoard(){
        PrintWriter pen = new PrintWriter(System.out, true);
        for (int j = 7; j >= 0; j--) {
            pen.println("");
            pen.println("");
            for (int i = 0; i < 8; i++) {
                piece p = this.board[i][j];
                String printable = "";
                switch (p) {
                    case EMPTY:
                    printable = "--";
                        break;
                    case WHITE_PAWN:
                    printable = "wp";
                        break;
                    case WHITE_KNIGHT:
                    printable = "wk";
                        break;
                    case WHITE_BISHOP:
                    printable = "wb";
                        break;
                    case WHITE_ROOK:
                    printable = "wr";
                        break;
                    case WHITE_QUEEN:
                    printable = "wq";
                        break;
                    case WHITE_KING:
                    printable = "wK";
                        break;
                    case BLACK_BISHOP:
                    printable = "bb";
                        break;
                    case BLACK_KING:
                    printable = "bK";
                        break;
                    case BLACK_KNIGHT:
                    printable = "bk";
                        break;
                    case BLACK_PAWN:
                    printable = "bp";
                        break;
                    case BLACK_QUEEN:
                    printable = "bq";
                        break;
                    case BLACK_ROOK:
                    printable = "br";                       
                        break;
                    default:
                        throw new AssertionError("Something went wrong");
                } //switch
                pen.print(printable + "  ");
            } // for
            
        } // for
        pen.close();
    } // printBoard()


}