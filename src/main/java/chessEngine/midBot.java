package chessEngine;

import java.io.PrintWriter;
import java.time.Duration;
import java.util.Scanner;

import utils.Board;
import utils.MCTUtils.MCT;
import utils.MCTUtils.MCTRAVE;
import utils.PieceMoves;
import utils.PieceTypes;
import utils.UIutils;

public class midBot {
    public static void main(String[] args) throws Exception {
        Scanner eyes = new Scanner(System.in);
        PrintWriter pen = new PrintWriter(System.out, true);
        String input;
        byte engineColor;

        /* Prompt the user for engine color */
        pen.println("Enter engine color (W/B): ");
        input = eyes.nextLine();
        if (input.equals("W")) {
            engineColor = PieceTypes.WHITE;
        } else if (input.equals("B")) {
            engineColor = PieceTypes.BLACK;
        } else {
            pen.println("Next time please enter W or B. Engine automatically assigned to white.");
            engineColor = PieceTypes.WHITE;
        } // if/else

        Board playingBoard = new Board(PieceTypes.WHITE, engineColor);
        playingBoard.startingPos();
        pen.println("Enter engine Heuristic (RAVE/UCT)");

        input = eyes.nextLine();
        String searchType = input;
        int start;
        int end;
        boolean promotePiece = false;
        pen.println("Enter starting duration:");
        input = eyes.nextLine();
        Duration duration = Duration.ofSeconds(Integer.parseInt(input));
        playingBoard.printBoard(pen);
        pen.println("");
        input = "";
        if (playingBoard.engineColor == PieceTypes.BLACK) {
            playingBoard.printBoard(pen);
            pen.println("Starting square:");
            input = eyes.nextLine();
            start = UIutils.tosquareIndex(input);
            pen.println("\nEnding Square:");
            input = eyes.nextLine();
            end = UIutils.tosquareIndex(input);
            playingBoard = PieceMoves.movePiece(start, end, playingBoard);
            playingBoard.turnColor = playingBoard.oppColor();
        }
        while (!input.equals("QUIT")) {
            pen.println("----------------");
            MCTRAVE searchTreeRAVE = null;
            MCT searchTreeUCT = null;
            if (searchType.equals("RAVE")) {
                searchTreeRAVE = new MCTRAVE(playingBoard);
                playingBoard = searchTreeRAVE.search(duration);
            } else {
                searchTreeUCT = new MCT(playingBoard);
                playingBoard = searchTreeUCT.search(duration);
            }

            if (playingBoard == null) {
                pen.println("Game Over.");
                break;
            }

            playingBoard.printBoard(pen);
            pen.println("Starting square:");
            input = eyes.nextLine();
            start = UIutils.tosquareIndex(input);
            pen.println("\nEnding Square:");
            input = eyes.nextLine();
            if (input.equals("00")) {
                pen.println("Duration to run:");
                input = eyes.nextLine();
                duration = Duration.ofSeconds(Integer.parseInt(input));
                pen.print("\n----------------\n");
                playingBoard = PieceMoves.movePiece(32, 48, playingBoard);
                playingBoard = PieceMoves.movePiece(56, 40, playingBoard);
                playingBoard.printBoard(pen);
                playingBoard.turnColor = playingBoard.oppColor();
                continue;
            }
            end = UIutils.tosquareIndex(input);
            if (input.length() > 2) {
                promotePiece = true;
            } // if

            pen.println("Duration to run:");
            input = eyes.nextLine();
            duration = Duration.ofSeconds(Integer.parseInt(input));
            pen.print("\n----------------\n");
            playingBoard = PieceMoves.movePiece(start, end, playingBoard);
            if (promotePiece ) {
                playingBoard.setSquare(end, PieceTypes.BLACK_QUEEN);
                promotePiece = false;
            } // if
            playingBoard.printBoard(pen);
            playingBoard.turnColor = playingBoard.oppColor();
        }
        eyes.close();
    }
}
