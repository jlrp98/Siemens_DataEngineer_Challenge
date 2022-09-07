public class Challenge {

    public static void main(String[] args) {

        //input argument should be hexadecimal string
        System.out.println("Input: "+args[0]);

        //Create MessageDecoder object
        PacketDecoder pd = new PacketDecoder(args[0]);


        System.out.println("Output: "+pd.evaluate());



    }

}
