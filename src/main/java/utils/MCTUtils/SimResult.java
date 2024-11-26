package utils.MCTUtils;
import java.util.HashSet;

import utils.Move;

public class SimResult {
    HashSet<Move> gameSim;
    double winPoints;

    public SimResult(HashSet<Move> gameSim, double winPoints) {
        this.gameSim = gameSim;
        this.winPoints = winPoints;
    }

}
