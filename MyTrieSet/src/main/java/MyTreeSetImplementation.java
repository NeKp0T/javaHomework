import com.sun.source.tree.Tree;
import org.jetbrains.annotations.NotNull;

import java.util.*;


class MyTreeSetImplementation<E extends Comparable<? super E>> extends AbstractSet<E>
    implements MyTreeSet<E> {

    static private class TreeNode<E> {
        private E value;
        private TreeNode<E> left;
        private TreeNode<E> right;
        private TreeNode<E> up;
        private TreeNode(E value) {
            this.value = value;
        }

        public TreeNode<E> getNext() {
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

        public TreeNode<E> getPrev() {
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

    private class TreeIterator implements Iterator<E> {
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

        public Iterator<E> descendingView() {
            return new DescendingIterator();
        }
    }

    private Comparator<? super E> comparator;
    private int size;
    private TreeNode<E> firstNode;
    private TreeNode<E> lastNode;
    private TreeNode<E> root;

    public MyTreeSetImplementation() {
        this(Comparator.<E>naturalOrder());
    }

    public MyTreeSetImplementation(@NotNull Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    private TreeNode<E> addReturningNode(E e) {
        if (root == null) {
            root = new TreeNode<E>(e);
            firstNode = root;
            lastNode = root;
            return root;
        }
        var i = root;
        while (true) {
            int res = comparator.compare(i.value, e);
            if (res == 0) {
                return null;
            }
            if (res < 0) {
                if (i.left == null) {
                    i.left = new TreeNode<E>(e);
                    return i.left;
                } else {
                    i = i.left;
                }
            } else { // res > 0
                if (i.right == null) {
                    i.right = new TreeNode<E>(e);
                    return i.right;
                } else {
                    i = i.right;
                }
            }
        }
    }

    public boolean add(E e) {
        var node = addReturningNode(e);
        if (node == null) {
            return false;
        }
        if (comparator.compare(e, firstNode.value) < 0) {
            firstNode = node;
        }
        if (comparator.compare(e, lastNode.value) > 0) {
            lastNode = node;
        }
        size++;
        return true;
    }

    public int size() {
        return size;
    }

    @NotNull
    public Iterator iterator() {
        return new TreeIterator(null, firstNode);
    }

    public Iterator<E> descendingIterator() {
        return new TreeIterator(lastNode, null).descendingView();
    }

    public MyTreeSet<E> descendingSet() {
        return null; // TODO
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

    public E lower(E e) {
        return null;
    }

    public E floor(E e) {
        return null;
    }

    public E ceiling(E e) {
        return null;
    }

    public E higher(E e) {
        return null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(Object o) {
        return false;
    }
}