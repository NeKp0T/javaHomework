import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class MyTreeSetImplementation<E extends Comparable<? super E>>
        extends AbstractSet<E>
        implements MyTreeSet<E> {

    static private class TreeNode<E> {
        private final E value;
        private TreeNode<E> left;
        private TreeNode<E> right;
        private TreeNode<E> up;
        private TreeNode(E value) {
            this.value = value;
        }

        TreeNode<E> getNext() {
            if (this.right != null) {
                return right.leftest();
            }

            var i = this;
            while (i.up != null && i.isRight()) {
                i = i.up;
            }

            if (i.up == null) {
                return null;
            }
            return i.up;
        }

        TreeNode<E> getPrev() {
            if (this.left != null) {
                return left.rightest();
            }

            var i = this;
            while (i.up != null && i.isLeft()) {
                i = i.up;
            }

            if (i.up == null) {
                return null;
            }
            return i.up;
        }

        // doesn't check if up == null
        private boolean isLeft() {
            return up.left == this;
        }

        private boolean isRight() {
            return up.right == this;
        }

        private TreeNode<E> leftest() {
            TreeNode i = this;
            while (i.left != null) {
                i = i.left;
            }
            return i;
        }

        private TreeNode<E> rightest() {
            var i = this;
            while (i.right != null) {
                i = i.right;
            }
            return i;
        }
    }

    // TODO invalidation
    public class TreeIterator implements Iterator<E> {
        private TreeNode<E> prev;
        private TreeNode<E> next;

        private TreeIterator(TreeNode<E> prev, TreeNode<E> next) {
            this.prev = prev;
            this.next = next;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            prev = next;
            next = next.getNext();
            return prev.value;
        }

        public boolean hasPrevious() {
            return prev != null;
        }

        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            next = prev;
            prev = prev.getPrev();
            return next.value;
        }

        private class DescendingIterator implements Iterator<E> {

            @Override
            public boolean hasNext() {
                return hasPrevious();
            }

            @Override
            public E next() {
                return previous();
            }
        }

        private Iterator<E> descendingView() {
            return new DescendingIterator();
        }
    }

    private final Comparator<? super E> comparator;
    private int size;
    private TreeNode<E> firstNode;
    private TreeNode<E> lastNode;
    private TreeNode<E> root;

    public MyTreeSetImplementation() {
        this(Comparator.naturalOrder());
    }

    @SuppressWarnings("WeakerAccess")
    public MyTreeSetImplementation(@NotNull Comparator<? super E> comparator) {
        this.comparator = comparator;
    }


    public boolean add(E e) {
        if (root == null) {
            root = new TreeNode<>(e);
            return true;
        }
        var adjacent = findAdjacent(e);
        int dir = comparator.compare(e, adjacent.value);

        if (dir == 0) {
            return false;
        }

        TreeNode<E> newNode;
        if (dir < 0) {
            adjacent.left = new TreeNode<>(e);
            newNode = adjacent.left;
        } else {
            adjacent.right = new TreeNode<>(e);
            newNode = adjacent.right;
        }

        if (comparator.compare(e, firstNode.value) < 0) {
            firstNode = newNode;
        }
        if (comparator.compare(e, lastNode.value) > 0) {
            lastNode = newNode;
        }
        size++;
        return true;
    }

    public int size() {
        return size;
    }

    @NotNull
    public Iterator<E> iterator() {
        return new TreeIterator(null, firstNode);
    }

    public Iterator<E> descendingIterator() {
        return new TreeIterator(lastNode, null).descendingView();
    }

    public MyTreeSet<E> descendingSet() {
        return new DescendingSetView();
    }

    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return firstNode.value;
    }

    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return lastNode.value;
    }

    private TreeNode<E> floorNode(E e) {
        var node = findAdjacent(e);
        if (node == null) {
            return null;
        }
        int dir = comparator.compare(node.value, e);
        if (dir <= 0) {
            return node;
        } else {
            return node.getPrev();
        }
    }

    private TreeNode<E> ceilNode(E e) {
        var node = findAdjacent(e);
        if (node == null) {
            return null;
        }
        int dir = comparator.compare(node.value, e);
        if (dir >= 0) {
            return node;
        } else {
            return node.getNext();
        }
    }

    public E lower(E e) {
        var node = floorNode(e);
        if (node == null || node.getPrev() == null) {
            return null;
        }
        return node.getPrev().value;
    }

    public E floor(E e) {
        var node = floorNode(e);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    public E ceiling(E e) {
        var node = ceilNode(e);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    public E higher(E e) {
        var node = ceilNode(e);
        if (node == null || node.getNext() == null) {
            return null;
        }
        return node.getNext().value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(Object o) {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return comparator.compare(findAdjacent((E) o).value, (E) o) == 0;
    }

    // null only if root is null
    // returns position of e if it is present,
    // or such node that e can be placed instead of left or right of it.
    private @Nullable TreeNode<E> findAdjacent(E e) {
        if (root == null) {
            return null;
        }
        TreeNode<E> i = root;
        while (true) {
            int dir = comparator.compare(i.value, e);
            if (dir == 0) {
                return i;
            }
            if (dir < 0) {
                if (i.left == null) {
                    return i;
                } else {
                    i = i.left;
                }
            } else { // dir > 0
                if (i.right == null) {
                    return i;
                } else {
                    i = i.right;
                }
            }
        }
    }

    private class DescendingSetView extends AbstractSet<E>
    implements MyTreeSet<E> {

        @Override
        public Iterator<E> descendingIterator() {
            return MyTreeSetImplementation.this.iterator();
        }

        @Override
        public MyTreeSet<E> descendingSet() {
            return MyTreeSetImplementation.this;
        }

        @Override
        public E first() {
            return MyTreeSetImplementation.this.last();
        }

        @Override
        public E last() {
            return MyTreeSetImplementation.this.first();
        }

        @Override
        public E lower(E e) {
            return MyTreeSetImplementation.this.higher(e);
        }

        @Override
        public E floor(E e) {
            return MyTreeSetImplementation.this.ceiling(e);
        }

        @Override
        public E ceiling(E e) {
            return MyTreeSetImplementation.this.floor(e);
        }

        @Override
        public E higher(E e) {
            return MyTreeSetImplementation.this.lower(e);
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return MyTreeSetImplementation.this.descendingIterator();
        }

        @Override
        public int size() {
            return 0;
        }
    }
}