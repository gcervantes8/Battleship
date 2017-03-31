package edu.utep.cs4330.battleship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A game board consisting of <code>size</code> * <code>size</code> places
 * where battleships can be placed. A place of the board is denoted
 * by a pair of 0-based indices (x, y), where x is a column index
 * and y is a row index. A place of the board can be shot at, resulting
 * in either a hit or miss.
 */
public class Board implements Serializable {


    /*Board class uses 0-based index for internal representation*/

    /**
     * Size of this board. This board has
     * <code>size*size </code> places.
     */
    private final int size;

    /**The game board consists of a size x size number of places, the board is a 2D array of Places*/
    private Place[][] board = null;

    /**The amount of places that have been shot*/
    private int placesShot = 0;

    /**Default constructor makes board size 10*/
    public Board(){
        this(10);
    }

    /** Create a new board of the given size. */
    public Board(int size) {
        this.size = size;
        board = new Place[size()][size()];
        createBoard(board);
    }

    /**Creates the board by initializing all of the places in the 2D array*/
    private void createBoard(Place[][] board){

        for(int y = 0; y < board.length; y++){
            for(int x = 0; x < board[0].length; x++){
                board[y][x] = new Place(x, y);
            }
        }
    }

    /**Given the ship, place, and direction*/
    boolean placeShip(Ship ship, int x, int y, boolean dir){

        if(ship == null){
            return false;
        }

        removeShip(ship);

        List<Place> shipPlaces = new ArrayList<Place>();
        Place place;

        //Goes through places where ship will be placed.*/
        for(int i = 0; i < ship.getSize(); i++){
            //If dir is true, then ship will be placed horizontally, otherwise vertically
            if(dir){
                place = placeAt(x+i, y);
            }
            else{
                place = placeAt(x, y+i);
            }

            //If place was invalid or already had a ship, returns false and doesn't place ship
            if(place == null || place.hasShip()) {
                return false;
            }

            //If was a valid place then adds to list of places, and looks through other places
            shipPlaces.add(place);
        }

        /**Gives a reference to the ship to all the places that will have the ship*/
        for(Place placeWithShip: shipPlaces){
            placeWithShip.setShip(ship);
        }

        ship.setDir(dir);
        ship.placeShip(shipPlaces);

        return true;
    }

    /**Removes ship from all places in the board*/
    private void removeShip(Ship ship){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j].hasShip(ship)){
                    board[i][j].removeShip();
                }
            }
        }
        ship.removeShip();
    }

    /**Returns the place in the board with coordinates (x, y)
     * @param x x coordinate 0-based index
     * @param y y coordinate 0-based index
     * @return place on the board*/
    Place placeAt(int x, int y){
        if(board == null || isOutOfBounds(x,y) || board[y][x] == null){
            return null;
        }

        return board[y][x];
    }

    /**Hits given place, returns true if was able to successfully hit the
     * @param placeToHit place you want to hit*/
    boolean hit(Place placeToHit){
        if(placeToHit == null){
            return false;
        }
        //If place hasn't been hit before, then hits the place.
        if(!placeToHit.isHit()){
            placesShot++;
            placeToHit.hit();
            return true;
        }
        return false;
    }

    /**Returns true if the (x,y) coordinates given are outside the board*/
    boolean isOutOfBounds(int x, int y){
        if(x >= size() || y >= size() || x < 0 || y < 0){
            return true;
        }
        return false;
    }

    /**Returns the amount of times that the board has been shot*/
    int numOfShots(){
        return placesShot;
    }
    /** Return the size of this board. */
    int size() {
        return size;
    }

    /**Returns all of the places that have a ship and have been hit*/
    List<Place> getShipHitPlaces() {

        List<Place> boardPlaces = getPlaces();
        List<Place> shipHitPlaces = new ArrayList<Place>();

        for (Place place : boardPlaces) {
            if (place.isHit() && place.hasShip()) {
                shipHitPlaces.add(place);
            }
        }
        return shipHitPlaces;
    }

    /**Returns all of the board's places in a LinkedList*/
    private List<Place> getPlaces(){
        List<Place> boardPlaces = new LinkedList<Place>();
        for(int i = 0; i < size(); i++){
            for(int j = 0; j < size(); j++){
                boardPlaces.add(board[i][j]);
            }
        }
        return boardPlaces;
    }

    /**Returns true if all the ships have been hit*/
    boolean isAllHit(){
        for(int i = 0; i < size(); i++){
            for(int j = 0; j < size(); j++){
                if(!board[i][j].isHit()){
                    return false;
                }
            }
        }
        return true;
    }


}
