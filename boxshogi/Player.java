package boxshogi;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Player {
    
    /** Private attributes*/
    private String playerName;
    private boolean lowerTurn;
    private List<String> captures;
    private AbstractMap.SimpleEntry<Integer, Integer> drivePosition;
    private Map<String, AbstractMap.SimpleEntry<Integer, Integer>> piecePosition;

    /** Constructor */
    public Player(boolean lowerTurn) {
        if (lowerTurn) { this.playerName = "lower"; }
        else { this.playerName = "UPPER"; }
        this.lowerTurn = lowerTurn;
        this.captures = new LinkedList<>();
        this.piecePosition = new HashMap<>();
    }

    /** <-------------------- Player names related operations --------------------> **/

    public String getPlayerName() {
        return playerName;
    }

    /** <-------------------- Drive position related operations --------------------> **/

    public void setDrivePosition(AbstractMap.SimpleEntry<Integer, Integer> position) {
        drivePosition = position;
    }

    public AbstractMap.SimpleEntry<Integer, Integer> getDrivePosition() {
        return drivePosition;
    }

    /** <-------------------- Captures related operations --------------------> **/

    public void addCaptures(String capturedPiece) {
        if (lowerTurn) {
            captures.add(capturedPiece.toLowerCase());
        } else {
            captures.add(capturedPiece.toUpperCase());
        }
    }

    public void removeCaptures(String capturedPiece) {
        captures.remove(capturedPiece);
    }

    public void containCaptures(String capturedPiece) {
        captures.remove(capturedPiece);
    }

    public void setCaptures(List<String> capTures) {
        captures = new LinkedList<>(capTures);
    }

    public List<String> getCaptures() {
        return captures;
    }


    /** <-------------------- Piece position related operations --------------------> **/
    
    public void addAPiecePosition(String pieceName, int col, int row) {
        piecePosition.put(pieceName, new AbstractMap.SimpleEntry<>(col, row));
    }

    public void removeAPiecePosition(String pieceName) {
        piecePosition.remove(pieceName);
    }

    public AbstractMap.SimpleEntry<Integer, Integer> getPiecePosition(String pieceName) {
        if (lowerTurn) {
            pieceName = pieceName.toLowerCase();
        } else {
            pieceName = pieceName.toUpperCase();
        }
        return piecePosition.get(pieceName);
    }

    public Map<String, AbstractMap.SimpleEntry<Integer, Integer>> getPiecePosition() {
        return piecePosition;
    }

}
