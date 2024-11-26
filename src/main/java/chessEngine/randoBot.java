// package chessEngine;
// import java.io.PrintWriter;
// import java.util.Random;
// import java.util.Scanner;

// import utils.Board;
// import utils.PieceMoves;
// import utils.PieceTypes;
// import utils.UIutils;

// public class randoBot {
//     public static void main(String[] args) throws Exception{
//         Random rand = new Random();
//         Scanner eyes = new Scanner(System.in);
//         PrintWriter pen = new PrintWriter(System.out, true);
//         String input = "";

//         Board playingBoard = new Board(PieceTypes.WHITE, PieceTypes.WHITE);
//         playingBoard.startingPos();

        
//         int start;
//         int end;

//         playingBoard.printBoard(pen);
//         pen.println("");
//         while (!input.equals("QUIT")){
//             pen.println("----------------");
//             Board[] nextMoves = playingBoard.nextMoves();
//             playingBoard = nextMoves[rand.nextInt(nextMoves.length)];
//             playingBoard.printBoard(pen);
//             pen.println("Starting square:");
//             input = eyes.nextLine();
//             start = UIutils.tosquareIndex(input);
//             pen.println("\nEnding Square:");
//             input = eyes.nextLine();
//             end = UIutils.tosquareIndex(input);
//             pen.print("\n----------------\n");
//             playingBoard = PieceMoves.movePiece(start, end, playingBoard);
//             playingBoard.printBoard(pen);
//             playingBoard.turnColor = playingBoard.oppColor();
//         } 
//         eyes.close();

//     }
// }
