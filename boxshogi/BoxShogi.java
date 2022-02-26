package boxshogi;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BoxShogi {
    private final int MAX_TURN = 400;
    private final List<String> ALL_POSSIBLE_PIECES =  Arrays.asList("n", "g", "r", "s", "d", "p", "+r", "+g", "+n", "+p");

    private int turnNumber;
    private int endGameFlag;
    private Board gameBoard; 
    private String winMessage;
    private String previewMove;
    private boolean lowerTurn;
    private boolean isInCheck;
    private List<String> moves;
    private List<String> upperCaptures;
    private List<String> lowerCaptures;
    private List<String> availableMoves;
    private List<Utils.InitialPosition> initialPieces;
    private BufferedReader bufferedReader;
    private Map<String, AbstractMap.SimpleEntry<Integer, Integer>> piecePositions;

    /** File mode */

    public BoxShogi(Utils.TestCase input) {
        // Initial
        this.endGameFlag = 0;
        this.gameBoard = new Board(true);
        this.lowerTurn = true;
        this.isInCheck = false;
        this.moves = input.moves;
        this.winMessage = "";
        this.piecePositions = new HashMap<>();
        this.availableMoves = new LinkedList<>();
        this.upperCaptures = new LinkedList<String>(input.upperCaptures);
        this.lowerCaptures = new LinkedList<String>(input.lowerCaptures);
        this.initialPieces = new LinkedList<Utils.InitialPosition>(input.initialPieces);
        initialEmptyBoard(initialPieces);

        // Run move
        checkIsInCheck(lowerTurn, false);
        for (String eachMove : moves) {
            if (!winMessage.equals("") || turnNumber == MAX_TURN) {
                break;
            }
            this.availableMoves.clear();
            this.isInCheck = false;
            handleUserInput(eachMove, this.lowerTurn);
            lowerTurn = !lowerTurn;
            checkIsInCheck(lowerTurn, false);
        }

        showGameStatus(lowerTurn);
        System.out.print("\n");
    }

    /**
     * Function that initialize the board
     * @param initialPositions all positions needed to be initialed in game board before moves
     */
    private void initialEmptyBoard(List<Utils.InitialPosition> initialPositions) {
        for (Utils.InitialPosition eachPosition : initialPositions) {
            String[] splited = eachPosition.toString().split(" ");
            String name = splited[0];
            String location = splited[1];
            Piece piece = new Piece(name, Character.isUpperCase(name.charAt(name.length()-1)));
            AbstractMap.SimpleEntry<Integer, Integer> colRowPair = parseStringLocationToColRow(location);
            this.gameBoard.placePieceOnBoard(colRowPair.getKey(), colRowPair.getValue(), piece);
            piecePositions.put(piece.getName(), colRowPair);
        }
    }

    /** Interactive mode */

    public BoxShogi(InputStream inputStream) {
        this.endGameFlag = 0;
        this.winMessage = "";
        this.previewMove = "";
        this.lowerTurn = true;
        this.isInCheck = false;
        this.gameBoard = new Board(false);
        this.piecePositions = new HashMap<>();
        this.upperCaptures = new LinkedList<>();
        this.lowerCaptures = new LinkedList<>();
        this.availableMoves = new LinkedList<>();
        this.piecePositions = gameBoard.getPiecePosition();
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public void gameStart() throws IOException {
        while (true) {
             // Check is need to end the game
             if (endGameFlag == 1) { break; }

            // Show game status.
            showGameStatus(lowerTurn);

            // Check is player in check
            checkIsInCheck(lowerTurn, false);

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
        } else if (turnNumber == MAX_TURN && !isInCheck) {
            gameStatusMessage += "Tie game.  Too many moves.";
        } else {
            // If player is in check, show in check.
            if (isInCheck && availableMoves.size() == 0) {
                if (lowerTurn) { gameStatusMessage += "UPPER player wins.  Checkmate."; }
                else { gameStatusMessage += "lower player wins.  Checkmate."; }
            } else {
                if (availableMoves.size() != 0) {
                    if (lowerTurn) { gameStatusMessage += "lower player is in check!\n"; }
                    else { gameStatusMessage += "UPPER player is in check!\n"; }
                    gameStatusMessage += "Available moves:\n" + String.join("\n", availableMoves);
                    gameStatusMessage += "\n";
                    availableMoves.clear();
                }
                // Show player before ask for input.
                if (lowerTurn) { gameStatusMessage += "lower>"; }
                else { gameStatusMessage += "UPPER>"; }
            }
        }

        System.out.print(gameStatusMessage);
    }

    /**
     * Function that finds all possible moves when player is in check.
     * @param lowerTurn boolean state whether is lower player's turn
     */
    private void findAvailableMoves(List<String> pieceNameCheckPlayer, boolean lowerTurn, int driveCol, int driveRow) {
        // Player is not in check.
        if (pieceNameCheckPlayer.size() == 0) { return; }

        // Single check
        if (pieceNameCheckPlayer.size() == 1) {
            AbstractMap.SimpleEntry<Integer, Integer> attackerPosition = piecePositions.get(pieceNameCheckPlayer.get(0));
            int attackerCol = attackerPosition.getKey();
            int attackerRow = attackerPosition.getValue();
            // If attacker could be captured
            for (String eachPiece : ALL_POSSIBLE_PIECES) {
                if (!lowerTurn) { eachPiece = eachPiece.toUpperCase(); }
                AbstractMap.SimpleEntry<Integer, Integer> eachPiecePosition = piecePositions.get(eachPiece);
                if (eachPiecePosition == null) { continue; }
                int eachPieceCol = eachPiecePosition.getKey();
                int eachPieceRow = eachPiecePosition.getValue();
                if (!(eachPieceCol == driveCol && eachPieceRow == driveRow) && checkMoveLegal(eachPieceCol, eachPieceRow, attackerCol, attackerRow, lowerTurn, false)) {
                    String locationToBeMove = convertPositionToString(eachPieceCol, eachPieceRow);
                    String locationMoveTo = convertPositionToString(attackerCol, attackerRow);
                    this.availableMoves.add("move " + locationToBeMove + " " + locationMoveTo);
                }
            } 
        }

        // Try to move drive around
        List<AbstractMap.SimpleEntry<Integer, Integer>> drivePossibleMove = getDrivePossibleMove(driveCol, driveRow);
        gameBoard.removePieceFromBoard(driveCol, driveRow);
        for (AbstractMap.SimpleEntry<Integer, Integer> eachPossibleMove : drivePossibleMove) {
            int colToMove = eachPossibleMove.getKey();
            int rowToMove = eachPossibleMove.getValue();
            if (gameBoard.getPiece(colToMove, rowToMove) != null && ((gameBoard.getPiece(colToMove, rowToMove).getIsUpper() && !lowerTurn) 
                    || (!gameBoard.getPiece(colToMove, rowToMove).getIsUpper() && lowerTurn))) {
                continue;
            }
            boolean safe = true;
            for (String eachPiece : ALL_POSSIBLE_PIECES) {
                if (lowerTurn) { eachPiece = eachPiece.toUpperCase(); }
                AbstractMap.SimpleEntry<Integer, Integer> eachPiecePosition = piecePositions.get(eachPiece);
                if (eachPiecePosition == null) { continue; }
                int eachPieceCol = eachPiecePosition.getKey();
                int eachPieceRow = eachPiecePosition.getValue();
                if (checkMoveLegal(eachPieceCol, eachPieceRow, colToMove, rowToMove, !lowerTurn, false) 
                        && !(eachPieceCol == colToMove && eachPieceRow == rowToMove)) {
                    safe = false;
                }
            }
            if (safe) {
                String locationToBeMove = convertPositionToString(driveCol, driveRow);
                String locationMoveTo = convertPositionToString(colToMove, rowToMove);
                this.availableMoves.add("move " + locationToBeMove + " " + locationMoveTo);
            }
        }  
        gameBoard.placePieceOnBoard(driveCol, driveRow, new Piece("d", !lowerTurn));
    }

    private void checkIsInCheck(boolean lowerTurn, boolean checkMove) {
        // Get drive position
        int driveCol;
        int driveRow;
        if (lowerTurn) {
            driveCol = piecePositions.get("d").getKey();
            driveRow = piecePositions.get("d").getValue();
        } else {
            driveCol = piecePositions.get("D").getKey();
            driveRow = piecePositions.get("D").getValue();
        }

        // Store all piece that checking current player
        List<String> pieceNameCheckPlayer = new ArrayList<>();
        for (String eachPiece : ALL_POSSIBLE_PIECES) {
            if (lowerTurn) { eachPiece = eachPiece.toUpperCase(); }
            AbstractMap.SimpleEntry<Integer, Integer> eachPiecePosition = piecePositions.get(eachPiece);
            if (eachPiecePosition == null) { continue; }
            int eachPieceCol = eachPiecePosition.getKey();
            int eachPieceRow = eachPiecePosition.getValue();
            if (checkMoveLegal(eachPieceCol, eachPieceRow, driveCol, driveRow, lowerTurn, false)) {
                pieceNameCheckPlayer.add(eachPiece);
            }
        }

        // Is in check
        this.isInCheck = pieceNameCheckPlayer.size() != 0;

        // If needed, find avaliable moves
        if (!checkMove) {
            findAvailableMoves(pieceNameCheckPlayer, lowerTurn, driveCol, driveRow);
        }
    }
    
    private List<AbstractMap.SimpleEntry<Integer, Integer>> getDrivePossibleMove(int col, int row) {
        List<AbstractMap.SimpleEntry<Integer, Integer>> possibleMove = new ArrayList<>();
        for (int possiblCol = col - 1; possiblCol <= col + 1; possiblCol++) {
            for (int possiblRow = row - 1; possiblRow <= row + 1; possiblRow++) {
                if (possiblCol >= 0 && possiblCol <= 4 && possiblRow >= 0 && possiblRow <=4) {
                    possibleMove.add(new AbstractMap.SimpleEntry<>(possiblCol, possiblRow));
                }
            }
        }
        return possibleMove;
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
        if (command.equalsIgnoreCase("exit")) { endGameFlag = 1; }
        else if (command.equalsIgnoreCase("move")) { handleMove(inputs, lowerTurn); }
        else if (command.equalsIgnoreCase("drop")) { handleDrop(inputs, lowerTurn); }

        // Update player pre move message
        if (lowerTurn) { previewMove = "lower player action: "; }
        else { previewMove = "UPPER player action: "; }
        previewMove += String.join(" ", Arrays.asList(inputs));

        // Increment turn
        turnNumber++;

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
        if (checkMoveLegal(col, row, newCol, newRow, lowerTurn, inputs.length == 4)) {
            gameBoard.removePieceFromBoard(col, row);
            Piece pieceOnTargetLocation = gameBoard.getPiece(newCol, newRow);
            if (pieceOnTargetLocation!= null) {
                // Capture piece that in target location.
                String pieceName = pieceOnTargetLocation.getName();
                if (lowerTurn) { lowerCaptures.add(pieceName.substring(pieceName.length()-1).toLowerCase()); }
                else { upperCaptures.add(pieceName.substring(pieceName.length()-1).toUpperCase()); }
                piecePositions.remove(pieceOnTargetLocation.getName());
            }
            gameBoard.placePieceOnBoard(newCol, newRow, pieceToMove);
            piecePositions.remove(pieceToMove.getName());
            
            // Promote piece user asked.
            if (inputs.length == 4 && !pieceToMove.getIsPromoted()) {
                pieceToMove.promotedPiece();
            }

            // Force preview to be promoted if it reach promotion zone.
            String pieceToMoveName = pieceToMove.getName();
            if (pieceToMoveName.equalsIgnoreCase("p") && ((lowerTurn && newRow == 4) || (!lowerTurn && newRow == 0))) {
                pieceToMove.promotedPiece();
            }

            // Update locations
            piecePositions.put(pieceToMove.getName(), new AbstractMap.SimpleEntry<>(newCol, newRow));

            // If is in check after move
            checkIsInCheck(lowerTurn, true);
            if (isInCheck) { 
                gameBoard.removePieceFromBoard(newCol, newRow); 
                gameBoard.placePieceOnBoard(col, row, pieceToMove);
                setWinMessage("Illegal move.", lowerTurn);
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

    private boolean checkMoveLegal(int col, int row, int newCol, int newRow, boolean lowerTurn, boolean tryPromote) {
        // Get pieceToMove
        Piece pieceToMove = gameBoard.getPiece(col, row);
        if (pieceToMove == null) { return false; }

        // Check if it try to promote, player is actually moving to a promote zone.
        if (tryPromote && (pieceToMove.getIsPromoted() 
                                || pieceToMove.getName().equalsIgnoreCase("d") 
                                || pieceToMove.getName().equalsIgnoreCase("s") 
                                || (lowerTurn && row != 4 && newRow != 4 ) 
                                || (!lowerTurn && row != 0 && newRow != 0))) {
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
        while (!(currentCol == newCol && currentRow == newRow)
                    && currentCol >= 0 && currentCol <= 4
                    && currentRow >= 0 && currentRow <= 4) {
            if (!(currentCol == col && currentRow == row) 
                        && !(currentCol == newCol && currentRow == newRow)
                        && this.gameBoard.getPiece(currentCol, currentRow) != null) {
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

        // Check if the piece is Preview and it is being placed in pomotion zone.
        if (pieceName.equalsIgnoreCase("p") && 
                ((lowerTurn && rowToBePlace == 4) 
                || (!lowerTurn && rowToBePlace == 0))) {
            setWinMessage("Illegal move.", lowerTurn);
            return true;
        }

        // Check if the piece is Preview and it raise a check
        if (pieceName.equalsIgnoreCase("p") && 
                ((lowerTurn && gameBoard.getPiece(colToBePlace, rowToBePlace+1) != null
                    && gameBoard.getPiece(colToBePlace, rowToBePlace+1).getName().equals("D"))
                || (!lowerTurn && gameBoard.getPiece(colToBePlace, rowToBePlace-1) != null
                    && gameBoard.getPiece(colToBePlace, rowToBePlace-1).getName().equals("d")))) {
            setWinMessage("Illegal move.", lowerTurn);
            return true;
        }

        // Check if two preview is in same column
        AbstractMap.SimpleEntry<Integer, Integer> previewPosition;
        if (lowerTurn) { previewPosition = piecePositions.get("p"); }
        else { previewPosition = piecePositions.get("P"); }
        if (previewPosition != null && pieceName.equalsIgnoreCase("p") && colToBePlace == previewPosition.getKey()) {
            setWinMessage("Illegal move.", lowerTurn);
            return true;
        }
 
        // Remove from captures
        if (lowerTurn) { lowerCaptures.remove(pieceName); }
        else { upperCaptures.remove(pieceName.toUpperCase()); }

        // Drop piece on board
        Piece pieceToBeDrop = new Piece(pieceName, !lowerTurn);
        this.gameBoard.placePieceOnBoard(colToBePlace, rowToBePlace, pieceToBeDrop);
        this.piecePositions.put(pieceToBeDrop.getName(), new AbstractMap.SimpleEntry<>(colToBePlace, rowToBePlace));

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

    private String convertPositionToString(int col, int row) {
        String location = String.valueOf((char) (col + (int) 'a')) + String.valueOf(row+1);
        return location;
    }
}