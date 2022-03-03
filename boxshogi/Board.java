package boxshogi;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent Box Shogi board
 */
public class Board {

    private final int BOARD_SIZE = 5;
    private final String[] ALL_POSSIBLE_PIECES = { "n", "g", "r", "s", "d", "p" };

    private Piece[][] board;
    private Map<Boolean, Player> playerStatus;

    public Board(boolean empty) {
        // Initial borad.
        this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
        this.playerStatus = new HashMap<>();
        this.playerStatus.put(true, new Player(true));
        this.playerStatus.put(false, new Player(false));

        // IF we need an empty board
        if (empty) {
            return;
        }

        // Initial the first and last row.
        for (int eachCol = 0; eachCol < BOARD_SIZE; eachCol++) {
            String pieceName = ALL_POSSIBLE_PIECES[eachCol];
            this.board[BOARD_SIZE - 1 - eachCol][0] = new Piece(pieceName, true);
            this.board[eachCol][BOARD_SIZE - 1] = new Piece(pieceName, false);
            // Record pieces' positions
            this.playerStatus.get(true).addAPiecePosition(pieceName, BOARD_SIZE - 1 - eachCol, 0);
            this.playerStatus.get(false).addAPiecePosition(pieceName.toUpperCase(), eachCol, BOARD_SIZE - 1);
            // Record drive's position it self
            if (pieceName.equalsIgnoreCase("d")) {
                this.playerStatus.get(true)
                        .setDrivePosition(new AbstractMap.SimpleEntry<Integer, Integer>(BOARD_SIZE - 1 - eachCol, 0));
                this.playerStatus.get(false)
                        .setDrivePosition(new AbstractMap.SimpleEntry<Integer, Integer>(eachCol, BOARD_SIZE - 1));
            }

        }

        // Initial preview.
        String pieceName = ALL_POSSIBLE_PIECES[BOARD_SIZE];
        this.board[0][1] = new Piece(pieceName, true);
        this.board[BOARD_SIZE - 1][BOARD_SIZE - 2] = new Piece(pieceName, false);
        this.playerStatus.get(true).addAPiecePosition(pieceName, 0, 1);
        this.playerStatus.get(false).addAPiecePosition(pieceName.toUpperCase(), BOARD_SIZE - 1, BOARD_SIZE - 2);
    }

    /* Print board */
    public String toString() {
        String[][] pieces = new String[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece curr = (Piece) board[col][row];
                pieces[col][row] = this.isOccupied(col, row) ? board[col][row].getName() : "";
            }
        }
        return stringifyBoard(pieces);
    }

    /**
     * PlayerStatusMap getter.
     * 
     * @return the map storing two players status
     */
    public Map<Boolean, Player> getPlayerStatus() {
        return playerStatus;
    }

    /**
     * Function that return the piece on given position.
     * 
     * @param col integer representing the col
     * @param row integer representing the row
     * @return the Piece on given position
     */
    public Piece getPiece(int col, int row) {
        if (col < 0 || col > 4 || row < 0 || row > 4) {
            return null;
        }
        return board[col][row];
    }

    /**
     * Function that places a piece on given position.
     * 
     * @param col   integer representing the col
     * @param row   integer representing the row
     * @param piece the piece to place
     */
    public void placePieceOnBoard(int col, int row, Piece piece) {
        this.board[col][row] = piece;
    }

    /**
     * Function that remove a piece from given position.
     * 
     * @param col integer representing the col
     * @param row integer representing the row
     */
    public void removePieceFromBoard(int col, int row) {
        this.board[col][row] = null;
    }

    /**
     * Function that return whether given position is occupied by a piece.
     * 
     * @param col integer representing the col
     * @param row integer representing the roe
     * @return whether given position is occupied by a piece
     */
    private boolean isOccupied(int col, int row) {
        return board[col][row] != null;
    }

    /**
     * Funtion that builds the string for our board.
     * 
     * @param board a two dimension array representing the board
     * @return the String representin the board
     */
    private String stringifyBoard(String[][] board) {
        String str = "";

        for (int row = board.length - 1; row >= 0; row--) {

            str += Integer.toString(row + 1) + " |";
            for (int col = 0; col < board[row].length; col++) {
                str += stringifySquare(board[col][row]);
            }
            str += System.getProperty("line.separator");
        }

        str += "    a  b  c  d  e" + System.getProperty("line.separator");

        return str;
    }

    /**
     * Function that builds the string on each board from a string.
     * 
     * @param sq String for each piece
     * @return the String should be shown on board
     */
    private String stringifySquare(String sq) {
        switch (sq.length()) {
            case 0:
                return "__|";
            case 1:
                return " " + sq + "|";
            case 2:
                return sq + "|";
        }
        throw new IllegalArgumentException("Board must be an array of strings like \"\", \"P\", or \"+P\"");
    }
}
