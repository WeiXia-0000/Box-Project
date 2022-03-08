package boxshogi;

import java.io.PrintStream;

public class InteractiveMessage {

    /** <---------- Print error message invalid input ----------> **/

    public static String printInvalidInput() {
        return 
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
            "-------------------------------------------------------------";
    }

    public static String printInCheck() {
        return
            "-------------------------------------------------------------\n" +
            "Since you are currently in check, you can only select actions\n" +
            "from your available moves list\n" +
            "\n" +
            "Please try make your selection again.\n" +
            "\n" +
            "You can also exit the game by typing: \"exit\"\n" +
            "-------------------------------------------------------------";
    }

    /** <---------- Print error message related to move ----------> **/

    public static String printMoveNoPiece() {
        return
            "-------------------------------------------------------------\n" +
            "There is no piece in given position!\n" +
            "\n" +
            "Please try a valid input again.\n" +
            "\n" +
            "You can also exit the game by typing: \"exit\"\n" +
            "-------------------------------------------------------------";
    }

    public static String printMoveOthersPiece() {
        return
            "-------------------------------------------------------------\n" +
            "You can not move your opponent's piece!\n" +
            "\n" +
            "Please try another action again.\n" +
            "\n" +
            "You can also exit the game by typing: \"exit\"\n" +
            "-------------------------------------------------------------";
    }

    public static String printCaptureOwnPiece() {
        return
            "-------------------------------------------------------------\n" +
            "You can not capture your own piece!\n" +
            "\n" +
            "Please try another action again.\n" +
            "\n" +
            "You can also exit the game by typing: \"exit\"\n" +
            "-------------------------------------------------------------";
    }

    public static String printCheckAfterMove() {
        return
            "-------------------------------------------------------------\n" +
            "You can not move your drive into check!\n" +
            "\n" +
            "Please try another action again.\n" +
            "\n" +
            "You can also exit the game by typing: \"exit\"\n" +
            "-------------------------------------------------------------";
    }

    public static String printCannotMove() {
        return
            "-------------------------------------------------------------\n" +
            "You can not move your piece to that position!\n" +
            "\n" +
            "Is either your move does not follow the rules of that piece\n" +
            "Or there is a piece in between your piece and your destination.\n" +
            "\n" +
            "Please try another action again.\n" +
            "\n" +
            "You can also exit the game by typing: \"exit\"\n" +
            "-------------------------------------------------------------";
    }

    /** <---------- Print error message related to drop ----------> **/

    public static String printNoPieceInCapture() {
        return
            "-------------------------------------------------------------\n" +
            "The piece you want to drop is not in your capture!\n" +
            "\n" +
            "Please try another action again.\n" +
            "\n" +
            "You can also exit the game by typing: \"exit\"\n" +
            "-------------------------------------------------------------";
    }

    public static String printDropOnAPiece() {
        return
            "-------------------------------------------------------------\n" +
            "You cannot drop your piece on the position of another piece!\n" +
            "\n" +
            "Please try another action again.\n" +
            "\n" +
            "You can also exit the game by typing: \"exit\"\n" +
            "-------------------------------------------------------------";
    }

    public static String printDropPreviewOnPromotion() {
        return
            "-------------------------------------------------------------\n" +
            "You cannot drop your previe on the promotion zone!\n" +
            "\n" +
            "Please try another action again.\n" +
            "\n" +
            "You can also exit the game by typing: \"exit\"\n" +
            "-------------------------------------------------------------";
    }

    public static String printDropTwoPreviewInSameColumn() {
        return
            "-------------------------------------------------------------\n" +
            "You cannot drop your preview here,  since you already have a \n" +
            "preview in the same column. \n" +
            "\n" +
            "Please try another action again.\n" +
            "\n" +
            "You can also exit the game by typing: \"exit\"\n" +
            "-------------------------------------------------------------";
    }

    public static String printDropPreviewCauseCheckMate() {
        return
            "-------------------------------------------------------------\n" +
            "You cannot drop your preview on a squre that results in \n" +
            "an immediate checkmate. \n" +
            "\n" +
            "Please try another action again.\n" +
            "\n" +
            "You can also exit the game by typing: \"exit\"\n" +
            "-------------------------------------------------------------";
    }
}

