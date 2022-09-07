/**
 * Abstract Packet class from which specific packet classes extend from
 */
abstract class Packet {

    //*******************CONSTANTS*********************
    /**
     * Number of bits used by packet type ID field
     */
    protected final int NR_BITS_PACKET_ID = 3;
    /**
     * Number of bits occupied by header attributes
     */
    protected int HEADER_SIZE;

    //******************ATTRIBUTES**********************

    /**
     * packet type ID
     */
    protected int packetTypeId;


    /**
     * Full packet in binary form
     * Converted from hex input
     */
    protected String fullBinary;

    /**
     * Packet data. Does not including header attributes
     * Contains only raw/uncarved payload
     */
    protected String data; //from bit 4 to last


    //************************ GETTER METHODS ************************
    public int getPacketTypeId() {
        return packetTypeId;
    }

    public String getData() {
        return data;
    }



    //*************************** METHODS ************************
    /**
     * Abstract function for specific Packet classes to Override
     * @return packet evaluated value
     * @throws InvalidPacketTypeException Invalid type of packet exception
     */
    public abstract int decode() throws InvalidPacketTypeException;

    public int getHEADER_SIZE() {
        return HEADER_SIZE;
    }
}
