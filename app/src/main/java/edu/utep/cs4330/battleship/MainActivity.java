package edu.utep.cs.cs4330.battleship;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    //TODO Seperated View/Model into different packages
    //TODO Sperate View and controller more


    /**The Boardview that will view the board and draw it's current state*/
    private BoardView boardView;

    /**Contains the game model*/
    private GameManager game = new GameManager();

    /**Text telling the user how many times they have shot the board*/
    private TextView shotCountText;

    /**Text telling the user how many times they have hit a ship*/
    private TextView amountHitText;

    /**Text telling the user how many times they have sunk a ship*/
    private TextView shipsSunkText;

    /**Text telling the user what their highscore is*/
    private TextView highscoreText;

    /**Button used to start a new game*/
    private Button resetGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shotCountText = (TextView) findViewById(R.id.shotCount);
        amountHitText = (TextView) findViewById(R.id.amountHit);
        shipsSunkText = (TextView) findViewById(R.id.shipsSunk);
        highscoreText = (TextView) findViewById(R.id.highscoreText);
        resetGame = (Button) findViewById(R.id.resetGame);

        boardView = (BoardView) findViewById(R.id.boardView);

        boardView.setBoard(game.getActivePlayer().getBoard());
        boardView.addBoardTouchListener(new BoardView.BoardTouchListener(){
            @Override
            public void onTouch(int x, int y){
                boardTouched(x, y);
            }
        });
    }

    /**Resets game when button was tapped*/
    public void resetGame(View view){

        /**If no moves have been made on the game or game is over, then reset game without asking user*/
        if(game.getActivePlayer().getBoard().numOfShots() == 0  || game.getActivePlayer().areAllShipsSunk()){
            resetGame();
            return;
        }
        resetPromptDialog();
    }

    /**AlertDialog is used to display a dialog asking the user if they want to reset the game
     * resets the game if user taps on Yes button*/
    public void resetPromptDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        alertDialog.setTitle("Reset game");

        alertDialog.setMessage("Are you sure you want to restart the current game?");

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
        double prevHighscore = game.getHighscore();
        game = new GameManager();
        game.setHighscore(prevHighscore);
        boardView.setBoard(game.getActivePlayer().getBoard());
        boardView.invalidate();
        updateShotCount(0);
        updatePlacesHit(0);
        updateShipsSunk(0);
        updateHighscore(game.getHighscore());
        resetGame.setText("START");
    }

    /**Updates textfield with new value of how many times the player has shot the board
     * @param shotCount is how many times a player has shot board*/
    private void updateShotCount(int shotCount){
        shotCountText.setText("Number of Shots: " + shotCount);
    }

    /**Updates textfield with amount of ship hits*/
    private void updatePlacesHit(int placesHit){
        amountHitText.setText("Ship hits: " + placesHit);
    }

    /**Updates textfield with amount of ship hits*/
    private void updateHighscore(double score){
        int roundedScore = (int) score;
        highscoreText.setText("Highscore: " + roundedScore + "%");
    }

    /**Updates textfield with how much ships have been sunk*/
    private void updateShipsSunk(int sunk){
        shipsSunkText.setText("Ships sunk: " + sunk + " / 5");
    }

    /*public void playSound(){
        MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.miss);
        mp.start();
    }*/

    /**Called when board was touched
     * @Param x is the x-coordinate of the square that was touched, 0-based index
     * @Param y is the y-coordinate of the square that was touched, 0-based index*/
    public void boardTouched(int x, int y) {

        resetGame.setText("RESTART");
        Player activePlayer = game.getActivePlayer();

        //If all ships are sunk, then game is over, do nothing if button click
        if(activePlayer.areAllShipsSunk()){
            return;
        }
        game.hitPlace(x, y);

        int sunkenShips = game.getShipsSunkCount(activePlayer);
        int shotCount = activePlayer.getBoard().numOfShots();
        int shipsHit = game.getShipShots(activePlayer);
        updateShotCount(shotCount);
        updatePlacesHit(shipsHit);
        updateShipsSunk(sunkenShips);

        boardView.invalidate();

        //If game was won, then calcuate score and display win dialog
        if(activePlayer.areAllShipsSunk()){
            double score = (double)shipsHit / shotCount;
            score *= 100;

            game.setHighscore(score);
            updateHighscore(game.getHighscore());
            winDialog(score, shipsHit, shotCount);
        }

    }

    /**Uses AlertDialog display a winning dialog after the player has won the game*/
    private void winDialog(double score, int shipsHit, int shotCount){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Winner!");
        alertDialog.setMessage("You have beaten the game by sinking all of the ships! \n\nYour score this game was " + (int) score + "%\n" + shipsHit + " successful shots, " + shotCount + " total shots");

        //Ok button
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }
}
