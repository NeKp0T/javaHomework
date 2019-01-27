import java.io.IOException;
import java.util.HashMap;

public class Trie {
    private static class Node {
         /*
          * Methods of this class do not work correctly with null as argument.
          * However they don't perform any checks because outer class Trie does in instead.
          */
        private HashMap<Character, Node> next;
        private boolean stringEndsHere;
        private int stringsEndSubtree;

        public Node() {
            next = new HashMap<Character, Node>();
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
    }

    Node root;

    public Trie() {
        root = new Node();
    }

    public boolean add(String element) throws IllegalArgumentException {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        return root.add(element, 0);
    }
    public boolean contains(String element) throws IllegalArgumentException {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        return root.contains(element, 0);
    }
    public boolean remove(String element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        return root.remove(element, 0);
    }
    public int size() {
        return root.getStringsEndSubtree();
    }
    public int howManyStartsWithPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        return root.howManyStartsWithPrefix(prefix, 0);
    }
}
