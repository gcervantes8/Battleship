package edu.utep.cs4330.battleship;


/**
 * Created by Gerardo C on 2/25/2017.
 */

interface StrategyInterface {

    /**Returns the name of the StrategyInterface, every strategy should have a name*/
    String getStrategyName();

    /**Given the board, returns the Place that the strategy should hit*/
    Place pickStrategyMove(Board board);

    /**Should be called after a computer move has been made so that the strategy knows how well it did.
     * If is not called with results after a computer move, then it might make strategy less good/accurate*/
    void afterHit(boolean shipHit, boolean shipSunk, int x, int y);

}
