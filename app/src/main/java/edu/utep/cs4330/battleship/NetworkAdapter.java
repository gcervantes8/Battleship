package edu.utep.cs4330.battleship;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Jerry C on 4/10/2017.
 */

public class NetworkAdapter {
    /**The socket that will be used for the connection, null if no connection is established*/
    private static Socket socket;

    /**Used to write messages to socket*/
    private static PrintWriter out;

    /**Used to receive messages from other user*/
    private static BufferedReader in;

    /**Message constant, sent or received when all your ships have been placed*/
    public static final String PLACED_SHIPS = "SHIPS PLACED";

    /**Message constant, Sent or received when a player requests to play a new game*/
    public static final String NEW_GAME = "NEW GAME REQUEST";

    /**Message constant, Sent or received when a place has been shot, message sent usually contains coordinates in the format of "PLACE SHOT 3,5"*/
    public static final String PLACE_SHOT = "PLACE SHOT";

    public static Board myBoard;

    //Methods accessed statically, prevents objects from being created to avoid confusion
    private NetworkAdapter(){}

    /**Used to set sockets*/
    public static void setSocket(Socket s){
        try {
            socket = s;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch(IOException e){
            Log.d("Exception", "Exception thrown in Network Adapter");
            e.printStackTrace();
        }
    }

    public static Socket getSocket(){
        return socket;
    }

    /**To be called on a new thread - blocks the calling thread, if it returns, should be called again on new thread to continue listening to messages
     * Once it returns, it stops listening for messages
     * How to use, call the method and when it returns make a comparison with Constant values in Network Adapter, ex: run(){ if( readMessage().equals(NetworkAdapter.NEW_GAME) ) {...} }*/

    /**
     * Pseudocode:
     * void startReadingMessage(){
     *
     *     Thread readMessages = new Thread(new Runnable(){
     *         public void run(){
     *             while(true){
     *                  String msg = NetworkAdapter.readMessage();
     *                  if(msg == null){
     *                      //Connection lost handler
     *                  }
     *                  else if(msg.equals(NetworkAdapter.PLACED_SHIPS)){
     *                      //Do appropriate action
     *                  }
     *                  else if(msg.equals(NetworkAdapter.NEW_GAME)){
     *                      //request new game
     *                  }
     *                  else if(msg.startsWith(NetworkAdapter.PLACE_SHOT){
     *                      int[] coordinateShots = decipherPlaceShot(msg);
     *                      //shoot ship, check coordinateShots is not null
     *                  }
     *
     *             }
     *         }
     *
     *     }).start();
     *
     * }
     *
     * */
    public static String readMessage(){
        if(in == null){
            //Only returns null if sockets aren't set correctly
            return null;
        }
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                if(msg.equals("") || msg.equals(" ")){ //Checking " " probably unnecessary
                    continue;
                }
                return msg;
            }

        } catch (IOException e) {
            Log.d("Exception", "IOException ON NETWORK ADAPTER CLASS, READ MESSAGES METHOD");
        }
        return null;
    }
    /**NOTE METHOD DOES NOT WORK IF BOARD IS OF SIZE BIGGER THAN 10, needs to be changed slightly to work with boards size bigger than 10
     * @return null if was not a placeShot message, or message did not have 2 coordinates specified
     * @return integer array of size 2 with coordinates of places shot, coordinates use 0 based index, (0,0) - top left corner.  int[0] - x coordinate, int[1] - y coordinate*/
    public static int[] decipherPlaceShot(String msg){
        if(msg == null || !msg.startsWith(PLACE_SHOT)){
            return null;
        }
        int[] coordinatesShot = new int[2];
        boolean firstDigitFound = false;
        for(int i = 0; i < msg.length(); i++){
            char letter = msg.charAt(i);

            if(isDigit(letter)){
                int digitFound = Character.getNumericValue(letter);

                if(firstDigitFound){
                    coordinatesShot[1] = digitFound;
                    return coordinatesShot;
                }
                else{
                    coordinatesShot[0] = digitFound;
                }
                firstDigitFound = true;
            }
        }

        return coordinatesShot;
    }

    public static void setMyBoard(Board x){
        myBoard = x;
    }

    public static void sendMyBoard(){
        String toSend = myBoard.toString();

        /**Add trailing symbol to show its the of the board (nvm) */
//        toSend += "f";
        out.write(toSend);
    }

    /** Should only be called once, per game connection */
    public static Board readTheirBoard(final Activity ctx) throws IOException, InterruptedException {
        int timeout = 20;
        String theirBoard = "";
        while(timeout > 0){

            /** Try to read their board */
            in.readLine();

            if(!theirBoard.isEmpty()){
                /** Got board, done and break*/
                break;
            }
            else{
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "Waiting on opponent...\nPlease don't quit app.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            /** Sleep for same amount as toast duration to allow time for opponent to send their board
             *  (LENGTH_SHORT is an int! So I'm assuming it refers to number of seconds, so I'm
             *  multiplying it by 1000 to turn into millis.
             */
            Thread.sleep(Toast.LENGTH_SHORT*1000);

            timeout--;

            // TODO: Add this feature
            if(timeout == 0){
                /** Throw some error (or restart the activity entirely) */
            }
        }

        /** Begin parsing the string into a board */
        Board b = new Board(myBoard.size());
        int traverseString = 0;
        char[] tb = theirBoard.toCharArray();
        for(int i = 0; i < b.size(); i++){
            for(int j = 0; j < b.size(); j++){
                int shipType = tb[traverseString++];
                Place place = b.placeAt(i, j);

                if(shipType == 5)
                    place.setShip(new Ship("aircraftcarrier", 5));
                else if(shipType == 4)
                    place.setShip(new Ship("battleship", 4));
                else if(shipType == 3)
                    place.setShip(new Ship("submarine", 3));
                else if(shipType == 2)
                    place.setShip(new Ship("frigate", 2));
                else if(shipType == 1)
                    place.setShip(new Ship("minesweeper", 1));
                else{
                    //do nothing, no ship in Place
                }

            }
        }

        return b;
    }

    /**Writes message*/
    public static void writeMessage(String msg){
        out.print(msg);
    }

    /**Writes a place shot message, and places it in given coordinates*/
    public static void writePlaceShotMessage(String msg, int x, int y){
        out.print(msg + " " + x + "," + y);
    }

    /**Returns true if character is a digit*/
    private static boolean isDigit(char l){
        return (l >= '0' && l <= '9');
    }
}
