package edu.grinnell.csc207.compression;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class Tests {

    @Test
    public void generateFreqMap() throws IOException {
        Map<Short, Integer> map = Grin.createFrequencyMap("sample.txt");
        assertEquals(1, map.get((short) 'I'));
        assertEquals(2, map.get((short) 'o'));
        assertEquals(3, map.get((short) ' '));
    }

    @Test
    public void encodeAndDecode() throws IOException {
        Grin.encode("sample.txt", "result.txt");
        Grin.decode("result.txt", "result.txt");
        Map<Short, Integer> map = Grin.createFrequencyMap("result.txt");
        assertEquals(1, map.get((short) 'I'));
        assertEquals(2, map.get((short) 'o'));
        assertEquals(3, map.get((short) ' '));
    }

    @Test
    public void createHuffmanTree() {
        Map<Short, Integer> freqs = new HashMap<>();
        freqs.put((short) 32, 1);
        freqs.put((short) 85, 6);
        freqs.put((short) 210, 10);
        HuffmanTree tree = new HuffmanTree(freqs);
        assertEquals(17, tree.getRoot().sumFreq());
    }
}
