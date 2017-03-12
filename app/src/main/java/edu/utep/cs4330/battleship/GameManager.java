package edu.utep.cs.cs4330.battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Gerardo C on 2/6/2017.
 */

public class GameManager {


    /**Player whose turn it is*/
    private Player activePlayer;

    /**The player who plays the game*/
    private Player player;

    /**The opponent, could be other human player or npc*/
    private ComputerPlayer opponent;

    /**The highscore for the game, will be a value between 0 and 100 inclusive*/
    private double highscore = 0;

    /**Creates a new game, with players and boards.*/
    public GameManager(){
        player = new Player();
        opponent = new ComputerPlayer();
        activePlayer = player;
    }

    /**Retrieves from given player's board how many ships they have sunk
     * @param player is player's ships you want to check amount of sunk ship*/
    public int getShipsSunkCount(Player player){
        return player.shipsSunk();
    }

    /**Returns amount of shots that have hit player's ships
     * @param player is the player's ships you want to check*/
    public int getShipShots(Player player){
        return player.getShipHitPlaces().size();
    }

    /**Only sets highscore if new score is greater than previous highscore
     * @param newScore is the score you want to replace the highscore */
    public void setHighscore(double newScore){
        if(newScore > highscore){
            highscore = newScore;
        }
    }

    /**Returns the highscore*/
    public double getHighscore(){
        return highscore;
    }

    /**Gets the current active player*/
    public Player getActivePlayer(){
        return activePlayer;
    }

    public Player getPlayer(){
        return player;
    }

    public Player getOpponentPlayer(){
        return opponent;
    }

    public void computerPlay(){
        Board opponentBoard = opponent.getBoard();
        opponentBoard.hit(opponent.pickPlace(opponentBoard));

    }

    /**Given the x and y coordinates of the board, marks the place as hit
     * @param x is x-coordinate of board, 0-based index
     * @param y is y-coordinate of board, 0-based index
     * @Return true if was a valid place to hit and place was hit*/
    public boolean hitPlace(int x, int y){
        Board board = activePlayer.getBoard();
        return board.hit(board.placeAt(x,y));

    }

     public void changeTurn(){
        if(activePlayer == player){
            activePlayer = opponent;
        }
        else{
            activePlayer = player;
        }
    }
}
