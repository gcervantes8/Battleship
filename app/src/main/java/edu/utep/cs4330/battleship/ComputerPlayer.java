package edu.utep.cs.cs4330.battleship;

/**
 * Created by Gerardo C on 2/25/2017.
 */

public class ComputerPlayer extends Player {

    Strategy strategy;

    public ComputerPlayer(){
        super();
        strategy = new RandomStrategy();
    }

    public Place pickPlace(Board opponentBoard){
        return strategy.pickStrategyMove(opponentBoard);
    }

}
