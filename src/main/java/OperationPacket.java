import java.util.ArrayList;


/**
 * Operator packet class
 * Used to encapsulate packets of operation type
 */
public class OperationPacket extends Packet {

    /**
     * Number of bits used to store number of sub-packets
     */
    private final int NR_BITS_PACKET_LENGTH = 4;

    /**
     * Enum specifying types of allowed operations
     */
    private enum Operation {
        SUM,
        PRODUCT,
        MIN,
        MAX
    }

    /**
     * Number of bits used to store header attributes
     */
    private final int HEADER_SIZE = NR_BITS_PACKET_ID + NR_BITS_PACKET_LENGTH;

    /**
     * Type of operation
     */
    private Operation operation;

    /**
     * Number of sub-packets in operation packet
     */
    int nrSubPackets;

    /**
     * Arraylist of all the sub-packets
     */
    ArrayList<Packet> subPackets;

    /**
     * Operation packet constructor.
     * Parses header.
     * @param binary Full packet in binary form
     * @param packetTypeId type of packet
     */
    public OperationPacket(String binary, int packetTypeId) {
        //Extract header fields
        this.packetTypeId = packetTypeId;
        String subPacketLengthField = binary.substring(NR_BITS_PACKET_ID, NR_BITS_PACKET_ID +NR_BITS_PACKET_LENGTH);
        this.nrSubPackets = PacketDecoder.binaryToDecimal(subPacketLengthField);


        this.fullBinary = binary;
        this.data = binary.substring(HEADER_SIZE);

        switch (packetTypeId) {
            case 0 -> operation = Operation.SUM;
            case 1 -> operation = Operation.PRODUCT;
            case 2 -> operation = Operation.MIN;
            case 3 -> operation = Operation.MAX;
        }

        //System.out.println("New operation packet with type "+this.packetTypeId+" and "+nrSubPackets+" sub packets." );

    }



    //************************ GETTER METHODS ************************
    public int getNrSubPackets() {
        return nrSubPackets;
    }

    /**
     * Returns header size - is different depending on packet type
     * @return header size
     */
    @Override
    public int getHEADER_SIZE() {
        return HEADER_SIZE;
    }


    //**************************** METHODS ************************

    /**
     * Calculates desired operation with all sub-packets
     *
     * @return evaluated packet
     * @throws InvalidPacketTypeException Invalid type of packet exception
     */
    private int solveOperation() throws InvalidPacketTypeException {

        int result = subPackets.get(0).decode();

        for(int i = 1; i< nrSubPackets; i++) {
            int packetValue = subPackets.get(i).decode();

            switch (operation) {
                case SUM -> result += packetValue;
                case PRODUCT -> result *= packetValue;
                case MIN -> result = Math.min(packetValue, result);
                case MAX -> result = Math.max(packetValue, result);
            }
        }

        return result;
    }

    /**
     * Decodes and returns evaluated value of Operation Packet
     * @return packet evaluated value
     * @throws InvalidPacketTypeException Invalid type of packet exception
     */
    @Override
    public int decode() throws InvalidPacketTypeException {
        PacketDecoder pd = new PacketDecoder();

        this.subPackets = pd.carveConsecutivePackets(this.data, this.nrSubPackets);


        //System.out.println("Decoding operation packet: "+this.data);
        return solveOperation();
    }
}
