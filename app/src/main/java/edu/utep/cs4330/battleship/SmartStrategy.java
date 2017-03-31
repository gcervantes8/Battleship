package edu.utep.cs4330.battleship;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.io.Serializable;
/**
 * Created by Gerardo C on 2/25/2017.
 */

class SmartStrategy implements StrategyInterface, Serializable{


    /**Picks value between 0 and 1.  Smart strategy looks at the game like a checkerboard
     * if it is looking for a spot to hit, then we can eliminiate half of the options by only
     * hitting places that are diagonal from each other, don't hit places adjacent unless you hit a ship.
     * We can do this because the smallest ship is of size 2*/




    static final String strategyName = "SMART STRATEGY";

    /**Randomness used frequently*/
    private Random rng = new Random();

    /**If the last move hit a ship, then lastMoveHitShip[0] contains x coordinate, and lastMoveHitShip[1] contains y coordinate
    of place that was hit, if last move didn't hit a ship, then is null*/
    private int[] lastMoveHitShip = null;

    /**The different direction of the ship, has unknown if still in hunt mode*/
    private final int UNKNOWN = 0;
    private final int UP = 1;
    private final int RIGHT = 2;
    private final int DOWN = 3;
    private final int LEFT = 4;

    /**Contains up,down, left, and right constants depending on which adjacent directions smart strategy has tried hitting*/
    private List<Integer> directionsToTry = new LinkedList<>();


    //TODO lastDirectionTried can be turned into a boolean for whether we are in hunt mode
    /**Contains direction of last ship hit
     * If previously hit a ship, then we know have knowledge about how the ship is placed.*/
    private int lastDirectionTried = UNKNOWN;


    /**Returns the name of the strategy, which will be used to change strategy or to identify it*/
    public String getStrategyName(){
        return strategyName;
    }

    /**Picks a place from the board that the strategy should hit*/
    public Place pickStrategyMove(Board board){
        if(lastMoveHitShip == null || lastMoveHitShip.length < 2){
            return hunt(board);
        }
        return target(board);
    }



    /**Targets the last place that hit a ship by looking into the last directions tried*/
    private Place target(Board board){
        int lastX = lastMoveHitShip[0];
        int lastY = lastMoveHitShip[1];
        Place toHit = null;

        //If tried all directions already, goes back to hunt mode
        if(directionsToTry.size() == 0){
            toHuntMode();
            return hunt(board);
        }


        int randomDirection = directionsToTry.get(rng.nextInt(directionsToTry.size()));
        lastDirectionTried = randomDirection;

        directionsToTry.remove(Integer.valueOf(randomDirection)); //Done to prevent calling Linked list remove(int) method given index.  Intended call is method(Object)

        switch (randomDirection){
            case UP: toHit = goDirection(board, lastX, lastY, 0, -1); break;
            case RIGHT: toHit = goDirection(board, lastX, lastY, 1, 0); break;
            case DOWN: toHit = goDirection(board, lastX, lastY, 0 , 1); break;
            case LEFT: toHit = goDirection(board, lastX, lastY, -1, 0); break;
        }


        if(toHit == null){

            return target(board);
        }
        return toHit;
    }

    /**Looks for a place on the board to hit the ship*/
    private Place hunt(Board board){

        lastDirectionTried = UNKNOWN; //Only used for hunt
        int boardSize = board.size();

        Place toHit = null;
        //Finds a Checkerboard place to hit that hasn't already been hit
        for (int i = 0; i < (boardSize*boardSize*4) && (toHit == null || toHit.isHit() || !isCheckerboardPlace(toHit)); i++){
            toHit = board.placeAt(rng.nextInt(boardSize), rng.nextInt(boardSize));
        }

        if(toHit == null || toHit.isHit() || !isCheckerboardPlace(toHit)){
            for(int i = 0; i < (boardSize*boardSize*4) && (toHit == null || toHit.isHit()); i++ ){
                toHit = board.placeAt(rng.nextInt(boardSize), rng.nextInt(boardSize));
            }
        }
        return toHit;
    }

    /**Returns true if place is part of a checkerboard place.*/
    private boolean isCheckerboardPlace(Place place){
        return (place.getX()+place.getY()) % 2 == 0;
    }

    /**Fills the directionsToTry list with all the adjacent places*/
    private void fillDirectionsList(){
        directionsToTry.add(UP);
        directionsToTry.add(DOWN);
        directionsToTry.add(LEFT);
        directionsToTry.add(RIGHT);
    }

    /**Returns the place adjacent to given coordinates.  Direction it should look is given by dx and dy*/
    private Place goDirection(Board board, int x, int y, int dx, int dy){
        int newX = x+dx;
        int newY = y+dy;

        if(board.isOutOfBounds(newX, newY)){
            return target(board);
        }
        //If place is already hit, then keep going in the same direction if it had a ship, if it didn't have a ship, go back to target
        if(board.placeAt(newX,newY).isHit()){
            if(board.placeAt(newX,newY).hasShip()){
                return goDirection(board, newX, newY, dx, dy);
            }
            else{
                return target(board);
            }
        }
        return board.placeAt(newX, newY);
    }

    /**Changes current mode of strategy to go back to hunt mode, either because the ship was sunk or it tried all possible places*/
    private void toHuntMode(){
        directionsToTry.clear();
        lastDirectionTried = UNKNOWN;
        lastMoveHitShip = null;
    }

    /**Should be called after a computer move has been made so that the strategy knows how well it did.
     * If is not called with results after a computer move, then it might make strategy less good/accurate*/
    public void afterHit(boolean shipHit, boolean shipSunk, int x, int y){

        //If ship was sunk, then goes back to hunt mode
        if(shipSunk){
            toHuntMode();
        }
        else if(shipHit) {

            //If ship was hit during hunt-mode
            if(lastDirectionTried == UNKNOWN){
                lastMoveHitShip = new int[]{x, y};
                fillDirectionsList();
            }
            else{
                directionsToTry.add(lastDirectionTried);
            }
        }

    }

}
