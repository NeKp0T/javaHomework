package com.example.trie;

import java.io.*;
import java.util.HashMap;

/**
 * Implements trie data structure. Each string can only occur in a trie once.
 *
 * All methods do not accept null string as an argument.
 *
 * Can be serialized and deserialized using corresponding methods
 */
public class Trie implements Serializable {
    private static class Node {
         /*
          * Methods of this class do not work correctly with null as argument.
          * However they don't perform any checks because outer class Trie does in instead.
          */
        private HashMap<Character, Node> next;
        private boolean stringEndsHere;
        private int stringsEndSubtree;

        public Node() {
            next = new HashMap<>();
        }

        public boolean add(String element, int position) {
            if (element.length() == position) {
                boolean hadString = stringEndsHere;
                stringEndsHereSet(true);
                return !hadString;
            }
            boolean added = accessNext(element.charAt(position)).add(element, position + 1);
            if (added) {
                stringsEndSubtreeChange(1);
            }
            return added;
        }

        public boolean contains(String element, int position) {
            if (element.length() == position) {
                return stringEndsHere;
            }
            Node nextNode = getNext(element.charAt(position));
            return nextNode != null && nextNode.contains(element, position + 1);
        }

        public boolean remove(String element, int position) {
            if (element.length() == position) {
                boolean hadString = stringEndsHere;
                stringEndsHereSet(false);
                return hadString;
            }

            final boolean removed = accessNext(element.charAt(position)).remove(element, position + 1);
            if (removed) {
                stringsEndSubtreeChange(-1);
            }
            return removed;
        }

        public int howManyStartsWithPrefix(String prefix, int position) {
            if (prefix.length() == position) {
                return getStringsEndSubtree();
            }

            Node nextNode = getNext(prefix.charAt(position));
            if (nextNode == null) {
                return 0;
            }
            return nextNode.howManyStartsWithPrefix(prefix, position + 1);
        }

        public int getStringsEndSubtree() {
            return stringsEndSubtree;
        }

        private void stringEndsHereSet(boolean value) {
            if (value != stringEndsHere) {
                if (value) {
                    stringsEndSubtreeChange(1);
                } else {
                    stringsEndSubtreeChange(-1);
                }
            }
            stringEndsHere = value;
        }

        private void stringsEndSubtreeChange(int difference) {
            stringsEndSubtree += difference;
        }

        private Node accessNext(char character) {
            if (!next.containsKey(character)) {
                next.put(character, new Node());
            }
            return next.get(character);
        }

        private Node getNext(char character) {
            return next.get(character);
        }

        /**
         * int:
         *   three bytes -- size of hashMap
         *   one byte (zero or not) -- stringEndsHere
         * for each hashMap entity:
         *   char -- key of entry
         *   Node serialization
         */
        public void deserialize(InputStream in) throws IOException {
            next = new HashMap<>();
            stringEndsHere = false;
            stringsEndSubtree = 0;

            var dataIn = new DataInputStream(in);

            int firstInt = dataIn.readInt();
            int mapSize = firstInt & ((1 << 24) - 1);
            stringEndsHereSet(firstInt >> 24 != 0);

            for (int i = 0; i < mapSize; i++) {
                char key = dataIn.readChar();
                var value = new Node();
                value.deserialize(in);
                next.put(key, value);
                stringsEndSubtreeChange(value.getStringsEndSubtree());
            }
        }

        public void serialize(OutputStream out) throws IOException {
            var dataOut = new DataOutputStream(out);
            int firstInt = next.size();
            if (stringEndsHere) {
                firstInt |= 1 << 24;
            }
            dataOut.writeInt(firstInt);

            for (var i : next.entrySet()) {
                dataOut.writeChar(i.getKey());
                i.getValue().serialize(out);
            }
        }
    }

    private final Node root;

    /**
     * Creates an empty Trie
     */
    public Trie() {
        root = new Node();
    }

    /**
     * Adds passed string to trie.
     * @param element  not null string
     * @return <code>true</code> if the specified string was not contained in trie
     * @throws IllegalArgumentException  if specified element is null
     */
    public boolean add(String element) throws IllegalArgumentException {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        return root.add(element, 0);
    }

    /**
     * Returns <code>true</code> if trie contains the specified string.
     * @return  <code>true</code> if trie contains the specified string
     * @throws IllegalArgumentException  if the specified string is null
     */
    public boolean contains(String element) throws IllegalArgumentException {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        return root.contains(element, 0);
    }

    /**
     * Removes the specified string from trie if it was there and does nothing otherwise.
     * @param element the string to be removed
     * @return <code>true</code> if trie contained the specified string
     * @throws IllegalArgumentException if the specified string is null
     */
    public boolean remove(String element) throws IllegalArgumentException {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        return root.remove(element, 0);
    }

    /**
     * Returns the count of strings contained in trie.
     * @return the count of strings contained in trie
     */
    public int size() {
        return root.getStringsEndSubtree();
    }

    /**
     * Returns the count of contained string that start with specified prefix.
     * @param prefix  the prefix by which counted strings start
     * @return the count of contained string that start with specified prefix
     * @throws IllegalArgumentException if the specified prefix is null
     */
    public int howManyStartsWithPrefix(String prefix) throws IllegalArgumentException {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        return root.howManyStartsWithPrefix(prefix, 0);
    }

    /**
     * Serealizes trie to byte-code in format parsed by deserialize method.
     * @param out the stream to put serialization
     * @throws IOException if any exceptions occur with while working with the stream
     */
    public void serialize(OutputStream out) throws IOException {
        root.serialize(out);
    }

    /**
     * Deserializes trie from byte-code generated by serialize method and replaces current trie completely.
     * @param in the stream to read serialization from
     * @throws IOException if any exceptions occur with while working with the stream
     */
    public void deserialize(InputStream in) throws IOException {
        root.deserialize(in);
    }
}
