package edu.utep.cs4330.battleship;


import android.widget.ImageView;

/**
 * Created by Gerardo C on 3/16/2017.
 */

class ShipView {

    /**Image that will be used to display the ship*/
    private ImageView shipImage;

    /**The ship model*/
    private Ship ship;

    /**Ships can be selected, used for rotation, ships are selected if touched*/
    private boolean isSelected = false;

    /**Creates a new ship*/
    ShipView(ImageView image, Ship newShip){
        shipImage = image;
        ship = newShip;
    }

    /**Returns whether ship is selected*/
    boolean isSelected(){
        return isSelected;
    }

    /**Changes ship to be selected*/
    void setSelected(boolean selected){
        isSelected = selected;
    }

    /**Returns the image being used to display the ship*/
    ImageView getShipImage(){
        return shipImage;
    }

    /**Returns the ship*/
    public Ship getShip(){
        return ship;
    }


}
