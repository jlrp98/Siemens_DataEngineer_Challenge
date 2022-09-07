
/**
 * Literal Value packet class
 * Used to encapsulate packets of Literal Value type
 */
public class LiteralValuePacket extends Packet{


    /**
     * Number of bits occupied by header attributes
     */
    protected final int HEADER_SIZE = NR_BITS_PACKET_ID;


    /**
     * Literal Value Packet constructor
     * @param binary Full packet in binary form
     * @param packetTypeId type of packet
     */
    public LiteralValuePacket(String binary, int packetTypeId) {
        this.fullBinary = binary;
        this.packetTypeId = packetTypeId;
        this.data = binary.substring(HEADER_SIZE);
    }

    //************************ GETTER METHODS ************************
    @Override
    public int getHEADER_SIZE() {
        return HEADER_SIZE;
    }


    //************************ METHODS ************************
    /**
     * Decodes and returns payload value of Literal Value Packet
     * @return packet evaluated value
     */
    @Override
    public int decode() {
        PacketDecoder pd = new PacketDecoder();

        return pd.carvePayload(this);
    }
}
