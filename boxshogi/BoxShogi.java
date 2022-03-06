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
    /** File mode only attributes **/
    private final int MAX_TURN = 400;
    private int turnNumber;
    private List<String> moves;
    private List<Utils.InitialPosition> initialPieces;

    /** Interactive mode only attribute **/
    private BufferedReader bufferedReader;

    /** Both mode sharing attributes **/
    private int modeFlag; // 0 means file mode, 1 means interactive mode.
    private int endGameFlag;
    private int exitGameFlag;
    private boolean lowerTurn;
    private boolean isInCheck;
    private Board gameBoard;
    private String winMessage;
    private String previewMove;
    private List<String> availableMoves;
    private Map<Boolean, Player> playerStatus;
    

    /** <---------------------- File mode only functions ----------------------> **/

    public BoxShogi(Utils.TestCase input) {
        // Initial
        this.modeFlag = 0;
        this.endGameFlag = 0;
        this.lowerTurn = true;
        this.isInCheck = false;
        this.winMessage = "";
        this.moves = input.moves;
        this.gameBoard = new Board(true);
        this.playerStatus = new HashMap<>();
        this.availableMoves = new LinkedList<>();
        this.initialPieces = new LinkedList<Utils.InitialPosition>(input.initialPieces);
        this.playerStatus.put(lowerTurn, new Player(lowerTurn));
        this.playerStatus.put(!lowerTurn, new Player(!lowerTurn));
        this.playerStatus.get(lowerTurn).setCaptures(input.lowerCaptures);
        this.playerStatus.get(!lowerTurn).setCaptures(input.upperCaptures);
        initialEmptyBoard(initialPieces);

        // Run move;
        checkIsDriveInCheck(false);
        for (String eachMove : moves) {
            if (!winMessage.equals("") || turnNumber == MAX_TURN) {
                break;
            }
            this.availableMoves.clear();
            this.isInCheck = false;
            handleUserInput(eachMove);
            lowerTurn = !lowerTurn;
            checkIsDriveInCheck(false);
        }

        showGameStatus();
        System.out.print("\n");
    }

    /**
     * Function that initialize the board
     * 
     * @param initialPositions all positions needed to be initialed in game board
     *                         before moves
     */
    private void initialEmptyBoard(List<Utils.InitialPosition> initialPositions) {
        for (Utils.InitialPosition eachPosition : initialPositions) {
            String[] splited = eachPosition.toString().split(" ");
            String name = splited[0];
            String location = splited[1];
            Piece piece = new Piece(name, Character.isLowerCase(name.charAt(name.length() - 1)));
            AbstractMap.SimpleEntry<Integer, Integer> colRowPair = parseStringLocationToColRow(location);
            int col = colRowPair.getKey();
            int row = colRowPair.getValue();
            this.gameBoard.placePieceOnBoard(col, row, piece);
            playerStatus.get(piece.getIsLower()).addAPiecePosition(piece.getName(), col, row);
            if (piece.getName().equalsIgnoreCase("d")) {
                playerStatus.get(piece.getIsLower()).setDrivePosition(colRowPair);
            }
        }
    }

    /** <---------------------- Interactive mode only functions ----------------------> **/

    public BoxShogi(InputStream inputStream) {
        this.modeFlag = 1;
        this.endGameFlag = 0;
        this.exitGameFlag = 0;
        this.winMessage = "";
        this.previewMove = "";
        this.lowerTurn = true;
        this.isInCheck = false;
        this.gameBoard = new Board(false);
        this.playerStatus = new HashMap<>();
        this.availableMoves = new LinkedList<>();
        this.playerStatus = gameBoard.getPlayerStatus();
        this.playerStatus.get(lowerTurn).setCaptures(new LinkedList<>());
        this.playerStatus.get(!lowerTurn).setCaptures(new LinkedList<>());
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public void gameStart() throws IOException {
        while (true) {
            // Check is need to exit the game
            if (exitGameFlag == 1) {
                break;
            }

            // Show game status.
            showGameStatus();

            // Check is need to end the game
            if (endGameFlag == 1) {
                break;
            }

            // Read in input and handle it.
            System.out.print(" ");
            String userInput = bufferedReader.readLine().trim();
            boolean inputValid = handleUserInput(userInput);

            // If input is valid, now, it is the turn of another player.
            if (inputValid) {
                checkIsDriveInCheck(false);
                // Check is player in check
                lowerTurn = !lowerTurn;
                checkIsDriveInCheck(false);
            }
            // Otherwise, tell user to give another inpu in correct format
            // If the game shoule not be end.
            else {
                if (endGameFlag != 1) {
                    System.out.println(
                        "-------------------------------------------------------------\n" +
                                "Given input is not valid, please try again in the format:\n" +
                                "\n" +
                                "            move x# x#\n" +
                                "            move x# x# promote\n" +
                                "\n" +
                                "In which x in {a, b, c, d, e}, # in {1,2,3,4,5}\n" +
                                "\n" +
                                "            drop x y#\n" +
                                "\n" +
                                "In which x is a piece in your captures and has to be lowercase!\n" +
                                "          y in {a, b, c, d, e}, # in {1,2,3,4,5}\n" +
                                "-------------------------------------------------------------\n");
                }
            }
        }
    }

    /** <----------------------  Both mode sharing functions ----------------------> **/

    /**
     * Function that shows current game status
     * Example:
     * 
     * 5 | N| G| R| S| D|
     * 4 |__|__|__|__| P|
     * 3 |__|__|__|__|__|
     * 2 | p|__|__|__|__|
     * 1 | d| s| r| g| n|
     * a b c d e
     *
     * Captures UPPER:
     * Captures lower:
     * 
     */
    private void showGameStatus() {
        // Build guaranteed message.
        String gameStatusMessage = previewMove + "\n"
                + gameBoard.toString() + "\n"
                + "Captures UPPER: " + String.join(" ", playerStatus.get(false).getCaptures()) + "\n"
                + "Captures lower: " + String.join(" ", playerStatus.get(true).getCaptures()) + "\n\n";

        // If win message has been set, that is when there is a winner, we add winner on to message.
        if (!winMessage.equals("")) {
            gameStatusMessage += winMessage;

        // If game reaches the max_turn with a tie game, we add tie game to message.
        } else if (turnNumber == MAX_TURN && !isInCheck) {
            gameStatusMessage += "Tie game.  Too many moves.";

        // If player is in check, 
        } else {
            String playerName = playerStatus.get(lowerTurn).getPlayerName();
            String opponentName = playerStatus.get(!lowerTurn).getPlayerName();
            // If player has no available moves to move out or checkmate. We add other player win to message.
            if (isInCheck && availableMoves.size() == 0) {
                gameStatusMessage += opponentName + " player wins.  Checkmate.";
            // If there is avaliable moves, we show add them to message.
            } else {
                if (availableMoves.size() != 0) {
                    gameStatusMessage += playerName + " player is in check!\n";
                    gameStatusMessage += "Available moves:\n" + String.join("\n", availableMoves);
                    gameStatusMessage += "\n";
                    availableMoves.clear();
                }
                // Show player before ask for input.
                gameStatusMessage += playerName + ">";
            }
        }

        // Shew our builted message to player.
        System.out.print(gameStatusMessage);
    }

    /**
     * Function that finds all possible moves when player is in check.
     * 
     * @param lowerTurn boolean state whether is lower player's turn
     */
    private void findAvailableMoves(List<String> pieceNameCheckPlayer, int driveCol, int driveRow) {
        // Curreny player is not in check.
        if (pieceNameCheckPlayer.size() == 0) {
            return;
        }

        // Current player's drive in check by one piece from the opponent.
        if (pieceNameCheckPlayer.size() == 1) {
            AbstractMap.SimpleEntry<Integer, Integer> attackerPosition = playerStatus.get(!lowerTurn)
                    .getPiecePosition(pieceNameCheckPlayer.get(0));
            int attackerCol = attackerPosition.getKey();
            int attackerRow = attackerPosition.getValue();

            // Drop captured piece between the attacker and our drive.
            int colIncrement = Integer.signum(driveCol - attackerCol);
            int rowIncrement = Integer.signum(driveRow - attackerRow);
            List<String> captures = playerStatus.get(lowerTurn).getCaptures();
            for (String eachCapture : captures) {
                if (eachCapture.length() == 0) {
                    continue;
                }
                int currentCol = attackerCol + colIncrement;
                int currentRow = attackerRow + rowIncrement;
                while (!(currentCol == driveCol && currentRow == driveRow)) {
                    String locationDropTo = convertPositionToString(currentCol, currentRow);
                    this.availableMoves.add("drop " + eachCapture.toLowerCase() + " " + locationDropTo);
                    currentCol += colIncrement;
                    currentRow += rowIncrement;
                }
            }

            // Move current piece to the position between drive and attacker.
            int currentCol = attackerCol + colIncrement;
            int currentRow = attackerRow + rowIncrement;
            while (!(currentCol == driveCol && currentRow == driveRow)) {
                for (AbstractMap.SimpleEntry<Integer, Integer> eachPiecePosition : playerStatus.get(lowerTurn)
                        .getPiecePosition().values()) {
                    int eachPieceCol = eachPiecePosition.getKey();
                    int eachPieceRow = eachPiecePosition.getValue();
                    if (gameBoard.getPiece(eachPieceCol, eachPieceRow).getName().equalsIgnoreCase("d")) {
                        continue;
                    }
                    if (checkMoveLegal(eachPieceCol, eachPieceRow, currentCol, currentRow, lowerTurn, false)) {
                        String locationToBeMove = convertPositionToString(eachPieceCol, eachPieceRow);
                        String locationMoveTo = convertPositionToString(currentCol, currentRow);
                        this.availableMoves.add("move " + locationToBeMove + " " + locationMoveTo);
                    }
                }
                currentCol += colIncrement;
                currentRow += rowIncrement;
            }

            // Use our piece to capture the attacker
            for (AbstractMap.SimpleEntry<Integer, Integer> eachPiecePosition : playerStatus.get(lowerTurn)
                    .getPiecePosition().values()) {
                if (eachPiecePosition == null) {
                    continue;
                }
                int eachPieceCol = eachPiecePosition.getKey();
                int eachPieceRow = eachPiecePosition.getValue();
                if (checkMoveLegal(eachPieceCol, eachPieceRow, attackerCol, attackerRow, lowerTurn, false)
                        && !(eachPieceCol == driveCol && eachPieceRow == driveRow)) {
                    // Try to move the piece first and check would drive be in check. 
                    Piece pieceToTry = gameBoard.getPiece(eachPieceCol, eachPieceRow);
                    Piece pieceToCapture = gameBoard.getPiece(attackerCol, attackerRow);
                    gameBoard.removePieceFromBoard(eachPieceCol, eachPieceRow);
                    gameBoard.placePieceOnBoard(attackerCol, attackerRow, pieceToTry);
                    playerStatus.get(!lowerTurn).removeAPiecePosition(pieceToCapture.getName());

                    // Check would drive be in check.
                    boolean inCheck = checkIsDriveInCheck(true);

                    // Put piece back.
                    gameBoard.placePieceOnBoard(eachPieceCol, eachPieceRow, pieceToTry);
                    gameBoard.placePieceOnBoard(attackerCol, attackerRow, pieceToCapture);
                    playerStatus.get(!lowerTurn).addAPiecePosition(pieceToCapture.getName(), attackerCol, attackerRow);

                    // If we find a piece that can capture the attacker, add it to our avaliable moves.
                    if (!inCheck) {
                        String locationToBeMove = convertPositionToString(eachPieceCol, eachPieceRow);
                        String locationMoveTo = convertPositionToString(attackerCol, attackerRow);
                        this.availableMoves.add("move " + locationToBeMove + " " + locationMoveTo);
                    }
                }
            }
        }

        // Try to move our drive around.
        List<AbstractMap.SimpleEntry<Integer, Integer>> drivePossibleMove = getDrivePossibleMove(driveCol, driveRow);
        gameBoard.removePieceFromBoard(driveCol, driveRow);
        for (AbstractMap.SimpleEntry<Integer, Integer> eachPossibleMove : drivePossibleMove) {
            int colToMove = eachPossibleMove.getKey();
            int rowToMove = eachPossibleMove.getValue();
            // If the piece on that locarion belongs to player
            if (gameBoard.getPiece(colToMove, rowToMove) != null
                    && ((!gameBoard.getPiece(colToMove, rowToMove).getIsLower() && !lowerTurn)
                            || (gameBoard.getPiece(colToMove, rowToMove).getIsLower() && lowerTurn))) {
                continue;
            }
            boolean safe = true;
            for (AbstractMap.SimpleEntry<Integer, Integer> eachPiecePositionAttacker : playerStatus
                    .get(!lowerTurn)
                    .getPiecePosition().values()) {
                int eachPieceCol = eachPiecePositionAttacker.getKey();
                int eachPieceRow = eachPiecePositionAttacker.getValue();
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
        gameBoard.placePieceOnBoard(driveCol, driveRow, new Piece("d", lowerTurn));
    }

    /**
     * Function that check is current player in check.
     *
     * @param checkMove true if check after moving drive, false otherwise.
     * @return a boolean indicate is current player in check on that temporary board.
     */
    private boolean checkIsDriveInCheck(boolean checkMove) {
        // Get drive position.
        AbstractMap.SimpleEntry<Integer, Integer> drivePosition = playerStatus.get(lowerTurn).getDrivePosition();
        int driveCol = drivePosition.getKey();
        int driveRow = drivePosition.getValue();

        // Store all piece that checking current player.
        List<String> pieceNameCheckPlayer = new ArrayList<>();
        for (AbstractMap.SimpleEntry<Integer, Integer> eachPiecePosition : playerStatus.get(!lowerTurn)
                .getPiecePosition().values()) {
            if (eachPiecePosition == null) {
                continue;
            }
            int eachPieceCol = eachPiecePosition.getKey();
            int eachPieceRow = eachPiecePosition.getValue();
            if (checkMoveLegal(eachPieceCol, eachPieceRow, driveCol, driveRow, lowerTurn, false)) {
                String pieceName = gameBoard.getPiece(eachPieceCol, eachPieceRow).getName();
                pieceNameCheckPlayer.add(pieceName);
            }
        }

        // If there is attacker that can capture the drive, drive is currently in check.
        boolean currentInCheck = pieceNameCheckPlayer.size() != 0;

        // If needed, find avaliable moves
        if (!checkMove) {
            this.isInCheck = currentInCheck;
            findAvailableMoves(pieceNameCheckPlayer, driveCol, driveRow);
        }
        return currentInCheck;
    }

    /**
     * Funstion that gets all possible positions current drive could be moved to.
     * 
     * @param col the col of current drive
     * @param row the row of current drive
     * @return a list of integer pairs indicating all possible position could be
     *         moved from current drive
     */
    private List<AbstractMap.SimpleEntry<Integer, Integer>> getDrivePossibleMove(int col, int row) {
        List<AbstractMap.SimpleEntry<Integer, Integer>> possibleMove = new ArrayList<>();
        for (int possiblCol = col - 1; possiblCol <= col + 1; possiblCol++) {
            for (int possiblRow = row - 1; possiblRow <= row + 1; possiblRow++) {
                if (possiblCol == col && possiblRow == row) {
                    continue;
                }
                if (possiblCol >= 0 && possiblCol <= 4 && possiblRow >= 0 && possiblRow <= 4) {
                    possibleMove.add(new AbstractMap.SimpleEntry<>(possiblCol, possiblRow));
                }
            }
        }
        return possibleMove;
    }

    /**
     * Function that sets the win message.
     * 
     * @param message message to be shown
     */
    private void setWinMessage(String message) {
        winMessage = playerStatus.get(!lowerTurn).getPlayerName()+ " player wins.  ";
        winMessage += message + "\n";
        endGameFlag = 1;
    }

    /**
     * Function that checks is a move from a given location to a new given location
     * is valid.
     * 
     * @param col        the col of the position a piece will be moved from
     * @param row        the row of the postition a piece will be moved from
     * @param newCol     the new col of the position a piece will be moved to
     * @param newRow     the new row of the position a piece will be moved to
     * @param lowerTurn  boolean indicating the player's turn. true means lowercase
     *                   player; false otherwise.
     * @param tryPromote boolean indicating if player tried to promote,. true means
     *                   yes; false otherwise
     * @return the boolean indicating is current move legally
     */
    private boolean checkMoveLegal(int col, int row, int newCol, int newRow, boolean lowerTurn, boolean tryPromote) {
        // Get pieceToMove
        Piece pieceToMove = gameBoard.getPiece(col, row);
        if (pieceToMove == null) {
            return false;
        }

        // Check if it try to promote, player is actually moving to a promote zone.
        if (tryPromote && (pieceToMove.getIsPromoted()
                || pieceToMove.getName().equalsIgnoreCase("d")
                || pieceToMove.getName().equalsIgnoreCase("s")
                || (lowerTurn && row != 4 && newRow != 4)
                || (!lowerTurn && row != 0 && newRow != 0))) {
            return false;
        }

        // Compute distance between col and newCol, row and newRow
        int dCol = newCol - col;
        int dRow = newRow - row;

        // Get rule of current piece
        if (checkIsThereAPieceInBetween(col, row, newCol, newRow)) {
            return false;
        }

        // Now, we only need to check basic rule.
        return pieceToMove.checkMoveFollowBasicRule(dCol, dRow);
    }

    /**
     * Function that handles the user input.`
     * 
     * @param userInpuString string representing the user input.
     * @param lowerTurn      boolean state whether is lower player's turn
     * @return whether the input is valid
     */
    private boolean handleUserInput(String userInpuString) {
        String[] inputs = userInpuString.split(" ");
        String command = inputs[0];
        boolean inputIsValid = false;
        if (command.equalsIgnoreCase("exit")) {
            exitGameFlag = 1;
            return true;
        } else if (command.equalsIgnoreCase("move")) {
            inputIsValid |= handleMove(inputs);
        } else if (command.equalsIgnoreCase("drop")) {
            inputIsValid |= handleDrop(inputs);
        }

        // Update player pre move message
        previewMove = playerStatus.get(lowerTurn).getPlayerName() + " player action: "; 
        previewMove += String.join(" ", Arrays.asList(inputs));

        // Increment turn
        turnNumber++;

        return inputIsValid;
    }

    /**
     * Function that handles user move ** input.
     * 
     * @param inputs arrays contains command and arguments from user
     * @return a boolean indicating whether this move should be executed
     */
    private boolean handleMove(String[] inputs) {
        // Check input format.
        if ((!(inputs.length == 4 && inputs[3].equalsIgnoreCase("promote"))
                && inputs.length != 3)
                || !(inputs[1].matches("[a-eA-E]\\d") && inputs[2].matches("[a-eA-E]\\d"))) {
            System.out.println("Invalid location given in move.");
            return false;
        }

        // Get the piece to move.
        String locationToBeMove = inputs[1];
        AbstractMap.SimpleEntry<Integer, Integer> colRowPair = parseStringLocationToColRow(locationToBeMove);

        // If user input location is not valid, ask for input again.
        // Otherwise, get the point.
        if (colRowPair == null) {
            return false;
        }
        int col = colRowPair.getKey();
        int row = colRowPair.getValue();
        Piece pieceToMove = this.gameBoard.getPiece(col, row);

        // Check is the piece to be moved belongs to the player.
        if (pieceToMove == null
                || (!pieceToMove.getIsLower() && lowerTurn)
                || (pieceToMove.getIsLower() && !lowerTurn)) {
            setWinMessage("Illegal move.");
            endGameFlag = 1;
            return false;
        }

        // Place piece on new location.
        String locationMoveTo = inputs[2];
        AbstractMap.SimpleEntry<Integer, Integer> newColRowPair = parseStringLocationToColRow(locationMoveTo);

        // If user input location is not valid, ask for input again.
        // Otherwise, get the point.
        if (newColRowPair == null) {
            return false;
        }
        int newCol = newColRowPair.getKey();
        int newRow = newColRowPair.getValue();
        Piece pieceOnTargetLocation = gameBoard.getPiece(newCol, newRow);

        // Check if the player try to capture his our piece.
        if (!(newCol == col && newRow == row) && pieceOnTargetLocation != null &&
                ((pieceOnTargetLocation.getIsLower() && lowerTurn)
                        || (!pieceOnTargetLocation.getIsLower() && !lowerTurn))) {
            setWinMessage("Illegal move.");
            endGameFlag = 1;
            return false;
        }

        // Check is the move possible or not.
        if (checkMoveLegal(col, row, newCol, newRow, lowerTurn, inputs.length == 4)) {
            if (pieceOnTargetLocation != null && !(newCol == col && newRow == row)) {
                // Capture piece that in target location.
                String pieceName = pieceOnTargetLocation.getName();
                playerStatus.get(lowerTurn).addCaptures(pieceName.substring(pieceName.length() - 1));
                playerStatus.get(!lowerTurn).removeAPiecePosition(pieceName);
            }

            // Move current piece to a new position.
            String pieceToMoveName = pieceToMove.getName();
            gameBoard.removePieceFromBoard(col, row);
            gameBoard.placePieceOnBoard(newCol, newRow, pieceToMove);
            playerStatus.get(lowerTurn).removeAPiecePosition(pieceToMoveName);

            // Promote piece user asked.
            if (inputs.length == 4 && !pieceToMove.getIsPromoted()) {
                pieceToMove.promotedPiece();
            }

            // Force preview to be promoted if it reach promotion zone.
            if (pieceToMoveName.equalsIgnoreCase("p") && ((lowerTurn && newRow == 4) || (!lowerTurn && newRow == 0))) {
                pieceToMove.promotedPiece();
            }

            // Update locations.
            playerStatus.get(lowerTurn).addAPiecePosition(pieceToMoveName, newCol, newRow);
            if (pieceToMoveName.equalsIgnoreCase("d")) {
                playerStatus.get(lowerTurn).setDrivePosition(newColRowPair);
            }

            // If is in check after move.
            if (checkIsDriveInCheck(true)) {
                gameBoard.removePieceFromBoard(newCol, newRow);
                gameBoard.placePieceOnBoard(col, row, pieceToMove);
                if (pieceOnTargetLocation != null) {
                    playerStatus.get(!lowerTurn).addAPiecePosition(pieceOnTargetLocation.getName(), col, row);
                }
                setWinMessage("Illegal move.");
                endGameFlag = 1;
                return false;
            }
        } else {
            setWinMessage("Illegal move.");
            endGameFlag = 1;
            return false;
        }

        return true;
    }

    /**
     * Function that handles user drop ** input.
     * 
     * @param inputs arrays contains command and arguments from user
     */
    private boolean handleDrop(String[] inputs) {
        // Check input format.
        if (inputs.length != 3
                || !(inputs[1].matches("[a-z]") && inputs[2].matches("[a-eA-E]\\d"))) {
            System.out.println("Invalid location given in drop.");
            return false;
        }

        // If the player is in check, then the drop must be in the available move.
        if (isInCheck && !availableMoves.contains("drop " + inputs[1] + " " + inputs[2])) {
            setWinMessage("Illegal move.");
            endGameFlag = 1;
            return false;
        }

        // Store name and location and piece
        String pieceName = inputs[1];
        String location = inputs[2];
        Piece pieceToBeDrop = new Piece(pieceName, lowerTurn);

        // Parse location
        AbstractMap.SimpleEntry<Integer, Integer> colRowPair = parseStringLocationToColRow(location);
        if (colRowPair == null) {
            return false;
        }
        int colToBePlace = colRowPair.getKey();
        int rowToBePlace = colRowPair.getValue();

        // Check is the piece has been capture
        // of is the place to be place is legal
        if (!playerStatus.get(lowerTurn).getCaptures().contains(pieceToBeDrop.getName())
                || (this.gameBoard.getPiece(colToBePlace, rowToBePlace) != null)) {
            setWinMessage("Illegal move.");
            endGameFlag = 1;
            return false;
        }

        // Check if the piece is Preview and it is being placed in pomotion zone.
        if (pieceName.equalsIgnoreCase("p") &&
                ((lowerTurn && rowToBePlace == 4)
                        || (!lowerTurn && rowToBePlace == 0))) {
            setWinMessage("Illegal move.");
            endGameFlag = 1;
            return false;
        }

        // Check if the piece is Preview and it raise a check
        if (pieceName.equalsIgnoreCase("p") &&
                ((lowerTurn && gameBoard.getPiece(colToBePlace, rowToBePlace + 1) != null
                        && gameBoard.getPiece(colToBePlace, rowToBePlace + 1).getName().equals("D"))
                        || (!lowerTurn && gameBoard.getPiece(colToBePlace, rowToBePlace - 1) != null
                                && gameBoard.getPiece(colToBePlace, rowToBePlace - 1).getName().equals("d")))) {
            setWinMessage("Illegal move.");
            endGameFlag = 1;
            return false;
        }

        // Check if two preview is in same column
        AbstractMap.SimpleEntry<Integer, Integer> previewPosition = playerStatus.get(lowerTurn).getPiecePosition("p");
        if (previewPosition != null && pieceName.equalsIgnoreCase("p") && colToBePlace == previewPosition.getKey()) {
            setWinMessage("Illegal move.");
            return false;
        }

        // Drop piece on board
        this.gameBoard.placePieceOnBoard(colToBePlace, rowToBePlace, pieceToBeDrop);
        playerStatus.get(lowerTurn).addAPiecePosition(pieceToBeDrop.getName(), colToBePlace, rowToBePlace);

        // Remove from captures
        playerStatus.get(lowerTurn).removeCaptures(pieceToBeDrop.getName());
        return true;
    }

    /**
     * Function that checks is there a piece between two location.
     * 
     * @param col    the col of the position a piece will be moved from
     * @param row    the row of the postition a piece will be moved from
     * @param newCol the new col of the position a piece will be moved to
     * @param newRow the new row of the position a piece will be moved to
     * @return boolean indicating whether there is a piece bewteen two location
     */
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
     * Funtion that returns the Pair representing the col and row.
     * 
     * @param location string in form "[a-zA-Z]\\d" represting location
     * @return the Pair representing the col and row
     */
    private AbstractMap.SimpleEntry<Integer, Integer> parseStringLocationToColRow(String location) {
        if (location.length() == 2) {
            int firstPart = location.charAt(0) - 'a';
            int secondPart = Integer.valueOf(location.substring(1)) - 1;
            if (firstPart < 0 || firstPart > 4 || secondPart < 0 || secondPart > 4) {
                System.out.println("Invalid location in getPieceOnLocation!");
                return null;
            }
            return new AbstractMap.SimpleEntry<>(firstPart, secondPart);
        }
        System.out.println("Invalid location in getPieceOnLocation!");
        return null;
    }

    /**
     * Function that convert integer col, row into string representation in actual
     * borad
     * 
     * @param col integer col index
     * @param row integer row index
     * @return the String representing location
     */
    private String convertPositionToString(int col, int row) {
        String location = String.valueOf((char) (col + (int) 'a')) + String.valueOf(row + 1);
        return location;
    }
}