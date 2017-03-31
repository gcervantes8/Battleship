package edu.utep.cs4330.battleship;

import java.io.Serializable;

/**
 * Created by Gerardo C on 2/25/2017.
 */

class ComputerPlayer extends Player implements Serializable {

    /**The strategyInterface that will be used by the computer to play the game*/
    private StrategyInterface strategyInterface;

    ComputerPlayer(){
        super();
        strategyInterface = new RandomStrategy();
    }

    /**Returns a place for the computer to shoot*/
    Place pickPlace(Board opponentBoard){
        return strategyInterface.pickStrategyMove(opponentBoard);
    }

    /**Used to change what strategyInterface the computer uses to play the game
     * @param strategyName is the new strategyInterface that will be used by the computer to play the game
     * StrategyInterface should use the class's official naming of the strategyInterface*/
    void changeStrategy(String strategyName){
        if(strategyName == null){
            return;
        }
        if(strategyName.equals(SmartStrategy.strategyName)){
            strategyInterface = new SmartStrategy();
        }
        else if(strategyName.equals(RandomStrategy.strategyName)){
            strategyInterface = new RandomStrategy();
        }
    }

    /**Returns the strategyInterface being used by the computer player*/
    StrategyInterface getStrategyInterface(){
        return strategyInterface;
    }

}
