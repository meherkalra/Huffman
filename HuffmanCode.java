/**
 * Huffman Encoding
 * @author Meher Kalra and Neo Cai
 * PS-3, Spring 2022, Dartmouth CS-10
 */

import java.io.*;
import java.util.*;

public class HuffmanCode {
    // instance variable
    public static BinaryTree<CharFreq> t;

    /**
     * CharFreq class that stores the character and frequency
     */
    private static class CharFreq {
        public Character character;
        public int frequency;

        public CharFreq(Character c, int f) {
            this.character = c;
            this.frequency = f;
        }

        public CharFreq(int f) {
            this.frequency = f;
        }

        @Override
        public String toString() {
            String stringC = character.toString();
            return stringC;
        }

        public char getCharacter() {
            return character;
        }
    }

    /**
     * Class to create a comparator for charFreq binary trees. Constructor takes in two trees and compares them.
     */
    public static class TreeComparator implements Comparator<BinaryTree<CharFreq>> {
        @Override
        public int compare(BinaryTree<CharFreq> t1, BinaryTree<CharFreq> t2) {
            int f1 = t1.getData().frequency;
            int f2 = t2.getData().frequency;
            if (f1 > f2) return 1;
            else if (f2 > f1) return -1;
            else return 0;
        }
    }

    /**
     * Maps the frequency of characters in a string
     * @param pathName
     * @return charCode
     * @throws IOException
     */
    public static ArrayList<Character> charCode(String pathName) throws IOException {
        ArrayList<Character> charCode = new ArrayList<Character>();
        BufferedReader input = new BufferedReader(new FileReader(pathName));
        try {
            int codeInt = input.read();
            while (codeInt != -1) {
                char character = (char) codeInt;
                charCode.add(character);
                codeInt = input.read();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return charCode;
    }

    /**
     * creates a map of the frequency of characters in a file
     */

    public static Map<Character, Integer> freq(ArrayList<Character> chars) {
        // compute the frequency of each character
        HashMap<Character, Integer> freq = new HashMap<Character, Integer>();
        for (char c : chars) {
            if (freq.containsKey(c)) freq.put(c, freq.get(c) + 1);
            else freq.put(c, 1);
        }
        return freq;
    }

    /**
     * builds the tree of characters 
     * @param freq
     * @return
     */
    public static BinaryTree<CharFreq> buildTrees(Map<Character, Integer> freq) {
        // Creates a priority queue with TreeComparator implementation
        PriorityQueue<BinaryTree<CharFreq>> Q = new PriorityQueue<>(new TreeComparator());

        // Goes through and creates nodes with corresponding frequency values and adds to Q
        for (char c : freq.keySet()) {
            CharFreq f = new CharFreq(c, freq.get(c));
            BinaryTree<CharFreq> k = new BinaryTree<>(f);
            Q.add(k);
        }

        // Merges all the nodes into one big Tree

        if (Q.size() == 1) {
            BinaryTree<CharFreq> e1 = Q.poll();
            BinaryTree<CharFreq> tree = new BinaryTree<CharFreq>(null, e1, null);
            Q.add(tree);

        } else if (Q.size() > 1){
            while (Q.size() > 1) {
                BinaryTree<CharFreq> e1 = Q.poll();
                BinaryTree<CharFreq> e2 = Q.poll();
                CharFreq newRoot = new CharFreq('-', e1.getData().frequency + e2.getData().frequency);
                BinaryTree<CharFreq> tree = new BinaryTree<CharFreq>(newRoot, e1, e2);
                Q.add(tree);
            }
        }
        else {
            System.out.println("no file found, cannot be compressed/decompressed");
        }
        BinaryTree<CharFreq> e = Q.poll();
        return e;
    }

    /**
     * Builds the encoding map for the tree using helper method encodeMapHelper that recursively goes down the encoded tree
     * The codes found by the helper are added to the Map (encodeMap)
     * @param encodeData
     * @return
     */
    public static Map<Character, String> encodeMap(BinaryTree<CharFreq> encodeData) {

        if (encodeData == null) {
            return null;
        } else {
            // Parameters for codeMapHelper
            Map<Character, String> encodeMap = new TreeMap<>();
            String pathSoFar = "";

            encodeMapHelper(encodeMap, pathSoFar, encodeData);
            return encodeMap;
        }
    }

    /**
     * The helper function recursively goes down the tree and determines the code (0s and 1s) for each leaf
     * @param encodeMap
     * @param pathSoFar
     * @param currentTree
     */
    public static void encodeMapHelper(Map<Character, String> encodeMap, String pathSoFar,
                                       BinaryTree<CharFreq> currentTree) {

        if (currentTree == null) {
            return;
        }

        if (currentTree.isLeaf()) {
            encodeMap.put(currentTree.data.character, pathSoFar);
        } else {
            if (currentTree.hasLeft()) {
                encodeMapHelper(encodeMap, pathSoFar + "0", currentTree.getLeft());
            }
            if (currentTree.hasRight()) {
                encodeMapHelper(encodeMap, pathSoFar + "1", currentTree.getRight());
            }
        }

    }

    /**
     * Reads a given text file and outputs a bit file
     *
     * @param inPath
     * @throws IOException
     */
    public static void encodeFile(String inPath) throws IOException {
        // Generates code map using binary tree
        t = buildTrees(freq(charCode(inPath)));
        if (t == null) {
        } else {

            Map<Character, String> dictionary = encodeMap(t);

            // Begins bitreader
            BufferedBitWriter bitOutput = new BufferedBitWriter(inPath.substring(0, inPath.length() - 4) + "_compressed.txt");
            BufferedReader charInput = new BufferedReader(new FileReader(inPath));

            // While bit reader has next

            try {
                int charInt = charInput.read();
                while (charInt != -1) {

                    // Take the character and get its code
                    char[] charCode = dictionary.get((char) charInt).toCharArray();

                    // Write bit values based on the code digit
                    for (char digit : charCode) {
                        if (digit == '0') bitOutput.writeBit(false);
                        else bitOutput.writeBit(true);
                    }
                    charInt = charInput.read();

                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            } finally {
                bitOutput.close();
            }
        }
    }

    /**
     * reads the compressed file and converts the binary sequence into characters
     * @param compressedfilename
     * @throws IOException
     */
    public static void decode(String compressedfilename) throws IOException {
        BinaryTree<CharFreq> temp = t;
        if (t == null){
            return;
        } else {
            BufferedBitReader bitInput = new BufferedBitReader(compressedfilename);
            String decompressedfilename = compressedfilename.substring(0, compressedfilename.length() - 15) + "_decompressed.txt";
            BufferedWriter encodedouput = new BufferedWriter(new FileWriter(decompressedfilename));

            try {
                while (bitInput.hasNext()) {
                    // moves left if bit == false
                    boolean bit = bitInput.readBit();
                    if (bit == false) {
                        temp = temp.getLeft();

                        // moves right if bit == true
                    } else if (bit == true) {
                        temp = temp.getRight();
                    }
                    // if we get a leaf, we want to get the character of that leaf
                    if (temp.isLeaf()) {
                        encodedouput.write(temp.getData().getCharacter());
                        temp = t;
                    }
                }

            } finally {
                bitInput.close();
                encodedouput.close();

            }
        }
    }

    /**
     * testing various files by calling the encode and decode methods
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Test Empty File");
        encodeFile("PS3/Emptytest.txt");
        decode("PS3/Emptytest_compressed.txt");

        System.out.println("Test Full File");
        encodeFile("PS3/USConstitution.txt");
        decode("PS3/USConstitution_compressed.txt");

        System.out.println("Test One Letter File");
        encodeFile("PS3/OneCharLC.txt");
        decode("PS3/OneCharLC_compressed.txt");

        System.out.println("Testing small sentence");
        encodeFile("PS3/Wordtest.txt");
        decode("PS3/Wordtest_compressed.txt");

    }
}