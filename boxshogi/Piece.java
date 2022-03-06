package boxshogi;

public class Piece {

    /** Private attributes*/
    private String name;
    private String rule;
    private boolean isLower;
    private boolean promoted;
    
    /** Constructor */
    public Piece(String name, boolean isLower) {
        this.isLower = isLower;
        this.promoted = false;
        // Ensure name is in correct case.
        if (this.isLower) { this.name = name.toLowerCase(); }
        else { this.name = name.toUpperCase(); }
        if (name.length() == 1) {
            this.rule = name;
        } else {
            promotedPiece();
        }
    }

    /** <---------- Class getter ----------> **/

    public String getName() {
        return name;
    }

    public String getRule() {
        return rule;
    }

    public boolean getIsLower() {
        return isLower;
    }

    public boolean getIsPromoted() {
        return promoted;
    }

    /**
     * Function that check is given move on current piece follows the basic rule.
     * @param dCol the col distance/direction the piece woule be move to
     * @param dRow the row distance/direction the piece woule be move to
     * @return a boolean - true means the move follows basic rule; false otherwise
     */
    public boolean checkMoveFollowBasicRule(int dCol, int dRow) {
        // Check basic rule when it is piece a box drive
        if (rule.equalsIgnoreCase("d")) {
            if (Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1) {
                return true;
            }
            return false;
        }

        // Check basic rule when it is piece a box note.
        if (rule.equalsIgnoreCase("n")) {
            if (dCol == 0 || dRow == 0) {
                return true;
            }
            return false;
        }

        // Check basic rule when it is piece a box governance.
        if (rule.equalsIgnoreCase("g")) {
            if (Math.abs(dCol) == Math.abs(dRow)) {
                return true;
            }
            return false;
        }

        // Check basic rule when it is piece a box shield
        if (rule.equalsIgnoreCase("s")) {
            if ((Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1)) {
                if (dCol != 0 && ((isLower && dRow == -1)
                        || (!isLower && dRow == 1))) {
                    return false;
                }
                return true;
            }
            return false;
        }

        // Check basic rule when it is piece a box relay.
        if (rule.equalsIgnoreCase("r")) {
            if ((Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1)) {
                if (dRow == 0 && dCol != 0) {
                    return false;
                }
                if (dCol == 0 && ((isLower && dRow == -1)
                        || (!isLower && dRow == 1))) {
                    return false;
                }
                return true;
            }
            return false;
        }

        // Check basic rule when it is piece a box preview.
        if (rule.equalsIgnoreCase("p")) {
            if (dCol == 0 && ((isLower && dRow == 1) || (!isLower && dRow == -1))) {
                return true;
            }
            return false;
        }

        // Check basic rule when it is piece a upgraded box governance.
        if (rule.equalsIgnoreCase("gd")) {
            if ((Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1) 
                    || (Math.abs(dCol) == Math.abs(dRow))) {
                return true;
            }
            return false;
        }

        // Check basic rule when it is piece a upgraded box note.
        if (rule.equalsIgnoreCase("nd")) {
            if ((Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1) 
                    || (dCol == 0 || dRow == 0)) {
                return true;
            }
            return false;
        }

        // Logically, this return statement would not be reached.
        System.out.println("Not sure what happened, but return false at the end of checkMoveFollowBasicRuleIn Piece class has been reached.");
        return false;
    }

    /**
     * Function to promote a piece, more specifically, upgrade its rule.
     */
    public void promotedPiece() {
        // Changed their rule based on current rule.
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
                return;
        }

        // Append "+" after upgrade.
        if (this.name.length() == 1) {
            this.name = "+" + this.name;
        }

        // Set promoted to be true
        this.promoted = true;
    }
}
