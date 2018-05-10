public class Driver {
    public static void main(String[] args) {
        ParamParser pp = new ParamParser("b32e25.txt.new");
        try {
            pp.parse_params();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Finished parsing");
    }
}