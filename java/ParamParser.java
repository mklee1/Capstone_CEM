import java.lang.String;
import java.lang.Double;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class ParamParser {

    private double[][][][] param11 = new double[32][1][3][3];
    private double[][] param21 = new double[32][1];
    private double[][] param15 = new double[7200][10];
    private double[][] param25 = new double[1][10];
    private String filename = "b32e25.txt.new";

    public ParamParser(String name) {
        filename = name;
    }

    public double[][][][] getParam11() { return param11; }
    public double[][] getParam21() { return param21; }
    public double[][] getParam15() { return param15; }
    public double[][] getParam25() { return param25; }

    public void parse_params () throws IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        int pcount = -1;
        String line2 = null;
        String line3 = null;
        int arrayCounter = 0;
        while ( (line = br.readLine()) != null ) {
            // determine if we have switched to a new param
            if (line.equals("")) {
                continue;
            }
            else if (line.startsWith("Param 1 for Layer 1")) {
                pcount = 11;
                arrayCounter = 0;
                continue;
            }
            else if (line.startsWith("Param 2 for Layer 1")) {
                pcount = 21;
                arrayCounter = 0;
                continue;
            }
            else if (line.startsWith("Param 1 for Layer 5")) {
                pcount = 15;
                arrayCounter = 0;
                continue;
            }
            else if (line.startsWith("Param 2 for Layer 5")) {
                pcount = 25;
                arrayCounter = 0;
                continue;
            }
            // continue parsing line

            switch(pcount) {
                case 11:
                    line2 = br.readLine();
                    line3 = br.readLine();
                    case11(line, line2, line3, arrayCounter);
                    arrayCounter++;
                    break;
                case 21:
                    case21(line, arrayCounter);
                    arrayCounter++;
                    break;
                case 15:
                    line2 = br.readLine();
                    line3 = br.readLine();
                    case15(line, line2, line3, arrayCounter);
                    arrayCounter++;
                    break;
                case 25:
                    case25(line, arrayCounter);
                    arrayCounter++;
                    break;
            }
        }
    }
    public void case11(String line, String line2, String line3, int ac) {
        int firstComma = line.indexOf(",");
        int secondComma = line.indexOf(",", firstComma+1);
        param11[ac][0][0][0] = Double.parseDouble(line.substring(1,firstComma));
        param11[ac][0][0][1] = Double.parseDouble(line.substring(firstComma+1,secondComma));
        param11[ac][0][0][2] = Double.parseDouble(line.substring(secondComma+1,line.length()-1));

        firstComma = line2.indexOf(",");
        secondComma = line2.indexOf(",", firstComma+1);
        param11[ac][0][1][0] = Double.parseDouble(line2.substring(0,firstComma));
        param11[ac][0][1][1] = Double.parseDouble(line2.substring(firstComma+1,secondComma));
        param11[ac][0][1][2] = Double.parseDouble(line2.substring(secondComma+1,line2.length()-1));

        firstComma = line3.indexOf(",");
        secondComma = line3.indexOf(",", firstComma+1);
        param11[ac][0][2][0] = Double.parseDouble(line3.substring(0,firstComma));
        param11[ac][0][2][1] = Double.parseDouble(line3.substring(firstComma+1,secondComma));
        param11[ac][0][2][2] = Double.parseDouble(line3.substring(secondComma+1,line3.length()-3));
    }
    public void case21(String line, int ac) {
        param21[ac][0] = Double.parseDouble(line.substring(1,line.length()-1));
    }
    public void case15(String line, String line2, String line3, int ac) {
        int firstComma = line.indexOf(",");
        int secondComma = line.indexOf(",", firstComma+1);
        int thirdComma = line.indexOf(",", secondComma+1);
        param15[ac][0] = Double.parseDouble(line.substring(3,firstComma));
        param15[ac][1] = Double.parseDouble(line.substring(firstComma+1,secondComma));
        param15[ac][2] = Double.parseDouble(line.substring(secondComma+1,thirdComma));
        param15[ac][3] = Double.parseDouble(line.substring(thirdComma+1,line.length()-1));

        firstComma = line2.indexOf(",");
        secondComma = line2.indexOf(",", firstComma+1);
        thirdComma = line2.indexOf(",", secondComma+1);
        param15[ac][4] = Double.parseDouble(line2.substring(1,firstComma));
        param15[ac][5] = Double.parseDouble(line2.substring(firstComma+1,secondComma));
        param15[ac][6] = Double.parseDouble(line2.substring(secondComma+1,thirdComma));
        param15[ac][7] = Double.parseDouble(line2.substring(thirdComma+1,line2.length()-1));

        firstComma = line3.indexOf(",");
        param15[ac][8] = Double.parseDouble(line3.substring(1,firstComma));
        param15[ac][9] = Double.parseDouble(line3.substring(firstComma+1,line3.length()-1));
    }
    public void case25(String line, int ac) {
        int nextComma;
        int comma = line.indexOf(",");
        param25[0][0] = Double.parseDouble(line.substring(1,comma));
        int ind = 1;
        while ((nextComma = line.indexOf(",", comma+1)) != -1) {
            param25[0][ind] = Double.parseDouble(line.substring(comma+1, nextComma));
            comma = nextComma;
            ind++;
        }
        param25[0][ind] = Double.parseDouble(line.substring(comma+1, line.length()-1));
    }

}
