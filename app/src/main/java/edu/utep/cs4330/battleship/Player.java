package edu.utep.cs.cs4330.battleship;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Gerardo C on 2/6/2017.
 */

public class Player {


    /**Each player has 1 board*/
    private Board playerBoard = null;

    /**List of ships that the player has*/
    private List<Ship> fleet = new LinkedList<Ship>();

    /**Creates board and places ships on the board, location of ships are random*/
    public Player(){
        playerBoard = new Board();

        placeShipRandomly(playerBoard, new Ship("Minesweeper", 2));
        placeShipRandomly(playerBoard, new Ship("Frigate", 3));
        placeShipRandomly(playerBoard, new Ship("Submarine", 3));
        placeShipRandomly(playerBoard, new Ship("Battleship", 4));
        placeShipRandomly(playerBoard, new Ship("Aircraft carrier", 5));
    }

    /**Places a given ship onto the board in a random location, location chosen won't already have a ship
     * */
    private Ship placeShipRandomly(Board board, Ship ship){
        Random rng = new Random();
        boolean dir = rng.nextBoolean(); //direction ship will be placed in
        // if dir is true then ship will be placed horizontally

        int[] maxCoordinates = findMaxLocation(board.size(), ship.getSize(), dir);

        //Then can't place ship
        if(maxCoordinates == null){
            return null;
        }

        int maxX = maxCoordinates[0];
        int maxY = maxCoordinates[1];

        boolean placedShip = false;

        while(!placedShip) {

            int x = rng.nextInt(maxX);
            int y = rng.nextInt(maxY);

            /**If was able to place ship on board*/
            if (board.placeShip(ship, x, y, dir)) {
                placedShip = true;
            }
        }

        fleet.add(ship);
        return ship;
    }

    /**Given the board size, ship size, and direction the ship will be placed,
     * finds the maxX and maxY locations the ship can be placed in.  You can make a 2D
     * square from (0,0) to (maxX,maxY), that square is all the locations where ship can be placed
     * @return integer array containing X and Y coordinates.  Returns null if can't be placed*/
    private int[] findMaxLocation(int boardSize, int shipSize, boolean dir){
        //int[] maxLocation = new int[2];
        int maxX = boardSize;
        int maxY = boardSize;
        if(dir){
            maxX = boardSize - shipSize;
        }
        else{
            maxY = boardSize - shipSize;
        }

        if(maxX < 0 || maxY < 0){
            return null;
        }

        return new int[]{maxX, maxY};
    }

    /**Returns the player's board*/
    public Board getBoard(){
        return playerBoard;
    }

    /**Returns all of the places that have a ship and have been hit*/
    public List<Place> getShipHitPlaces(){
        List<Place> shipHitPlaces = new LinkedList<Place>();

        for(Ship ship : fleet){
            List<Place> shipPlaces = ship.getPlacement();
            for(Place shipPlace : shipPlaces){
                if (shipPlace.isHit()){
                    shipHitPlaces.add(shipPlace);
                }
            }

        }
        return shipHitPlaces;
    }

    /**Returns the amount of ships on the board that have been sunk*/
    public int shipsSunk(){
        int sunkShips = 0;

        for(Ship ship : fleet){
            if(ship.isShipSunk()){
                sunkShips++;
            }
        }
        return sunkShips;
    }

    /**Returns true if all the ships on the board have been sunk*/
    public boolean areAllShipsSunk(){
        for (Ship ship : fleet) {
            if(!ship.isShipSunk()){
                return false;
            }
        }
        return true;
    }
}
