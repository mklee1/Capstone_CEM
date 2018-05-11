import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Driver {
    public static void main(String[] args) {
        ParamParser pp = new ParamParser();
        try {
        	pp.parse_params();
        	System.out.println("hi");
        	Process p = Runtime.getRuntime().exec("python condensed.py");
        	BufferedReader stdInput = new BufferedReader(new 
                    InputStreamReader(p.getInputStream()));

               BufferedReader stdError = new BufferedReader(new 
                    InputStreamReader(p.getErrorStream()));
        	String s = null;
        	System.out.println("Here is the standard output of the command:\n");
        	String result = "";
            while ((s = stdInput.readLine()) != null) {
            	result = result + s.substring(1,2);
            }
            System.out.println(result);
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        	System.out.println("bye");
        }
        
        catch (Exception e) {
        	e.printStackTrace();
        }
        System.out.println("Finished parsing");
    }
}