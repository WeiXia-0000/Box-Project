package boxshogi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BoxShogi {
    private int endGameFlag;
    private Board gameBoard; 
    private String winMessage;
    private String previewMove;
    private boolean lowerTurn;
    private List<String> upperCaptures;
    private List<String> lowerCaptures;
    private List<String> availableMoves;
    private BufferedReader bufferedReader;

    public BoxShogi(InputStream inputStream) {
        this.endGameFlag = 0;
        this.winMessage = "";
        this.previewMove = "";
        this.lowerTurn = true;
        this.gameBoard = new Board();
        this.upperCaptures = new ArrayList<>();
        this.lowerCaptures = new ArrayList<>();
        this.availableMoves = new ArrayList<>();
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }
    
    public void gameStart() throws IOException {
        while (true) {
            // Show game status.
            showGameStatus(lowerTurn);

            // Check is need to end the game
            if (endGameFlag == 1) { break; }

            // Show player before ask for input.
            if (lowerTurn) { System.out.print("lower> "); }
            else { System.out.print("UPPER> "); }

            // Read in input and handle it.
            String userInput = bufferedReader.readLine().trim();
            boolean inputValid = handleUserInput(userInput, lowerTurn);

            // Now, it is the turn of another player.
            if (inputValid) { lowerTurn = !lowerTurn; }
        }
    }

    /**
     * Function that shows current game status
     * Example: 
     * 
     *   5 | N| G| R| S| D|
     *   4 |__|__|__|__| P|
     *   3 |__|__|__|__|__|
     *   2 | p|__|__|__|__|
     *   1 | d| s| r| g| n|
     *       a  b  c  d  e
     *
     *   Captures UPPER:
     *   Captures lower:
     * 
     * @param lowerTurn boolean state whether is lower player's turn
     */
    private void showGameStatus(boolean lowerTurn) {
        String gameStatusMessage = previewMove + "\n"
            + gameBoard.toString() + "\n"
            + "Captures UPPER: " + String.join(" ", upperCaptures) + "\n"
            + "Captures lower: " + String.join(" ", lowerCaptures) + "\n\n";

        // If win message has been set, show win message
        if (!winMessage.equals("")) {
            gameStatusMessage += winMessage;
        } else {
            // If player is in check, show in check.
            if (availableMoves.size() != 0) {
                if (lowerTurn) { gameStatusMessage += "lower player is in check!"; }
                else { gameStatusMessage += "UPPER player is in check!"; }
                gameStatusMessage += "Available moves:\n" + String.join("\n", availableMoves);
                availableMoves.clear();
            }
        }

        System.out.println(gameStatusMessage);
    }

    /**
     * Function that finds all possible moves when player is in check.
     * @param lowerTurn boolean state whether is lower player's turn
     */
    public void findAvailableMoves(boolean lowerTurn) {
    }
        

    /**
     * Function that handles the user input.
     * @param userInpuString string representing the user input.
     * @param lowerTurn boolean state whether is lower player's turn
     * @return whether the input is valid
     */
    private boolean handleUserInput(String userInpuString, boolean lowerTurn) {
        String[] inputs = userInpuString.split(" ");
        String command = inputs[0];
        boolean inputHandled = false;
        if (command.equalsIgnoreCase("exit")) { endGameFlag = 1; }
        else if (command.equalsIgnoreCase("move")) { inputHandled = handleMove(inputs, lowerTurn); }
        else if (command.equalsIgnoreCase("drop")) { inputHandled = handleDrop(inputs, lowerTurn); }

        // If there is a problem in input
        if (!inputHandled) { return false; }

        // Update player pre move message
        if (lowerTurn) { previewMove = "lower player action: "; }
        else { previewMove = "UPPER player action: "; }
        previewMove += String.join(" ", Arrays.asList(inputs));

        return true;
    }

    /**
     * 
     * @param inputs
     * @param lowerTurn
     * @return
     */
    private boolean handleMove(String[] inputs, boolean lowerTurn) {
        if ((inputs.length == 4 && !inputs[3].equalsIgnoreCase("promote")) 
                || inputs.length != 3
                || !(inputs[1].matches("[a-zA-Z]\\d") && inputs[2].matches("[a-zA-Z]\\d"))) {
            return false;
        }
        String locationToBeMove = inputs[1];
        String locationMoveTo= inputs[2];
        Piece pieceToMove = getPieceOnLocation(locationToBeMove);
        if (pieceToMove == null 
                || (pieceToMove.getIsUpper() && lowerTurn) 
                    || (!pieceToMove.getIsUpper() && !lowerTurn)) {
            if (lowerTurn) { winMessage = "UPPER players wins.  "; }
            else { winMessage = "lower players wins.  "; }
            winMessage += "Illegal move.";
            endGameFlag = 1;
            return true;
        }
        return true;
    }

    /**
     * 
     * @param inputs
     */
    private boolean handleDrop(String[] inputs, boolean lowerTurn) {
        if (inputs.length != 3
                || !(inputs[1].matches("[a-zA-Z]") && inputs[2].matches("[a-zA-Z]\\d"))) {
            return false;
        }
        return true;
    }

    /**
     * Funtion that returns the Piece on given location of the board.
     * @param location string in form "[a-zA-Z]\\d" represting location
     * @return the Piece on given location of the board
     */
    private Piece getPieceOnLocation(String location) {
        if (location.length() == 2) {
            int firstPart = location.charAt(0) - 'a';
            int secondPart = Integer.valueOf(location.substring(1));
            if (firstPart < 0 || firstPart > 4 || secondPart < 0 || secondPart > 4) {
                System.out.println("Invalid location in getPieceOnLocation!\n"); 
                return null; 
            }
            return gameBoard.getPiece(firstPart, secondPart);
        }
        System.out.println("Invalid location in getPieceOnLocation!\n"); 
        return null;
    }
}