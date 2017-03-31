package edu.utep.cs4330.battleship;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Gerardo C on 2/25/2017.
 */

class RandomStrategy implements StrategyInterface, Serializable{


    static final String strategyName = "RANDOM STRATEGY";

    /**Returns the name of the strategy, which will be used to change strategy or to identify it*/
    public String getStrategyName(){
        return strategyName;
    }

    /**Picks a place from the board that the strategy should hit*/
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

    /**Should be called after a computer move has been made so that the strategy knows how well it did.
     * If is not called with results after a computer move, then it might make strategy less good/accurate*/
    public void afterHit(boolean shipHit, boolean shipSunk, int x, int y){
        //Random strategy ignores information on whether place was hit or not
    }
}
