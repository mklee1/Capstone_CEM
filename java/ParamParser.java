public class ParamParser {

    private int[][][][] param11 = [32][1][3][3];
    private int[][] param21 = [32][1];
    private int[][] param15 = [7200][10];
    private int[][] param25 = [1][10];

    public ParamParser() {

    }

    public int[][][][] getParam11() { return param11; }
    public int[][] getParam21() { return param21; }
    public int[][] getParam15() { return param15; }
    public int[][] getParam25() { return param25; }

    public void parse_params(String filename) {
        filename = "b32e25.txt.new";
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        String line == null;
        int pcount = -1;
        String line2 == null;
        String line3 == null;
        int arrayCounter = 0;

        while ( (line = br.readline()) != null ) {
            // determine if we have switched to a new param
            if (line.equals("\n")) {
                continue;
            }
            else if (line.startsWith("Param 1 for Layer 1")) {
                pcount = 11;
                arrayCounter = 0;
                System.out.println("Param 1 Layer 1");
                continue;
            }
            else if (line.startsWith("Param 2 for Layer 1")) {
                pcount = 21;
                arrayCounter = 0;
                System.out.println("Param 2 Layer 1");
                continue;
            }
            else if (line.startsWith("Param 1 for Layer 5")) {
                pcount = 15;
                arrayCounter = 0;
                System.out.println("Param 1 Layer 5");
                continue;
            }
            else if (line.startsWith("Param 2 for Layer 5")) {
                pcount = 25;
                arrayCounter = 0;
                System.out.println("Param 2 Layer 5");
                continue;
            }

            // continue parsing line
            switch(pcount) {
                case 11:
                    line2 = br.readline();
                    line3 = br.readline();

                    break;
                case 21:
                    break;
                case 15:
                    break;
                case 25:
                    break;
            }
        }
    }
}