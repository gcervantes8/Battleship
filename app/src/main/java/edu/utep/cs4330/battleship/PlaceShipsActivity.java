package edu.utep.cs4330.battleship;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PlaceShipsActivity extends AppCompatActivity {

    /**The board that will be displayed so user can place ships*/
    private BoardView boardView;

    /**The board where ships will be placed*/
    private Board playerBoard;

    /**The ship that is being dragged, null if no ship is being dragged*/
    private ShipView shipBeingDragged = null;

    /**Fleet of shipViews for every ship that can be added*/
    private List<ShipView> fleetView = new LinkedList<>();

    /**Place button, which is disabled if not doing placing ships*/
    private Button placeButton;


    /**Opponent's board, given by other user using network connection, if playing p2p game, otherwise is should always be null*/
    private Board opponentBoard = null;


    private boolean donePlacingShips = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.content_place_ships, null);
        setContentView(layout);

        boardView = (BoardView) findViewById(R.id.placeShipsBoardView);
        playerBoard = new Board();
        boardView.setBoard(playerBoard);
        boardView.displayBoardsShips(true);

        placeButton = (Button) findViewById(R.id.placeButton);
        enablePlaceButton(false);


        ImageView minesweeper = (ImageView) findViewById(R.id.minesweeperStatus);
        ImageView frigate = (ImageView) findViewById(R.id.frigate);
        ImageView submarine = (ImageView) findViewById(R.id.submarine);
        ImageView battleship = (ImageView) findViewById(R.id.battleship);
        ImageView aircraftcarrier = (ImageView) findViewById(R.id.aircraftcarrier);


        fleetView.add(new ShipView(minesweeper, new Ship("minesweeper", 2)));
        fleetView.add(new ShipView(frigate, new Ship("frigate", 3)));
        fleetView.add(new ShipView(submarine, new Ship("submarine", 3)));
        fleetView.add(new ShipView(battleship, new Ship("battleship", 4)));
        fleetView.add(new ShipView(aircraftcarrier, new Ship("aircraftcarrier", 5)));

        for(ShipView shipView: fleetView){
            setShipImage(shipView);
        }

        setContentView(layout);

        setBoardDragListener(boardView, playerBoard);

        boardView.invalidate();

        startReadingMessage();
    }



    /**Sets drag listener for the board, snaps the object being dragged onto the board.
     * Ship is also placed on the board*/
    public void setBoardDragListener(final BoardView boardView, final Board board){
        boardView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                switch(event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;

                    case DragEvent.ACTION_DRAG_EXITED :
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION  :
                        break;

                    case DragEvent.ACTION_DRAG_ENDED   :
                        break;

                    case DragEvent.ACTION_DROP:


                        float x = event.getX();
                        float y = event.getY();
                        int width;
                        int height;

                        if(!shipBeingDragged.getShip().getDir()) {
                            width = shipBeingDragged.getShipImage().getHeight();
                            height = shipBeingDragged.getShipImage().getWidth();

                        }else{
                            width = shipBeingDragged.getShipImage().getWidth();
                            height = shipBeingDragged.getShipImage().getHeight();
                        }

                        //x and y coordinates of top-left of image, relative to the board
                        float boardX = x - (width/2);
                        float boardY = y - (height/2);

                        int xy = boardView.locatePlace(boardX, boardY);
                        if(xy == -1){
                            return true;
                        }
                        int xGrid =  xy/100;
                        int yGrid =  xy%100;

                        if(!board.placeShip(shipBeingDragged.getShip(), xGrid, yGrid, shipBeingDragged.getShip().getDir())){
                            return true;
                        }

                        if(!shipBeingDragged.getShip().getDir()) {
                            shipBeingDragged.getShipImage().setX(v.getX() + (xGrid*(v.getWidth()/10)) - (height/2) + (width/2));
                            shipBeingDragged.getShipImage().setY(v.getY() + (yGrid*(v.getHeight()/10)) + (height/2) - (width/2));

                        }else{
                            shipBeingDragged.getShipImage().setX(v.getX() + (xGrid*(v.getWidth()/10)));
                            shipBeingDragged.getShipImage().setY(v.getY() + (yGrid*(v.getHeight()/10)));
                        }


                        boardView.invalidate();
                        if(allShipsPlaced()){
                            enablePlaceButton(true);
                        }
                        break;

                    default: break;
                }
                return true;
            }
        });
    }

    void startReadingMessage(){

          Thread readMessages = new Thread(new Runnable(){
              public void run(){
                  while(true){
                       String msg = NetworkAdapter.readMessage();
                        if(msg == null){
                           //Connection lost handler

                            toast("Connection Lost! Now playing single player game against computer");
                            return;
                        }
                       else if(msg.startsWith(NetworkAdapter.PLACED_SHIPS)){
                            //Gets board
                            opponentBoard = NetworkAdapter.decipherPlaceShips(msg);

                            //If you are already done placing ships, and you have received your opponent's board, then startActivity
                            if(donePlacingShips){

                            }
                       }
                  }
              }
          });
        readMessages.start();
    }

    private void toast(final String s) {
        //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**Returns true if all ships have been placed*/
    public boolean allShipsPlaced(){
        for(ShipView ship: fleetView){
            if(ship.getShip() == null){
                return false;
            }

          if(!ship.getShip().isPlaced()){
              return false;
          }
        }
        return true;
    }

    /**Segues to the play activity, gives information to play activity*/
    public void segueToPlayActivity(View view){

        donePlacingShips = true;

        Intent i = new Intent(this, MainActivity.class);

        GameManager game =  new GameManager(playerBoard);
        Bundle bundle = new Bundle();

        //If there is a p2p connection
        if(NetworkAdapter.getSocket() != null){

            //If other player has given us their board
            if(opponentBoard != null){
                game =  new GameManager(playerBoard, opponentBoard);
                bundle.putSerializable("gameManager", game);
                i.putExtra("gameManager", bundle);
                startActivity(i);
            }
            else{
                toast("Game will start when the other player places their ships");
                return;
            }
        }

        /** If the game is multiplayer */
        /*if(NetworkAdapter.getSocket() != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkAdapter.setMyBoard(playerBoard);
                    NetworkAdapter.sendMyBoard();
                }
            }).start();
        }*/

        /** Attempt to get their board, error should be thrown if player takes too long, or there was a connection error */
        /*try {
            game.setOpponentBoard(NetworkAdapter.readTheirBoard(this));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }*/

        bundle.putSerializable("gameManager", game);
        i.putExtra("gameManager", bundle);
        startActivity(i);
    }

    /**Correctly scales the image and gives it a touch listener*/
    private void setShipImage(final ShipView shipView){
        setImageScaling(shipView.getShipImage());
        setTouchListener(shipView);
    }

    /**Gives a touch listener to the image for dragging*/
    private void setTouchListener(final ShipView shipView){
        final ImageView image = shipView.getShipImage();
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    ClipData data = ClipData.newPlainText("", "");


                    double rotationRad = Math.toRadians(image.getRotation());
                    final int w = (int) (image.getWidth() * image.getScaleX());
                    final int h = (int) (image.getHeight() * image.getScaleY());
                    double s = Math.abs(Math.sin(rotationRad));
                    double c = Math.abs(Math.cos(rotationRad));
                    final int width = (int) (w * c + h * s);
                    final int height = (int) (w * s + h * c);
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(image) {
                        @Override
                        public void onDrawShadow(Canvas canvas) {
                            canvas.scale(image.getScaleX(), image.getScaleY(), width / 2,
                                    height / 2);
                            canvas.rotate(image.getRotation(), width / 2, height / 2);
                            canvas.translate((width - image.getWidth()) / 2,
                                    (height - image.getHeight()) / 2);
                            super.onDrawShadow(canvas);
                        }

                        @Override
                        public void onProvideShadowMetrics(Point shadowSize,
                                                           Point shadowTouchPoint) {
                            shadowSize.set(width, height);
                            shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
                        }
                    };

                    image.startDrag(data, shadowBuilder, image, 0);
                    //image.setVisibility(View.INVISIBLE);
                    shipBeingDragged = shipView;
                    deselectAllShipViews();
                    select(shipView);

                    return true;
                } else {
                    return false;
                }
            }

        });
    }

    /**Rotates the ship and then places the ship in the middle of the screen*/
    public void rotateButtonTapped(View v){
        ShipView shipToRotate = findSelectedShip();
        rotateShip(shipToRotate);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        shipToRotate.getShipImage().setX(width/3+10);
        shipToRotate.getShipImage().setY((height/4)-20);

        enablePlaceButton(false);

        if(shipToRotate.getShip() != null) {
            for (Place place : shipToRotate.getShip().getPlacement()) {
                place.setShip(null);
            }
            shipToRotate.getShip().removeShip();
        }

        shipToRotate.getShipImage().setOnTouchListener(null);
        setTouchListener(shipToRotate); //Creates new touch listener to update the shadow builder
        boardView.invalidate();
    }

    private void enablePlaceButton(Boolean enable){
        if(enable){
            placeButton.setEnabled(true);
            placeButton.setTextColor(Color.WHITE);
            placeButton.setBackgroundColor(Color.rgb(102,153,0));
        }
        else{
            placeButton.setBackgroundColor(Color.rgb(75,120,30));
            placeButton.setTextColor(Color.rgb(115,115,115));
            placeButton.setEnabled(false);
        }
    }

    /**Finds the ship that has a selected image*/
    private ShipView findSelectedShip(){
        for(ShipView shipView: fleetView){
            if(shipView.isSelected()){
                return shipView;
            }
        }
        return null;
    }

    /**Rotates the ship*/
    private void rotateShip(ShipView shipToRotate){
        if(shipToRotate.getShip().getDir()){
            shipToRotate.getShipImage().setRotation(90);
            shipToRotate.getShip().setDir(false);
        }
        else{
            shipToRotate.getShipImage().setRotation(0);
            shipToRotate.getShip().setDir(true);
        }
    }

    /**Selects the image, image needs to be selected for rotation*/
    public void select(ShipView shipView){
        shipView.setSelected(true);
        shipView.getShipImage().setBackgroundColor(Color.GREEN);
    }

    /**Deselects all of the images*/
    public void deselectAllShipViews(){
        for(ShipView shipView: fleetView){
            shipView.setSelected(false);
            shipView.getShipImage().setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**Scales the image to have the same height as a tile on the boardView*/
    private void setImageScaling(final ImageView image){

        image.setAdjustViewBounds(true);

        ViewTreeObserver vto = image.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                image.setMaxHeight(boardView.getMeasuredHeight()/10);
            }

        });
    }
}
