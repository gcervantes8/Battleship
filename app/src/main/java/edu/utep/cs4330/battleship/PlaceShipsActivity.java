package edu.utep.cs4330.battleship;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class PlaceShipsActivity extends AppCompatActivity {

    BoardView boardView;
    Board board;

    ShipView shipBeingDragged = null;

    ShipView minesweeper;
    ShipView frigate;
    ShipView battleship;
    ShipView aircraftcarrier;
    ShipView submarine;


    private List<ShipView> fleetView = new LinkedList<>();

    Button placeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.content_place_ships, null);
        setContentView(layout);
        //setContentView(R.layout.activity_place_ships);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        boardView = (BoardView) findViewById(R.id.placeShipsBoardView);
        board = new Board();
        boardView.setBoard(board);

        placeButton = (Button) findViewById(R.id.placeButton);
        placeButton.setBackgroundColor(Color.rgb(75,120,30));
        placeButton.setTextColor(Color.rgb(115,115,115));
        placeButton.setEnabled(false);


        ImageView minesweeper = (ImageView) findViewById(R.id.minesweeperStatus);
        ImageView frigate = (ImageView) findViewById(R.id.frigate);
        ImageView submarine = (ImageView) findViewById(R.id.submarine);
        ImageView battleship = (ImageView) findViewById(R.id.battleship);
        ImageView aircraftcarrier = (ImageView) findViewById(R.id.aircraftcarrier);



        /*minesweeperShip = new Ship("minesweeper", 2);
        frigateShip = new Ship("frigate", 3);
        submarineShip = new Ship("submarine", 3);
        battleshipShip  = new Ship("battleship" , 4);
        aircraftcarrierShip = new Ship("aircraftcarrier", 5);*/

        fleetView.add(new ShipView(minesweeper, new Ship("minesweeper", 2)));
        fleetView.add(new ShipView(frigate, new Ship("frigate", 3)));
        fleetView.add(new ShipView(submarine, new Ship("submarine", 3)));
        fleetView.add(new ShipView(battleship, new Ship("battleship", 4)));
        fleetView.add(new ShipView(aircraftcarrier, new Ship("aircraftcarrier", 5)));

        /*setShipImage(minesweeper);
        setShipImage(frigate);
        setShipImage(submarine);
        setShipImage(battleship);
        setShipImage(aircraftcarrier);*/
        for(ShipView shipView: fleetView){
            setShipImage(shipView);
        }

        setContentView(layout);

        setBoardDragListener(boardView, board);

        boardView.invalidate();
    }


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
                        shipBeingDragged.getShipImage().setVisibility(View.VISIBLE);


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



                        if(allShipsPlaced()){
                            placeButton.setEnabled(true);
                            placeButton.setTextColor(Color.WHITE);
                            placeButton.setBackgroundColor(Color.rgb(102,153,0));
                        }

                        break;
                    default: break;
                }
                return true;
            }
        });
    }

    public boolean allShipsPlaced(){
        for(ShipView ship: fleetView){
          if(!ship.getShip().isPlaced()){
              return false;
          }
        }
        return true;
        //return minesweeperShip.isPlaced() && frigateShip.isPlaced() && battleshipShip.isPlaced() && aircraftcarrierShip.isPlaced();
    }

    public void segueToPlayActivity(View view){
        Intent i = new Intent(this, MainActivity.class);

        GameManager game =  new GameManager(board);
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameManager", (Serializable) game);
        i.putExtra("gameManager", bundle);

        startActivity(i);
    }

    private void selectImage(ImageView image){
        image.setBackgroundColor(Color.GREEN);
        image.setSelected(true);
    }

    private void setShipImage(final ShipView shipView){
        setImageScaling(shipView.getShipImage());
        setTouchListener(shipView);
    }


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
                    image.setVisibility(View.VISIBLE);
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

    public void rotateShip(View v){
        for(ShipView shipView: fleetView){
            if(shipView.isSelected()){
                if(shipView.getShip().getDir() == true){
                    shipView.getShipImage().setRotation(90);
                    shipView.getShip().setDir(false);
                }
                else{
                    shipView.getShipImage().setRotation(0);
                    shipView.getShip().setDir(true);
                }
            }
            shipView.getShipImage().setOnTouchListener(null);
            setTouchListener(shipView); //Creates new touch listener to update the shadow builder
        }
    }

    public void select(ShipView shipView){
        shipView.setSelected(true);
        shipView.getShipImage().setBackgroundColor(Color.GREEN);
    }

    public void deselectAllShipViews(){
        for(ShipView shipView: fleetView){
            shipView.setSelected(false);
            shipView.getShipImage().setBackgroundColor(Color.TRANSPARENT);
        }
    }


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
