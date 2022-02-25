package boxshogi;

public class Piece {
    
    private String name;
    private boolean isUpper;

    public Piece(String name, boolean isUpper) {
        this.isUpper = isUpper;
        // Ensure name is in correct case.
        if (this.isUpper) { this.name = name.toUpperCase(); }
        else { this.name = name.toLowerCase(); }
    }

    public String getName() {
        return name;
    }

    public boolean getIsUpper() {
        return isUpper;
    }
}
