package chessEngine;

import java.io.PrintWriter;
import java.time.Duration;
import java.util.Scanner;

import utils.Board;
import utils.MCTUtils.MCT;
import utils.MCTUtils.MCTPPR;
import utils.MCTUtils.MCTRAVE;
import utils.Move;
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
        // Move g4 = new Move(49, 51, PieceTypes.WHITE_PAWN);
        // Move e5 = new Move(38, 36, PieceTypes.BLACK_PAWN);
        // Move f3 = new Move(41, 42, PieceTypes.WHITE_PAWN);
        // Move Qh4 = new Move(31, 59, PieceTypes.BLACK_QUEEN);
        // playingBoard = PieceMoves.movePiece(g4, playingBoard);
        // playingBoard = PieceMoves.movePiece(e5, playingBoard);
        // playingBoard = PieceMoves.movePiece(f3, playingBoard);
        // playingBoard = PieceMoves.movePiece(Qh4, playingBoard);
        pen.println("Enter engine selection process (RAVE/UCT)");

        input = eyes.nextLine();
        String searchType = input;
        int start;
        int end;
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
            Move nextMove = new Move(start, end, playingBoard.getSquare(start));
            playingBoard = PieceMoves.movePiece(nextMove, playingBoard);
            playingBoard.turnColor = playingBoard.oppColor();
        }
        while (!input.equals("QUIT")) {
            pen.println("----------------");
            MCTRAVE searchTreeRAVE;
            MCT searchTreeUCT;
            MCTPPR searchTreePPR;
            switch (searchType) {
                case "RAVE" -> {
                    searchTreeRAVE = new MCTRAVE(playingBoard);
                    playingBoard = searchTreeRAVE.search(duration);
                }
                case "UCT" -> {
                    searchTreeUCT = new MCT(playingBoard);
                    playingBoard = searchTreeUCT.search(duration);
                }
                default -> {
                    searchTreePPR = new MCTPPR(playingBoard);
                    playingBoard = searchTreePPR.search(duration);
                }
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
                Move castleking = new Move(39, 55, PieceTypes.BLACK_KING);
                Move castleRook = new Move(63, 47, PieceTypes.BLACK_ROOK);
                playingBoard = PieceMoves.movePiece(castleking, playingBoard);
                playingBoard = PieceMoves.movePiece(castleRook, playingBoard);
                playingBoard.printBoard(pen);
                playingBoard.turnColor = playingBoard.oppColor();
                continue;
            }
            end = UIutils.tosquareIndex(input);

            pen.println("Duration to run:");
            input = eyes.nextLine();
            duration = Duration.ofSeconds(Integer.parseInt(input));
            pen.print("\n----------------\n");
            Move nextMove = new Move(start, end, playingBoard.getSquare(start));
            playingBoard = PieceMoves.movePiece(nextMove, playingBoard);

            playingBoard.printBoard(pen);
            playingBoard.turnColor = playingBoard.oppColor();
        }
        eyes.close();
    }
}
