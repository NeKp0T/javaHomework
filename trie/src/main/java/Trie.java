import java.util.HashMap;

public class Trie {
    private static class Node {
         /*
          * Methods of this class do not accept null as argument.
          * However they don't perform any checks because outer class Trie does in instead.
          */
        private HashMap<Integer, Node> next;
        private int stringsEndHere;
        private int stringsEndSubtree;

        public Node() {
            next = new HashMap<Integer, Node>();
        }

        public boolean add(String element, int position) {
            if (element.length() == position) {
                stringsEndHereChange(1);
                return stringsEndHere > 1;
            }
            stringsEndSubtreeChange(1);
            return accessNext(element.charAt(position)).add(element, position + 1);
        }

        public boolean contains(String element, int position) {
            if (element.length() == position) {
                return stringsEndHere > 0;
            }
            Node nextNode = getNext(element.charAt(position));
            return nextNode != null && nextNode.contains(element, position + 1);
        }

        public boolean remove(String element, int position) {
            if (element.length() == position) {
                if (stringsEndHere > 0) {
                    stringsEndHereChange(-1);
                    return true;
                }
                return false;
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

        private void stringsEndHereChange(int difference) {
            stringsEndHere += difference;
            stringsEndSubtreeChange(difference);
        }

        private void stringsEndSubtreeChange(int difference) {
            stringsEndSubtree += difference;
        }

        private Node accessNext(int character) {
            if (!next.containsKey(character)) {
                next.put(character, new Node());
            }
            return next.get(character);
        }

        private Node getNext(int character) {
            return next.get(character);
        }
    }

    Node root;

    public Trie() {
        root = new Node();
    }

    boolean add(String element) throws IllegalArgumentException {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        return root.add(element, 0);
    }
    boolean contains(String element) throws IllegalArgumentException {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        return root.contains(element, 0);
    }
    boolean remove(String element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        return root.remove(element, 0);
    }
    int size() {
        return root.getStringsEndSubtree();
    }
    int howManyStartsWithPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        return root.howManyStartsWithPrefix(prefix, 0);
    }
}
