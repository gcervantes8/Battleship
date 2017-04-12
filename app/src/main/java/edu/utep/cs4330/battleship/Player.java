package edu.utep.cs4330.battleship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Gerardo C on 2/6/2017.
 */

class Player implements Serializable {


    /**
     * Each player has 1 board, this is a reference to the player's own board
     * (the one that contains their ships, not the one they will be hitting)
     */
    private Board playerBoard = null;

    /**
     * List of ships that the player has
     */
    private List<Ship> fleet = new LinkedList<>();

    /**
     * Creates board and places ships on the board, location of ships are random
     */
    Player() {
        playerBoard = new Board();

        placeShipRandomly(playerBoard, new Ship("Minesweeper", 2));
        placeShipRandomly(playerBoard, new Ship("Frigate", 3));
        placeShipRandomly(playerBoard, new Ship("Submarine", 3));
        placeShipRandomly(playerBoard, new Ship("Battleship", 4));
        placeShipRandomly(playerBoard, new Ship("Aircraft carrier", 5));
    }

    /**
     * Gives the player a new board to use
     */
    Player(Board board) {
        setBoard(board);
    }

    /**
     * Places a given ship onto the board in a random location, location chosen won't already have a ship
     */
    private Ship placeShipRandomly(Board board, Ship ship) {
        Random rng = new Random();
        boolean dir = rng.nextBoolean();
        // if dir is true then ship will be placed horizontally

        int[] maxCoordinates = findMaxLocation(board.size(), ship.getSize(), dir);

        //Then can't place ship
        if (maxCoordinates == null) {
            return null;
        }

        int maxX = maxCoordinates[0];
        int maxY = maxCoordinates[1];

        boolean placedShip = false;

        while (!placedShip) {

            int x = rng.nextInt(maxX);
            int y = rng.nextInt(maxY);

            //if was able to place ship on board
            if (board.placeShip(ship, x, y, dir)) {
                placedShip = true;
            }
        }

        fleet.add(ship);
        return ship;
    }

    /**
     * Given the board size, ship size, and direction the ship will be placed,
     * finds the maxX and maxY locations the ship can be placed in.  You can make a 2D
     * square from (0,0) to (maxX,maxY), that square is all the locations where ship can be placed
     *
     * @return integer array containing X and Y coordinates.  Returns null if can't be placed
     */
    private int[] findMaxLocation(int boardSize, int shipSize, boolean dir) {

        int maxX = boardSize;
        int maxY = boardSize;
        if (dir) {
            maxX = boardSize - shipSize;
        } else {
            maxY = boardSize - shipSize;
        }

        if (maxX < 0 || maxY < 0) {
            return null;
        }

        return new int[]{maxX, maxY};
    }

    /**
     * Returns the player's board
     */
    public Board getBoard() {
        return playerBoard;
    }

    /**
     * Returns all of the places that have a ship and have been hit
     */
    List<Place> getShipHitPlaces() {
        List<Place> shipHitPlaces = new LinkedList<>();

        for (Ship ship : fleet) {
            List<Place> shipPlaces = ship.getPlacement();
            for (Place shipPlace : shipPlaces) {
                if (shipPlace.isHit()) {
                    shipHitPlaces.add(shipPlace);
                }
            }

        }
        return shipHitPlaces;
    }

    /**
     * Returns the amount of ships on the board that have been sunk
     */
    int shipsSunk() {
        int sunkShips = 0;

        for (Ship ship : fleet) {
            if (ship.isShipSunk()) {
                sunkShips++;
            }
        }
        return sunkShips;
    }

    /**
     * Gives a reference to a new board to the player
     */
    public void setBoard(Board newBoard) {
        playerBoard = newBoard;
        updateShipsFromBoard(newBoard);
    }

    /**
     * Updates the ships field based on a new board given
     */
    private void updateShipsFromBoard(Board board) {
        Place place;
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                place = board.placeAt(i, j);
                if (place != null && place.hasShip()) {
                    addShip(place.getShip());
                }
            }
        }
    }

    /**
     * Adds a ship to the player's fleet
     */
    private void addShip(Ship shipToAdd) {
        for (Ship ship : fleet) {
            //If ship we want to add is already in fleet, don't add.  (Acting as set)
            if (ship == shipToAdd) {
                return;
            }
        }
        fleet.add(shipToAdd);
    }

    /**
     * Returns true if all the ships on the board have been sunk
     */
    boolean areAllShipsSunk() {

        return getBoard().isAllSunk();
        /*for (Ship ship : fleet) {
            if(!ship.isShipSunk()){   //TODO remove after testing is done
                return false;
            }
        }
        return true;*/

    }
}
