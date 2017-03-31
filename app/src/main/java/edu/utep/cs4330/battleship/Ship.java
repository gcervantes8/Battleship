package edu.utep.cs4330.battleship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gerardo C on 2/6/2017.
 */

class Ship implements Serializable {

    /**The name of the ship*/
    private String name;

    /**Size of the ship*/
    private int size;

    /**Is true if ship has been sunk, boolean will be updated after isShipSunk() has been called*/
    private boolean isSunk = false;

    /**Is the amount of times the ship has been shot, can be updated by calling updateAmountShot()*/
    private int amountShot = 0;

    /**Shows the direction.  If true then ship is placed horizontal*/
    private boolean dir = true;

    /**List containing the places where ship is placed*/
    private List<Place> placed = new ArrayList<>();

    public Ship(int size){
        this.size = size;
        name = " ";
    }
    Ship(String name, int size){
        this.name = name;
        this.size = size;
    }

    /**Returns size of ship*/
    int getSize(){
        return size;
    }

    /**Sets the places where ship is on, used to place the ship*/
    void placeShip(List<Place> places){
        placed = places;
    }

    /**Returns amount of time ship has been shot*/
    public int getAmountShot(){
        updateAmountShot();
        return amountShot;
    }

    /**Updates the field amountShot*/
    private void updateAmountShot(){
        amountShot = 0;
        for (Place place : placed) {
            if(place.isHit()) {
                amountShot++;
            }
        }
    }

    /**Changes the direction of the ship*/
    void setDir(boolean newDir){
        dir = newDir;
    }

    /**Returns ship's direction*/
    boolean getDir(){
        return dir;
    }

    /**Returns true if ship is placed on a board*/
    boolean isPlaced(){
        return !placed.isEmpty();
    }

    /**Returns true if the ship has been sunk*/
    boolean isShipSunk(){

        //If field isSunk is true, then ship is sunk, otherwise check if ship is sunk by checking the places
        if(isSunk){
            return true;
        }
        //Return false if ship hasn't been placed
        if(placed == null){
            return false;
        }
        updateAmountShot();

        isSunk = (size <= amountShot);
        return isSunk;
    }

    /**Returns a list of places where ship is placed*/
    List<Place> getPlacement(){
        return placed;
    }

    void removeShip(){
        placed.clear();
    }
}
