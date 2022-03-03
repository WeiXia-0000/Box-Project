package boxshogi;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Player {
    private boolean lowerTurn;
    private List<String> captures;
    private AbstractMap.SimpleEntry<Integer,Integer> drivePosition;
    private Map<String, AbstractMap.SimpleEntry<Integer,Integer>> piecePosition;
    
    public Player(boolean lowerTurn) {
        this.lowerTurn = lowerTurn;
        this.captures = new LinkedList<>();
        this.piecePosition = new HashMap<>();
    }

    public void setDrivePosition(AbstractMap.SimpleEntry<Integer,Integer> position) {
        drivePosition = position;
    }

    public AbstractMap.SimpleEntry<Integer,Integer> getDrivePosition() {
        return drivePosition;
    }

    public void addCaptures(String capturedPiece) {
        if (lowerTurn) { captures.add(capturedPiece.toLowerCase()); }
        else { captures.add(capturedPiece.toUpperCase()); }
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

    public void addAPiecePosition(String pieceName, int col, int row) {
        piecePosition.put(pieceName, new AbstractMap.SimpleEntry<>(col, row));
    }

    public void removeAPiecePosition(String pieceName) {
        piecePosition.remove(pieceName);
    }

    public AbstractMap.SimpleEntry<Integer,Integer> getPiecePosition(String pieceName) {
        if (lowerTurn) { pieceName = pieceName.toLowerCase(); }
        else { pieceName = pieceName.toUpperCase(); }
        return piecePosition.get(pieceName);
    }
    
}
