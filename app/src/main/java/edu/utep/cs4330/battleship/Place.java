package edu.utep.cs.cs4330.battleship;

/**
 * Created by ZDeztroyerz on 2/6/2017.
 */

public class Place {

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
    public boolean isHit(){
        return isHit;
    }

    /**Marks the place as hit*/
    public void hit() {
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
    public boolean hasShip() {
        return ship != null;
    }

    /**Sets a ship on the place*/
    public void setShip(Ship ship){
        this.ship = ship;
    }
}