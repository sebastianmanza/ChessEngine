package utils.MCTUtils;
import java.util.HashSet;

import utils.Board;

public class SimResult {
    HashSet<Board> gameSim;
    double winPoints;

    public SimResult(HashSet<Board> gameSim, double winPoints) {
        this.gameSim = gameSim;
        this.winPoints = winPoints;
    }

}
