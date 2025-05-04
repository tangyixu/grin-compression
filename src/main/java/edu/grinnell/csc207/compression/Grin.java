package edu.grinnell.csc207.compression;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The driver for the Grin compression program.
 */
public class Grin {

    /**
     * Decodes the .grin file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     *
     * @param infile the file to decode
     * @param outfile the file to ouptut to
     * @throws java.io.IOException
     */
    public static void decode(String infile, String outfile) throws IOException {
        HuffmanTree tree;
        tree = new HuffmanTree(createFrequencyMap(infile));
        tree.decode(new BitInputStream(infile), new BitOutputStream(outfile));
    }

    /**
     * Creates a mapping from 8-bit sequences to number-of-occurrences of those
     * sequences in the given file.To do this, read the file using a
     * BitInputStream, consuming 8 bits at a time.
     *
     * @param file the file to read
     * @return a freqency map for the given file
     * @throws java.io.IOException
     */
    public static Map<Short, Integer> createFrequencyMap(String file) throws IOException {
        Map<Short, Integer> map = new HashMap<>();
        BitInputStream in = new BitInputStream(file);
        int bits = (short) in.readBits(8);
        short val = (short) bits;
        while (val != 256) {
            map.put(val, map.getOrDefault(val, 0) + 1);
            bits = in.readBits(8);
        }
        in.close();
        return map;
    }

    /**
     * Encodes the given file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     *
     * @param infile the file to encode.
     * @param outfile the file to write the output to.
     */
    public static void encode(String infile, String outfile) throws IOException {
        HuffmanTree tree;
        tree = new HuffmanTree(createFrequencyMap(infile));
        tree.encode(new BitInputStream(infile), new BitOutputStream(outfile));
    }

    /**
     * The entry point to the program.
     *
     * @param args the command-line arguments.
     */
    public static void main(String[] args) {
        String command = args[0];
        System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");
        switch (command) {
            case "encode" -> {
                try {
                    encode(args[1], args[2]);
                } catch (IOException ex) {
                }
            }

            case "decode" -> {
                try {
                    decode(args[1], args[2]);
                } catch (IOException ex) {

                }
            }

            default ->
                System.out.println("Invalid command. Enter either encode or decode.");
        }

    }

}
