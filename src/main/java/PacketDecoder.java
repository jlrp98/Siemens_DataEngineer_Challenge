import java.util.ArrayList;
import java.util.HashMap;

/**
 * Packet decoding class.
 * Contains relevant utilites and packet parsing logic.
 *
 */
public class PacketDecoder {

    /**
     * Number of bits each payload group occupies
     */
    private final int NR_BITS_PAYLOAD_GROUP = 5;
    private final int LITERAL_VALUE_PACKET_ID = 4;
    private String hex = "";
    private String bin = "";

    /**
     * Constructor method. Receives hex string
     * @param hexInput hexadecimal String
     */
    public PacketDecoder(String hexInput) {
        this.hex = hexInput;
        this.bin = hexToBinary(hex);
    }

    public PacketDecoder() {

    }

    /**
     * Utility method.
     * Convert from hexadecimal to binary.
     * @param hex hexadecimal String
     * @return binary String
     */
    private String hexToBinary(String hex) {
        StringBuilder bin = new StringBuilder();

        //Store binary representation of each hexadecimal value
        HashMap<String, String> hexBinPairs = new HashMap<>();
        hexBinPairs.put("0", "0000");
        hexBinPairs.put("1", "0001");
        hexBinPairs.put("2", "0010");
        hexBinPairs.put("3", "0011");
        hexBinPairs.put("4", "0100");
        hexBinPairs.put("5", "0101");
        hexBinPairs.put("6", "0110");
        hexBinPairs.put("7", "0111");
        hexBinPairs.put("8", "1000");
        hexBinPairs.put("9", "1001");
        hexBinPairs.put("A", "1010");
        hexBinPairs.put("B", "1011");
        hexBinPairs.put("C", "1100");
        hexBinPairs.put("D", "1101");
        hexBinPairs.put("E", "1110");
        hexBinPairs.put("F", "1111");

        //iterate through hexadecimal string index
        //append the binary value of each hexadecimal char to binary string
        for(int i = 0; i< hex.length(); i++) {
            bin.append(hexBinPairs.get("" + hex.charAt(i)));
        }


        return bin.toString();
    }

    /**
     * Utility method.
     * Convert from binary to decimal.
     * @param bin binary String
     * @return int value of binary string
     */
    public static int binaryToDecimal(String bin) {
        //convert from binary to integer
        return Integer.parseInt(bin,2);
    }


    /**
     * convert hexadecimal input to binary.
     * Decodes packet and performs operations if necessary
     * @return packet evaluation
     */
    public int evaluate() {
        if(bin.equals("")) {
            bin = hexToBinary(hex);
        }

        int message;
        try {
            Packet packet = binaryToPacket(bin);
            message = packet.decode();
        } catch (InvalidPacketTypeException e) {
            throw new RuntimeException(e);
        }

        return message;
    }

    /**
     *
     * Encapsulate Packet from binary data
     * @param binary packet in binary form
     * @return encapsulated packet
     * @throws InvalidPacketTypeException Invalid type of packet exception
     */
    public Packet binaryToPacket(String binary) throws InvalidPacketTypeException {
        //first 3 bits indicate packet type ID
        String typeBits =  binary.substring(0,3);

        //get packet type ID
        int packetType = calculatePacketType(typeBits);

        //create new packet according to type ID
        return switch(packetType) {
            case LITERAL_VALUE_PACKET_ID -> new LiteralValuePacket(binary, packetType);
            case 0, 1, 2, 3 -> new OperationPacket(binary, packetType);
            default -> throw new InvalidPacketTypeException();
        };
    }

    /**
     * Carve payload from packets data field
     * Explanation:
     * -payload is segmented in groups
     * -each group is composed of NR_BITS_PAYLOAD_GROUP bits
     * -the first bit indicates if the current group is the last and the 4 last correspond to the payload
     * Format: LPPPP   |   L = 0 if last group, 1 if not    P=payload bit
     * Example: 1100 1001 1010 0110
     *
     * @param packet Literal Value Packet
     * @return carved payload in decimal form
     */
    public int carvePayload(LiteralValuePacket packet) {
        StringBuilder payload = new StringBuilder();

        String payloadBits = packet.getData();
        //System.out.println("Decoding: "+packet.getFullBinary());

        for(int i = 0; i < payloadBits.length() - NR_BITS_PAYLOAD_GROUP + 1; i+= NR_BITS_PAYLOAD_GROUP) {

            //segment bits into groups by dividing them in groups of PAYLOAD_GROUP_SIZE
            String group = payloadBits.substring(i,i + NR_BITS_PAYLOAD_GROUP);

            //"first bit indicates if the current group is the last"
            boolean isEndOfPacket = group.charAt(0) == '0';

            //append rest of group data bits to payload
            payload.append(group.substring(1));


            if(isEndOfPacket) break;
        }


        //System.out.println("Extracted payload: "+payload.toString());
        return binaryToDecimal(payload.toString());
    }

    /**
     *
     * extracts consecutive sub-packets from an operation packet
     *
     * @param consecutivePacketsBinary binary string with all sub-packets
     * @param nrPackets number of sub-packets in main packet
     * @return  ArrayList with all sub-packets
     * @throws InvalidPacketTypeException Invalid type of packet exception
     */
    public ArrayList<Packet> carveConsecutivePackets(String consecutivePacketsBinary, int nrPackets) throws InvalidPacketTypeException {

        ArrayList<Packet> packets = new ArrayList<>();

        //System.out.println("packetsBinary: "+consecutivePacketsBinary);

        //auxiliar var to delimit where a packet is placed in consecutivePacketsBinary
        int offset = 0;

        for(int i = 0; i<nrPackets; i++) {

            //finds packet length of first packet placed in consecutivePacketsBinary
            int packetLength = findPacketLength(consecutivePacketsBinary.substring(offset));

            Packet newPacket = binaryToPacket(consecutivePacketsBinary.substring(offset, offset + packetLength));
            packets.add(newPacket);

            offset = offset + packetLength;

        }

        return packets;
    }

    /**
     * Find length of a packets data including group bits
     * @param payloadBinary packet data in binary form
     * @return length of packet data including group bits
     */
    private int findUncarvedPayloadLength(String payloadBinary) {

        int payloadLength;


        boolean isLastGroup = false;
        int groupCount = 0;
        int offset;

        while(!isLastGroup) {

            offset = groupCount * NR_BITS_PAYLOAD_GROUP;
            String payloadGroup = payloadBinary.substring(offset, offset + NR_BITS_PAYLOAD_GROUP);

            //check if its last one
            if (payloadGroup.charAt(0) == '0') isLastGroup = true;


            groupCount++;
        }
        payloadLength = groupCount * NR_BITS_PAYLOAD_GROUP;

        //System.out.println("Payload length: "+payloadLength);

        return payloadLength;
    }

    /**
     * Find length of the first packet in consecutivePacketsBinary recursively
     * Note: length includes nested packets
     * @param consecutivePacketsBinary consecutive packets in binary form
     * @return length of first packet in consecutivePacketsBinary
     * @throws InvalidPacketTypeException Invalid type of packet exception
     */
    private int findPacketLength(String consecutivePacketsBinary) throws InvalidPacketTypeException {

        int length = 0;

        //get next packet. Only the header is relevant here
        Packet nextPacket = binaryToPacket(consecutivePacketsBinary);

        if (nextPacket.getPacketTypeId() == LITERAL_VALUE_PACKET_ID) {
            //if its a literal value packet the length is equals to header_size + data_size
            length = nextPacket.getHEADER_SIZE() + findUncarvedPayloadLength(nextPacket.getData());
        }else {
            //if its an operation packet we need to check for nested packets in all sub-packets
            //length will be the header_size + what the recursion returns

            int nrSubPackets = ((OperationPacket) nextPacket).getNrSubPackets();
            int HEADER_SIZE = nextPacket.getHEADER_SIZE();

            length += HEADER_SIZE;

            for(int i = 0; i<nrSubPackets;i++) {
                length += findPacketLength(consecutivePacketsBinary.substring(length));
            }

        }
        return length;
    }

    /**
     * @param seq binary input sequence
     * @return int referring to packet type ID
     */
    private int calculatePacketType(String seq) {
        return binaryToDecimal(seq.substring(0,3));
    }

    @Override
    public String toString() {
        return "MessageDecoder{" +
                "hex='" + hex + '\'' +
                ", bin='" + bin + '\'' +
                '}';
    }


}
