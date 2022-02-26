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
            gameStatusMessage += winMessage;
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
     * Function that handles the user input.`
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
        if ((!(inputs.length == 4 && inputs[3].equalsIgnoreCase("promote")) 
                && inputs.length != 3)
                || !(inputs[1].matches("[a-zA-Z]\\d") && inputs[2].matches("[a-zA-Z]\\d"))) {
            return false;
        }

        // Get the piece to move.
        String locationToBeMove = inputs[1];
        AbstractMap.SimpleEntry<Integer, Integer> colRowPair = parseStringLocationToColRow(locationToBeMove);
        int col = colRowPair.getKey(); 
        int row =  colRowPair.getValue();
        Piece pieceToMove = this.gameBoard.getPiece(col, row); 

        // Check is the piece to be moved belongs to the player.
        if (pieceToMove == null 
                || (pieceToMove.getIsUpper() && lowerTurn) 
                    || (!pieceToMove.getIsUpper() && !lowerTurn)) {
            setWinMessage("Illegal move.", lowerTurn);
            return true;
        }

        // Place piece on new location.
        String locationMoveTo= inputs[2];
        AbstractMap.SimpleEntry<Integer, Integer> newColRowPair = parseStringLocationToColRow(locationMoveTo);
        int newCol = newColRowPair.getKey();
        int newRow =  newColRowPair.getValue();

        // Check is the move possible or not.
        if (checkMoveLegal(col, row, newCol, newRow, pieceToMove, lowerTurn, inputs.length == 4)) {
            gameBoard.removePieceFromBoard(col, row);
            Piece pieceOnTargetLocation = gameBoard.getPiece(newCol, newRow);
            if (pieceOnTargetLocation!= null) {
                // Capture piece that in target location.
                if (lowerTurn) { lowerCaptures.add(pieceOnTargetLocation.getName().toLowerCase()); }
                else { upperCaptures.add(pieceOnTargetLocation.getName().toUpperCase()); }
            }
            gameBoard.placePieceOnBoard(newCol, newRow, pieceToMove);

            // Promote piece if needed
            if (inputs.length == 4) {
                pieceToMove.promotedPiece();
            }
        } else {
            setWinMessage("Illegal move.", lowerTurn);
        }

        return true;
    }

    /**
     * Function that sets the win message.
     * @param message message to be shown
     * @param lowerTurn boolean state whether is lower player's turn
     */
    private void setWinMessage(String message, boolean lowerTurn) {
        if (lowerTurn) { winMessage = "UPPER player wins.  "; }
        else { winMessage = "lower player wins.  "; }
        winMessage += message;
        endGameFlag = 1;
    }

    private boolean checkMoveLegal(int col, int row, int newCol, int newRow, Piece pieceToMove, boolean lowerTurn, boolean tryPromote) {
        // Check if it try to promote, player is actually moving to a promote zone.
        if (tryPromote && (pieceToMove.getIsPromoted() 
                                || pieceToMove.getName().equalsIgnoreCase("d") 
                                || pieceToMove.getName().equalsIgnoreCase("s") 
                                || (lowerTurn && newRow != 4) 
                                || (!lowerTurn && newRow != 0))) {
            return false;
        }

        // Compute distance between col and newCol, row and newRow
        int dCol = newCol-col;
        int dRow = newRow-row;

        // Get rule of current piece
        String pieceRule = pieceToMove.getRule();
        if (checkIsThereAPieceInBetween(col, row, newCol, newRow)) {
            return false;
        }

        // If piece follows the rule of a box drive
        if (pieceRule.equalsIgnoreCase("d")) {
            if (Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1) {
                return true;
            }
            return false;
        }

        // If piece follows the rule of a box note
        if (pieceRule.equalsIgnoreCase("n")) {
            if (dCol == 0 || dRow == 0) {
                return true;
            }
            return false;
        }

        // If piece follows the rule of box governance
        if (pieceRule.equalsIgnoreCase("g")) {
            if (Math.abs(dCol) == Math.abs(dRow)) {
                return true;
            }
            return false;
        }

        // If piece follows the rule of a  box shield
        if (pieceRule.equalsIgnoreCase("s")) {
            if ((Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1)) {
                if (dCol != 0 && ((lowerTurn && dRow == -1)
                        || (!lowerTurn && dRow == 1))) {
                    return false;
                }
                return true;
            }
            return false;
        }

        // If piece follows the rule of a box relay
        if (pieceRule.equalsIgnoreCase("r")) {
            if ((Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1)) {
                if (dRow == 0 && dCol != 0) {
                    return false;
                }
                if (dCol == 0 && ((lowerTurn && dRow == -1)
                        || (!lowerTurn && dRow == 1))) {
                    return false;
                }
                return true;
            }
            return false;
        }

        // If piece follows the rule of a box preview
        if (pieceRule.equalsIgnoreCase("p")) {
            if (dCol == 0 && ((lowerTurn && dRow == 1) || (!lowerTurn && dRow == -1))) {
                return true;
            }
            return false;
        }

        // If piece follows the rule of a box preview
        if (pieceRule.equalsIgnoreCase("gd")) {
            if ((Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1) 
                    || (Math.abs(dCol) == Math.abs(dRow))) {
                return true;
            }
            return false;
        }

        // If piece follows the rule of a box preview
        if (pieceRule.equalsIgnoreCase("nd")) {
            if ((Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1) 
                    || (dCol == 0 || dRow == 0)) {
                return true;
            }
            return false;
        }
        return true;
    }


    private boolean checkIsThereAPieceInBetween(int col, int row, int newCol, int newRow) {
        int colIncrement = Integer.signum(newCol - col);
        int rowIncrement = Integer.signum(newRow - row);
        int currentCol = col;
        int currentRow = row;
        while (currentCol != newCol && currentCol != newCol) {
            if (!(currentCol == col && currentRow == row) 
                        && !(currentCol == newCol && currentRow == newRow)
                        && gameBoard.getPiece(currentCol, currentRow) != null) {
                return true;
            }
            currentCol += colIncrement;
            currentRow += rowIncrement;
        }
        return false;
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
            setWinMessage("Illegal move.", lowerTurn);
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