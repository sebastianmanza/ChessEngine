package chessEngine;

import java.io.PrintWriter;
import java.time.Duration;
import java.util.Scanner;

import utils.Board;
import utils.MCTUtils.MCT;
import utils.PieceMoves;
import utils.PieceTypes;
import utils.UIutils;

public class badBot {
      public static void main(String[] args) throws Exception{
        Scanner eyes = new Scanner(System.in);
        PrintWriter pen = new PrintWriter(System.out, true);
        String input = "";

        Board playingBoard = new Board(PieceTypes.WHITE, PieceTypes.WHITE);
        playingBoard.startingPos();

        
        int start;
        int end;
        boolean promotePiece = false;
        Duration duration = Duration.ofSeconds(30);

        playingBoard.printBoard(pen);
        pen.println("");
        while (!input.equals("QUIT")){
            pen.println("----------------");
            MCT searchTree = new MCT(playingBoard);
            playingBoard = searchTree.search(duration);

            playingBoard.printBoard(pen);
            pen.println("Starting square:");
            input = eyes.nextLine();
            start = UIutils.tosquareIndex(input);
            pen.println("\nEnding Square:");
            input = eyes.nextLine();
            end = UIutils.tosquareIndex(input);
            if (input.length() > 2) {
                promotePiece = true;
            }

            pen.println("Duration to run:");
            input = eyes.nextLine();
            duration = Duration.ofSeconds(Integer.parseInt(input));


            pen.print("\n----------------\n");
            playingBoard = PieceMoves.movePiece(start, end, playingBoard);
            if (promotePiece) {
                playingBoard.setSquare(end, PieceTypes.BLACK_QUEEN);
            }
            playingBoard.printBoard(pen);
            playingBoard.turnColor = playingBoard.oppColor();
        } 
        eyes.close();
}
}
