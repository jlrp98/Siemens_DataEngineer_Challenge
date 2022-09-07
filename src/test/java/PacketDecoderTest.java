import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PacketDecoderTest {

    @Test
    void decodeMessage() {

        PacketDecoder pd1 = new PacketDecoder("97F14");
        assertEquals(2021,pd1.evaluate());

        PacketDecoder pd2 = new PacketDecoder("07030506");
        assertEquals(6,pd2.evaluate());

        PacketDecoder pd3 = new PacketDecoder("27050506");
        assertEquals(12,pd3.evaluate());

        PacketDecoder pd4 = new PacketDecoder("0688941C1328B8A05030306");
        assertEquals(11,pd4.evaluate());

        PacketDecoder pd5 = new PacketDecoder("240A1614142C10");
        assertEquals(70,pd5.evaluate());

        assertAll(() -> assertEquals(2021,pd1.evaluate()),
                  () -> assertEquals(6,pd2.evaluate()),
                  () -> assertEquals(12,pd3.evaluate()),
                  () -> assertEquals(11,pd4.evaluate()),
                  () -> assertEquals(70,pd5.evaluate())

 );



    }
}