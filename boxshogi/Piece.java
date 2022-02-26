package boxshogi;

public class Piece {
    
    private String name;
    private String rule;
    private boolean isUpper;
    private boolean promoted;
    
    public Piece(String name, boolean isUpper) {
        this.isUpper = isUpper;
        this.promoted = false;
        // Ensure name is in correct case.
        if (this.isUpper) { this.name = name.toUpperCase(); }
        else { this.name = name.toLowerCase(); }
        if (name.length() == 1) {
            this.rule = name;
        } else {
            promotedPiece();
        }
    }

    public String getName() {
        return name;
    }

    public String getRule() {
        return rule;
    }

    public boolean getIsUpper() {
        return isUpper;
    }

    public boolean getIsPromoted() {
        return promoted;
    }

    public void promotedPiece() {
        if (this.name.length() == 1) {
            this.name = "+" + this.name;
        }
        this.promoted = true;

        switch (name.toLowerCase().charAt(name.length()-1)) {
            case 'r':
            case 'p':
                this.rule = "s";
                break;
            case 'g':
                this.rule = "gd";
                break;
            case 'n':
                this.rule = "nd";
                break;
            default:
                System.out.print("Something goes wrong in promotedPiece related to piece type.");
        }
    }
}
