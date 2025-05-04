package edu.grinnell.csc207.compression;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally take
 * 8 bits. However, we also need to encode a special EOF character to denote the
 * end of a .grin file. Thus, we need 9 bits to store each byte value. This is
 * fine for file writing (modulo the need to write in byte chunks to the file),
 * but Java does not have a 9-bit data type. Instead, we use the next larger
 * primitive integral type, short, to store our byte values.
 */
public class HuffmanTree {

    public class Node {

        private int sumFreq;
        private short val;
        private Node left;
        private Node right;

        public Node(short val, int sf, Node left, Node right) {
            this.sumFreq = sf;
            this.val = val;
            this.left = left;
            this.right = right;
        }

        public Node(int sf, Node left, Node right) {
            this.sumFreq = sf;
            this.left = left;
            this.right = right;
        }

        public Node(Node left, Node right) {
            this.sumFreq = left.sumFreq() + right.sumFreq();
            this.left = left;
            this.right = right;
        }

        public short value() {
            return this.val;
        }

        public Node left() {
            return this.left;
        }

        public Node right() {
            return this.right;
        }

        public int sumFreq() {
            return this.sumFreq;
        }

        public boolean isLeaf() {
            return (this.left == null && this.right == null);
        }
    }

    private Node root;

    /**
     * Constructs a new HuffmanTree from a frequency map.
     *
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree(Map<Short, Integer> freqs) {
        Comparator<Node> comparator = (Node a, Node b) -> Integer.compare(a.sumFreq(), b.sumFreq());
        PriorityQueue<Node> pq = new PriorityQueue<>(freqs.size(), comparator);
        freqs.put((short) 256, 1);
        for (Map.Entry<Short, Integer> map : freqs.entrySet()) {
            Node n = new Node(map.getKey(), map.getValue(), null, null);
            pq.add(n);
        }
        while (pq.size() >= 2) {
            Node fst = pq.remove();
            Node snd = pq.remove();
            Node newNode = new Node(fst.sumFreq() + snd.sumFreq(), fst, snd);
            pq.add(newNode);
        }
        this.root = pq.remove();
    }

    /**
     * Return the node as the root of the tree.
     *
     * @param in
     * @return A node as the root of the tree.
     */
    private Node HuffmanTreeHelper(BitInputStream in) {
        Node result = new Node(null, null);
        int bit = in.readBit();
        if (bit == 0) {
            short val = (short) in.readBits(9);
            return new Node(val, null, null);
        }
        if (bit == 1) {
            result = new Node(HuffmanTreeHelper(in), HuffmanTreeHelper(in));
        }
        in.close();
        return result;
    }

    /**
     * Constructs a new HuffmanTree from the given file.
     *
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree(BitInputStream in) {
        this.root = HuffmanTreeHelper(in);
        in.close();
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     *
     * @param out the output file as a BitOutputStream
     */
    public void serialize(BitOutputStream out) {
        Node hold = this.root;
        if (hold.isLeaf()) {
            out.writeBit(0);
            out.writeBits((byte) hold.val, 9);
        } else {
            out.writeBit(1);
        }
    }

    /**
     * Encodes the file given as a stream of bits into a compressed format using
     * this Huffman tree. The encoded values are written, bit-by-bit to the
     * given BitOuputStream.
     *
     * @param in the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode(BitInputStream in, BitOutputStream out) {
        HuffmanTree tree = new HuffmanTree(in);
        tree.serialize(out);
        in.close();
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of bits
     * into their uncompressed form, saving the results to the given output
     * stream. Note that the EOF character is not written to out because it is
     * not a valid 8-bit chunk (it is 9 bits).
     *
     * @param in the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode(BitInputStream in, BitOutputStream out) {

        decodeHelper(in, out, this.root);
    }

    /**
     * Decode file in to out starting from Node n of the tree.
     *
     * @param in
     * @param out
     * @param n
     */
    public void decodeHelper(BitInputStream in, BitOutputStream out, Node n) {
        Node hold = n;
        int bit = in.readBit();

        while (bit != 256 || bit != -1) {

            if (hold.isLeaf()) {
                out.writeBits((short) hold.value(), 8);
            }

            if (bit == 1) {
                hold = hold.left();
                decodeHelper(in, out, hold);
            } else {
                hold = hold.right();
                decodeHelper(in, out, hold);
            }

        }
    }

    /**
     * Return the root of the tree.
     *
     * @return the root.
     */
    public Node getRoot() {
        return this.root;
    }
}
