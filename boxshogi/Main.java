package boxshogi;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-i")) {
            System.out.println("Must implement interactive mode");
        } else if (args.length == 2 && args[0].equals("-f")) {
            try {
                Utils.TestCase input = Utils.parseTestCase(args[1]);
                String sampleOutput = "UPPER player action: drop s d1\n"
                + "5 |__|__| R|__| D|\n"
                + "4 |__|__|__|__|__|\n"
                + "3 |__|__|__|__|__|\n"
                + "2 |__|__|__|__|__|\n"
                + "1 | d| g|__| n|__|\n"
                + "a  b  c  d  e\n\n"
                + "Captures UPPER: S R P\n"
                + "Captures lower: p n g s\n\n"
                + "lower player wins.  Illegal move.\n";
                System.out.println(sampleOutput);

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } else {
            System.out.println("Please specify input by -i or -f [file name]");
        }
    }
}
