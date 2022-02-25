package boxshogi;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BoxShogi {
    private final int MAX_TURN = 200;

    private int endGameFlag;
    private Board gameBoard; 
    private String winMessage;
    private String previewMove;
    private boolean lowerTurn;
    private List<String> moves;
    private List<String> upperCaptures;
    private List<String> lowerCaptures;
    private List<String> availableMoves;
    private List<Utils.InitialPosition> initialPieces;
    private BufferedReader bufferedReader;

    /** File mode */

    public BoxShogi(Utils.TestCase input) {
        // Initial
        this.gameBoard = new Board(true);
        this.lowerTurn = true;
        this.moves = input.moves;
        this.winMessage = "";
        this.initialPieces = new LinkedList<Utils.InitialPosition>(input.initialPieces);
        this.upperCaptures = new LinkedList<String>(input.upperCaptures);
        this.lowerCaptures = new LinkedList<String>(input.lowerCaptures);
        initialEmptyBoard(initialPieces);

        // Run move
        for (String eachMove : moves) {
            if (!winMessage.equals("")) {
                break;
            }
            handleUserInput(eachMove, this.lowerTurn);
            lowerTurn = !lowerTurn;
        }

        showGameStatus(lowerTurn);
        System.out.print("\n");
    }

    private void initialEmptyBoard(List<Utils.InitialPosition> initialPositions) {
        for (Utils.InitialPosition eachPosition : initialPositions) {
            String[] splited = eachPosition.toString().split(" ");
            String name = splited[0];
            String location = splited[1];
            Piece piece = new Piece(name, Character.isUpperCase(name.charAt(name.length()-1)));
            AbstractMap.SimpleEntry<Integer, Integer> colRowPair = parseStringLocationToColRow(location);
            this.gameBoard.placePieceOnBoard(colRowPair.getKey(), colRowPair.getValue(), piece);
        }
    }

    /** Interactive mode */

    public BoxShogi(InputStream inputStream) {
        this.endGameFlag = 0;
        this.winMessage = "";
        this.previewMove = "";
        this.lowerTurn = true;
        this.gameBoard = new Board(false);
        this.upperCaptures = new LinkedList<>();
        this.lowerCaptures = new LinkedList<>();
        this.availableMoves = new LinkedList<>();
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public void gameStart() throws IOException {
        while (true) {
             // Check is need to end the game
             if (endGameFlag == 1) { break; }

            // Show game status.
            showGameStatus(lowerTurn);

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
            gameStatusMessage += winMessage + "\n";
        } else {
            // If player is in check, show in check.
            if (availableMoves != null && availableMoves.size() != 0) {
                if (lowerTurn) { gameStatusMessage += "lower player is in check!"; }
                else { gameStatusMessage += "UPPER player is in check!"; }
                gameStatusMessage += "Available moves:\n" + String.join("\n", availableMoves);
                availableMoves.clear();
            }

            // Show player before ask for input.
            if (lowerTurn) {gameStatusMessage += "lower>"; }
            else { gameStatusMessage += "UPPER>"; }
        }

        System.out.print(gameStatusMessage);
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
        // Check input format.
        if ((inputs.length == 4 && !inputs[3].equalsIgnoreCase("promote")) 
                || inputs.length != 3
                || !(inputs[1].matches("[a-zA-Z]\\d") && inputs[2].matches("[a-zA-Z]\\d"))) {
            return false;
        }

        // Get the piece to move.
        String locationToBeMove = inputs[1];
        AbstractMap.SimpleEntry<Integer, Integer> colRowPair = parseStringLocationToColRow(locationToBeMove);
        Piece pieceToMove = this.gameBoard.getPiece(colRowPair.getKey(), colRowPair.getValue());
        if (pieceToMove == null 
                || (pieceToMove.getIsUpper() && lowerTurn) 
                    || (!pieceToMove.getIsUpper() && !lowerTurn)) {
            if (lowerTurn) { winMessage = "UPPER players wins.  "; }
            else { winMessage = "lower players wins.  "; }
            winMessage += "Illegal move.\n";
            endGameFlag = 1;
            return true;
        }

        // Place piece on new location.
        String locationMoveTo= inputs[2];
        AbstractMap.SimpleEntry<Integer, Integer> newColRowPair = parseStringLocationToColRow(locationMoveTo);
        this.gameBoard.removePieceFromBoard(colRowPair.getKey(), colRowPair.getValue());
        this.gameBoard.placePieceOnBoard(newColRowPair.getKey(), newColRowPair.getValue(), pieceToMove);

        return true;
    }

    /**
     * 
     * @param inputs
     */
    private boolean handleDrop(String[] inputs, boolean lowerTurn) {
        // Check input format.
        if (inputs.length != 3
                || !(inputs[1].matches("[a-zA-Z]") && inputs[2].matches("[a-zA-Z]\\d"))) {
            return false;
        }

        // Store name and location
        String pieceName = inputs[1];
        String location = inputs[2];

        // Parse location
        AbstractMap.SimpleEntry<Integer, Integer> colRowPair = parseStringLocationToColRow(location);
        int colToBePlace = colRowPair.getKey();
        int rowToBePlace = colRowPair.getValue();

        // Check is the piece has been capture
        // of is the place to be place is legal
        if ((lowerTurn && !lowerCaptures.contains(pieceName)) 
                || (!lowerTurn && !upperCaptures.contains(pieceName.toUpperCase()))
                    || (this.gameBoard.getPiece(colToBePlace, rowToBePlace) != null)) {
            if (lowerTurn) { winMessage = "UPPER players wins.  "; }
            else { winMessage = "lower players wins.  "; }
            winMessage += "Illegal move.\n";
            endGameFlag = 1;
            return true;
        }

        // Remove from captures
        if (lowerTurn) { lowerCaptures.remove(pieceName); }
        else { upperCaptures.remove(pieceName.toUpperCase()); }

        // Drop piece on board
        this.gameBoard.placePieceOnBoard(colToBePlace, rowToBePlace, new Piece(pieceName, !lowerTurn));

        return true;
    }

    /**
     * Funtion that returns the Pair representing the col and row.
     * @param location string in form "[a-zA-Z]\\d" represting location
     * @return the Pair representing the col and row
     */
    private AbstractMap.SimpleEntry<Integer, Integer> parseStringLocationToColRow(String location) {
        if (location.length() == 2) {
            int firstPart = location.charAt(0) - 'a';
            int secondPart = Integer.valueOf(location.substring(1)) - 1;
            if (firstPart < 0 || firstPart > 4 || secondPart < 0 || secondPart > 4) {
                System.out.println("Invalid location in getPieceOnLocation!\n"); 
                return null; 
            }
            return new AbstractMap.SimpleEntry<>(firstPart, secondPart);
        }
        System.out.println("Invalid location in getPieceOnLocation!\n"); 
        return null;
    }
}