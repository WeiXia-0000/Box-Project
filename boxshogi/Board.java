package boxshogi;


/**
 * Class to represent Box Shogi board
 */
public class Board {

    private final int BOARD_SIZE = 5;

    private Piece[][] board;

    public Board(boolean empty) {
        // Initial borad.
        board = new Piece[BOARD_SIZE][BOARD_SIZE];

        // IF we need an empty board
        if (empty) { return; }

    	// Store all the possible strings
        String[] pieces = {"n", "g", "r", "s", "d", "p"};

        // Initial the first and last row.
        for (int eachCol = 0; eachCol < BOARD_SIZE; eachCol++) {
            this.board[eachCol][BOARD_SIZE-1] = new Piece(pieces[eachCol], true);
            this.board[BOARD_SIZE-1-eachCol][0] = new Piece(pieces[eachCol], false);
        }

        // Initial preview.
        this.board[BOARD_SIZE-1][BOARD_SIZE-2] = new Piece(pieces[BOARD_SIZE], true);
        this.board[0][1] = new Piece(pieces[BOARD_SIZE], false);
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
     * Function that return the piece on given position.
     * @param col integer representing the col
     * @param row integer representing the row
     * @return the Piece on given position
     */
    public Piece getPiece(int col, int row) {
        return board[col][row];
    }

    /**
     * Function that places a piece on given position.
     * @param col integer representing the col
     * @param row integer representing the row
     * @param piece the piece to place
     */
    public void placePieceOnBoard(int col, int row, Piece piece) {
        this.board[col][row] = piece;
    }

    /**
     * Function that remove a piece from given position.
     * @param col integer representing the col
     * @param row integer representing the row
     */
    public void removePieceFromBoard(int col, int row) {
        this.board[col][row] = null;
    }


    /**
     * Function that return whether given position is occupied by a piece.
     * @param col integer representing the col
     * @param row integer representing the roe
     * @return whether given position is occupied by a piece
     */
    private boolean isOccupied(int col, int row) {
        return board[col][row] != null;
    }

    /**
     * Funtion that builds the string for our board.
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

