package edu.utep.cs4330.battleship;

import java.io.Serializable;

/**
 * Created by ZDeztroyerz on 2/6/2017.
 */

public class Place implements Serializable {

    /**Contains x-coordinate of the place, 0-based index*/
    private int x = 0;

    /**Contains y-coordinate of the place, 0-based index*/
    private int y = 0;

    /**Boolean indicating if place has been hit or not*/
    private boolean isHit = false;

    /**Place can have 1 ship, if it doesn't have a ship then it is null*/
    private Ship ship = null;


    /**Initializes place with x and y coordinates
     * @param x is the x-coordinate of place, 0-based index
     * @param y is the y-coordinate of place, 0-based index*/
    public Place(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**Checks to see if place has been hit
     * @return true if place has been hit*/
    boolean isHit(){
        return isHit;
    }

    /**Marks the place as hit*/
    void hit() {
        isHit = true;
    }

    /**Gets X-coordinate, 0-based index*/
    public int getX(){
        return x;
    }

    /**Gets Y-coordinate, 0-based index*/
    public int getY(){
        return y;
    }

    /**Returns true if place has a ship*/
    boolean hasShip() {
        return ship != null;
    }

    /**Checks if place contains the ship
     * @param shipToCheck is the ship you want to find is in the place*/
    boolean hasShip(Ship shipToCheck) {
        return ship == shipToCheck;
    }

    /**Removes the ship from the place*/
    void removeShip(){
        ship = null;
    }

    /**Sets a ship on the place*/
    protected void setShip(Ship ship){
        this.ship = ship;
    }

    /**Returns the ship that is in the place, null if no ship is in the place*/
    public Ship getShip(){
        return ship;
    }

}