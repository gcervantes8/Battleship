package edu.utep.cs4330.battleship;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;


import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity{


    /**The view of the Board, main player's board view (computer)*/
    private BoardView playerBoardView;

    /**The view of the Board, opponent's board view (computer)*/
    private BoardView opponentBoardView;

    /**Contains the game model*/
    private GameManager game;

    /**TextView that says what strategy the computer opponent is using*/
    private TextView strategyDescription;

    /**Shows the status of the game, whose turn it is or if someone has won*/
    private TextView gameStatus;

    /**MediaPlayer used to play sound effects for shooting a ship and missing*/
    private MediaPlayer mp;

    /**If sound is disabled then it is false*/
    private boolean soundEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        //Gets Game Manager from previous activity or makes a new one
        if(intent == null){
            game = new GameManager();
        }
        else{
            Bundle oldBundle = intent.getBundleExtra("gameManager");
            game = (GameManager) oldBundle.getSerializable("gameManager");
            if(game == null){
                game = new GameManager();
            }
        }


        opponentBoardView = (BoardView) findViewById(R.id.opponentBoardView);
        playerBoardView = (BoardView) findViewById(R.id.playerBoardView);
        strategyDescription = (TextView) findViewById(R.id.strategy_description);
        gameStatus = (TextView) findViewById(R.id.gameStatus);

        //Gives board references to the BoardViews
        setNewBoards(playerBoardView, opponentBoardView, game.getPlayer().getBoard(), game.getOpponentPlayer().getBoard());


        if(NetworkAdapter.hasConnection()) {
            startReadingNetworkMessages();
        }
        else{
            toast("No connection with opponent"); //TODO used for debugging remove before submission, or add something else to indicate not connected
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //Restores Game State for after an orientation happens
        game = (GameManager) savedInstanceState.getSerializable("game");
        setNewBoards(playerBoardView, opponentBoardView, game.getPlayer().getBoard(), game.getOpponentPlayer().getBoard());
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        //Saves Game State for before an orientation happens
        super.onSaveInstanceState(bundle);
        bundle.putSerializable("game", game);
    }

    /**Gives a Board references to the BoardViews*/
    private void setNewBoards(BoardView playerBoardView, BoardView opponentBoardView, Board playerBoard, Board opponentBoard){

        playerBoardView.setBoard(playerBoard);
        opponentBoardView.setBoard(opponentBoard);

        playerBoardView.displayBoardsShips(true);

        opponentBoardView.addBoardTouchListener(new BoardView.BoardTouchListener(){
            @Override
            public void onTouch(int x, int y){
                boardTouched(x, y);
            }
        });

        playerBoardView.invalidate();
        opponentBoardView.invalidate();
    }

    void startReadingNetworkMessages(){

        Thread readMessages = new Thread(new Runnable(){
            public void run(){
                while(true){
                    String msg = NetworkAdapter.readMessage();
                    Log.d("wifiMe", "Message received: " + msg);
                    if(msg == null){
                        //Connection lost handler
                        Log.d("wifiMe", "Connection Lost!");
                        toast("Connection Lost! Now playing single player game against computer");
                        return;
                    }
                    else if(msg.startsWith(NetworkAdapter.PLACED_SHIPS)){
                        Log.d("wifiMe", "Received place ships message?? Shouldn't have found one, debug");
                    }
                    else if(msg.startsWith(NetworkAdapter.NEW_GAME)){
                        Log.d("wifiMe", "New game requested, but we will ignore");
                    }
                    else if(msg.startsWith(NetworkAdapter.PLACE_SHOT)){
                        Log.d("wifiMe", "Place was shot message received, message: " + msg);
                        int[] placeShot = NetworkAdapter.decipherPlaceShot(msg);
                        if(placeShot == null){
                            Log.d("wifiMe", "Found no coordinates");
                            return;
                        }
                        Log.d("wifiMe", "Placed shot on: " + placeShot[0] + ", " + placeShot[1]);
                        p2pOpponentPlay(placeShot[0], placeShot[1]);

                    }

                }
            }
        });
        readMessages.start();
    }

    /**Resets game when button was tapped*/
    public void resetGame(View view){

        //Doesn't ask user to reset the game if game is over or no shots have been made to the board
        if(game.getActivePlayer().getBoard().numOfShots() == 0  || game.getActivePlayer().areAllShipsSunk()){
            resetGame();
            return;
        }
        resetPromptDialog();
    }

    /**AlertDialog is used to display a dialog, the dialog asks the user if they want to reset the game.
     * Game is reset if user taps on yes*/
    public void resetPromptDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getString(R.string.reset_game_title));
        alertDialog.setMessage(getString(R.string.reset_game_prompt));

        //Yes button, and listener for if button is pressed
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                resetGame();
            }
        });

        //No button
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    /**Resets game*/
    public void resetGame(){
        game = new GameManager();
        if(strategyDescription.getText().equals(getString(R.string.random_opponent))){
            game.changeStrategy(RandomStrategy.strategyName);
        }
        else{
            game.changeStrategy(SmartStrategy.strategyName);
        }

        setNewBoards(playerBoardView, opponentBoardView, game.getPlayer().getBoard(), game.getOpponentPlayer().getBoard());
    }

    /**Plays sound of the resource id
     * @param soundResourceId that will be played, should be in res/raw/ folder*/
    public void playSound(int soundResourceId){
        if(!soundEnabled){
           return;
        }
        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }

        mp = MediaPlayer.create(this, soundResourceId);
        mp.start();
    }

    /**Called when board was touched
     * @param x is the x-coordinate of the square that was touched, 0-based index
     * @param y is the y-coordinate of the square that was touched, 0-based index*/
    public void boardTouched(int x, int y) {

        Place placeToHit = game.getOpponentPlayer().getBoard().placeAt(x, y);

        boolean isGameOver = game.getPlayer().areAllShipsSunk() || game.getOpponentPlayer().areAllShipsSunk();
        boolean computersTurn = game.getActivePlayer() != game.getPlayer();
        boolean placeAlreadyHit = placeToHit.isHit();

        //If game is over, or is computer's turn, or place touched was already hit
        if(isGameOver || computersTurn || placeAlreadyHit){
            //Ignore button click
            return;
        }

        game.hitPlace(x, y);


        //If place we hit had a ship
        if(placeToHit.hasShip()){
            playSound(R.raw.shiphit);
        }
        //Player keeps turn if they hit a ship
        else{
            game.changeTurn();
            updateTurnDisplay();
            playSound(R.raw.miss);
        }

        opponentBoardView.invalidate();

        boolean playerWon = game.getOpponentPlayer().areAllShipsSunk();
        if(playerWon){
            updateWinDisplay(true);
            resultsDialog(true, game.getShipsSunkCount(game.getPlayer()) );
            return;
        }

        if(NetworkAdapter.hasConnection()){
            NetworkAdapter.writePlaceShotMessage(x, y);
        }else {

            boolean isComputersTurn = game.getActivePlayer() != game.getPlayer();
            if (isComputersTurn) {
                computerTurn();
            }
        }
    }

    public void updateTurnDisplay(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(game.getOpponentPlayer() == game.getActivePlayer()) {
                    if(NetworkAdapter.hasConnection()){
                        gameStatus.setText(getString(R.string.opponent_turn_status));
                    }
                    else{
                        gameStatus.setText(getString(R.string.computer_turn_status));
                    }
                }
                else{
                    gameStatus.setText(getString(R.string.player_turn_status));
                }
            }
        });
    }

    public void updateWinDisplay(final boolean playerWon){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(playerWon){
                    gameStatus.setText(getString(R.string.win_status));
                }
                else{
                    gameStatus.setText(getString(R.string.lose_status));
                }
            }
        });
    }

    public void p2pOpponentPlay(int x, int y){
        Place placeToHit = game.getPlayer().getBoard().placeAt(x, y);

        game.hitPlace(x, y);


        //If place we hit had a ship
        if(placeToHit.hasShip()){
            playSound(R.raw.shiphit);
        }
        //Player keeps turn if they hit a ship
        else{
            game.changeTurn();
            updateTurnDisplay();
            playSound(R.raw.miss);
        }

        playerBoardView.invalidate();

        boolean p2pOpponentWon = game.getPlayer().areAllShipsSunk();
        if(p2pOpponentWon){
            updateWinDisplay(false);
            resultsDialog(true, game.getShipsSunkCount(game.getPlayer()) );
            return;
        }
    }

    /**Takes care of computer shooting the board.
     * Shoots board, checks if ship was hit or sunk.*/
    public void computerTurn(){

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Place placeToHit = game.computerPickPlace();

                        //Prevents computer from placing immediately after player places
                        sleep(450);

                        game.computerPlay(placeToHit);

                        boolean hitShip = placeToHit.hasShip();


                        if (hitShip) {
                            playSound(R.raw.shiphit);

                            boolean sunkShip = placeToHit.getShip().isShipSunk();
                            if (sunkShip) {

                            }
                        }
                        else{
                            game.changeTurn();
                            updateTurnDisplay();
                            playSound(R.raw.miss);
                        }
                        final boolean computerWon = game.getPlayer().areAllShipsSunk();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playerBoardView.invalidate();
                                if(computerWon){
                                    updateWinDisplay(false);
                                    resultsDialog(false,  game.getShipsSunkCount(game.getPlayer()) );
                                }
                            }
                        });

                        if(computerWon){
                            return;
                        }
                        sleep(250); //Prevents player from place immediately after the computer places

                        if(placeToHit.hasShip()){
                            computerTurn();
                        }
                    }
                    catch(InterruptedException e){
                        Log.d("", "Exception thrown in computerTurn() method in MainActivity");
                        computerTurn();
                    }
                }
            });

            thread.start();
    }

    /**Shows pop-up so that user can select their opponent*/
    public void showOpponentSelectPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.opponent_selection, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem item){
                String newStrategyName = null;
                String newStrategyDescription = "";
                switch (item.getItemId()){
                    case R.id.smart_opponent: newStrategyName = SmartStrategy.strategyName; newStrategyDescription = getString(R.string.smart_opponent); break;
                    case R.id.random_opponent: newStrategyName = RandomStrategy.strategyName;  newStrategyDescription = getString(R.string.random_opponent); break;
                    default: break;
                }
                game.changeStrategy(newStrategyName);
                strategyDescription.setText(newStrategyDescription);
                return true;
            }
        });
    }

    /**Uses AlertDialog display a winning dialog after the player has won the game*/
    private void resultsDialog(boolean winner, int shipsSunk){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        String title, description;
        if(winner){
            title = getString(R.string.winner_title);
            description = getString(R.string.winner_description) + " " + shipsSunk + " ships";
        }
        else{
            title = getString(R.string.loser_title);
            description = getString(R.string.loser_description) + " " + shipsSunk + " ships";
        }
        alertDialog.setTitle(title);
        alertDialog.setMessage(description);

        //Ok button
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //Sound is a toggle for when to disable and enable
            case R.id.sound:
                soundEnabled = !soundEnabled;
                if(soundEnabled){
                    item.setTitle(getString(R.string.disable_sound));
                }
                else{
                    item.setTitle(getString(R.string.enable_sound));
                }
                return true;
            case R.id.place_ships:
                Intent i = new Intent(this, PlaceShipsActivity.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
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
}
