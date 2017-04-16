package edu.utep.cs4330.battleship;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by Gerardo C on 2/6/2017.
 */

public class GameManager implements Serializable{

    //TODO instead of implementing parcelable, can turn into json for cleaner code

    /**Player whose turn it is*/
    private Player activePlayer;

    /**The player who plays the game*/
    private Player player;

    /**The opponent, could be other human player or npc*/
    private ComputerPlayer opponent;

    /**Creates a new game, players, and boards.*/
    GameManager(){
        player = new Player();
        opponent = new ComputerPlayer();
        activePlayer = player;
    }

    /**Creates new game and uses the player's board*/
    GameManager(Board playerBoard){
        player = new Player(playerBoard);

        opponent = new ComputerPlayer();
        activePlayer = player;
    }

    //TODO
    GameManager(Board playerBoard, Board opponentBoard){
        Log.d("wifiMe", "Player board is null? " + (playerBoard == null));
        player = new Player(playerBoard);
        opponent = new ComputerPlayer(opponentBoard);
        //opponent = new ComputerPlayer();
        activePlayer = player;
    }

    /**Retrieves from given player's board how many ships they have sunk
     * @param player is player's ships you want to check amount of sunk ship*/
    int getShipsSunkCount(Player player){
        return opponent.shipsSunk();
    }

    /**Returns amount of shots that have hit player's ships
     * @param player is the player's ships you want to check*/
    public int getShipShots(Player player){
        return opponent.getShipHitPlaces().size();
    }

    /** Following method sets the opponent's new board*/
    public void setOpponentBoard(Board x){
        opponent.setBoard(x);
    }

    /**Gets the current active player*/
    Player getActivePlayer(){
        return activePlayer;
    }

    /**Returns the player that is not active*/
    private Player getInactivePlayer(){
        if(activePlayer == player){
            return opponent;
        }
        return player;
    }

    /**Returns the main player*/
    Player getPlayer(){
        return player;
    }

    /**Returns opponent player*/
    Player getOpponentPlayer(){
        return opponent;
    }

    /**Hits the given place for the computer
     * @param place you want to the computer to shoot*/
    void computerPlay(Place place){
        Board opponentBoard = player.getBoard();
        boolean hitShip = false;
        boolean sunkShip = false;
        if(opponentBoard.hit(place)){
            if(place.hasShip()){

                hitShip = true;
                //Then computer sunk a ship
                if(place.getShip().isShipSunk()){
                    sunkShip = true;
                }
            }
            opponent.getStrategyInterface().afterHit(hitShip, sunkShip, place.getX(), place.getY());
            return;
        }
        Log.d("", "Computer opponent tried to hit invalid place");
    }

    /**Returns a place chosen by the computer to place their ship*/
    Place computerPickPlace(){
        Board opponentBoard = player.getBoard();
        return opponent.pickPlace(opponentBoard);
    }

    /**Used to change what strategy the computer uses to play the game
     * @param strategyName is the new strategy that will be used by the computer to play the game
     * StrategyInterface should use the class's official naming of the strategy*/
    void changeStrategy(String strategyName){
        opponent.changeStrategy(strategyName);
    }

    /**Given the x and y coordinates of the board, hits non active player's board
     * @param x is x-coordinate of board, 0-based index
     * @param y is y-coordinate of board, 0-based index
     * @return true if was a valid place to hit and place was hit*/
    boolean hitPlace(int x, int y){
        Board board = getInactivePlayer().getBoard();
        return board.hit(board.placeAt(x,y));
    }

    /**Changes whose turn it is*/
     void changeTurn(){
        if(activePlayer == player){
            activePlayer = opponent;
        }
        else{
            activePlayer = player;
        }
    }
}
