package utils;

public class setBoards {
  
  public static Board setBoardW2M1582108() {
    Board board = new Board(PieceTypes.WHITE, PieceTypes.WHITE);
    board.startingPos();
    board.setSquare(0, PieceTypes.EMPTY);
    board.setSquare(8, PieceTypes.EMPTY);
    board.setSquare(10, PieceTypes.WHITE_BISHOP);
    board.setSquare(16, PieceTypes.EMPTY);
    board.setSquare(17, PieceTypes.EMPTY);
    board.setSquare(0, PieceTypes.WHITE_PAWN);
    board.setSquare(22, PieceTypes.BLACK_QUEEN);
    board.setSquare(24, PieceTypes.WHITE_ROOK);
    board.setSquare(25, PieceTypes.EMPTY);
    board.setSquare(30, PieceTypes.EMPTY);
    board.setSquare(31, PieceTypes.EMPTY);
    board.setSquare(33, PieceTypes.WHITE_KNIGHT);
    board.setSquare(36, PieceTypes.BLACK_ROOK);
    board.setSquare(38, PieceTypes.EMPTY);
    board.setSquare(39, PieceTypes.EMPTY);
    board.setSquare(40, PieceTypes.EMPTY);
    board.setSquare(42, PieceTypes.WHITE_QUEEN);
    board.setSquare(47, PieceTypes.EMPTY);
    board.setSquare(48, PieceTypes.EMPTY);
    board.setSquare(55, PieceTypes.BLACK_KING);
    board.setSquare(51, PieceTypes.BLACK_KNIGHT);
    board.setSquare(63, PieceTypes.EMPTY);

    return board;
  }

  public static Board setBoardW2M1869044() {
    Board board = new Board(PieceTypes.WHITE, PieceTypes.WHITE);
    board.setSquare(2, PieceTypes.WHITE_BISHOP);
    board.setSquare(8, PieceTypes.WHITE_ROOK);
    board.setSquare(12, PieceTypes.WHITE_QUEEN);
    board.setSquare(13, PieceTypes.BLACK_BISHOP);
    board.setSquare(14, PieceTypes.WHITE_PAWN);
    board.setSquare(19, PieceTypes.WHITE_PAWN);
    board.setSquare(20, PieceTypes.BLACK_PAWN);

  }
}
