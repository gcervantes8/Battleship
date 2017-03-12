package edu.utep.cs.cs4330.battleship;

import java.util.Random;

/**
 * Created by Gerardo C on 2/25/2017.
 */

public class RandomStrategy implements Strategy{

    public Place pickStrategyMove(Board board) {
        if (board == null || board.isAllHit()) {
            return null;
        }
        Random rng = new Random();
        int boardSize = board.size();

        Place toHit = null;

        while (toHit == null || toHit.isHit()){

            toHit = board.placeAt(rng.nextInt(boardSize), rng.nextInt(boardSize));
        }

        return toHit;
    }
}
