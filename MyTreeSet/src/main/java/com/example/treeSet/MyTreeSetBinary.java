package com.example.treeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;


/**
 * Implements <code>MyTreeSet<E></code> interface using unbalanced binary tree.
 * @param <E> a type of set elements
 */
public class MyTreeSetBinary<E extends Comparable<? super E>>
        extends AbstractSet<E>
        implements MyTreeSet<E> {

    static private class TreeNode<E> {
        private final E value;
        @Nullable
        private TreeNode<E> left;
        @Nullable
        private TreeNode<E> right;
        @Nullable
        private TreeNode<E> up;

        private TreeNode(E value) {
            this.value = value;
        }

        @Nullable
        TreeNode<E> getNext() {
            if (this.right != null) {
                return right.leftestChild();
            }

            @NotNull var i = this;

            while (i.up != null && i.isRightSon()) {
                i = i.up;
            }

            if (i.up == null) {
                return null;
            }
            return i.up;
        }

        @Nullable
        TreeNode<E> getPrev() {
            if (this.left != null) {
                return left.rightestChild();
            }

            @NotNull var i = this;
            while (i.up != null && i.isLeftSon()) {
                i = i.up;
            }

            if (i.up == null) {
                return null;
            }
            return i.up;
        }

        // doesn't check if up == null
        private boolean isLeftSon() {
            return up.left == this;
        }

        // doesn't check if up == null
        private boolean isRightSon() {
            return up.right == this;
        }

        private @NotNull TreeNode<E> leftestChild() {
            TreeNode i = this;
            while (i.left != null) {
                i = i.left;
            }
            return i;
        }

        private @NotNull TreeNode<E> rightestChild() {
            var i = this;
            while (i.right != null) {
                i = i.right;
            }
            return i;
        }

        static <G> G getValueOrNull(TreeNode<G> node) {
            return (node == null) ? null : node.value;
        }
    }

    class TreeIterator implements Iterator<E> {

        @Nullable
        private TreeNode<E> prev;
        @Nullable
        private TreeNode<E> next;

        private int creationVersion;

        TreeIterator(@Nullable TreeNode<E> prev, @Nullable TreeNode<E> next) {
            creationVersion = version;
            this.prev = prev;
            this.next = next;
        }

        // throws a ConcurrentModificationException if collection was modified since iterator creation
        private void checkConcurrentModification() {
            if (version > creationVersion) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public boolean hasNext() {
            checkConcurrentModification();
            return next != null;
        }

        @Override
        public @NotNull E next() {
            checkConcurrentModification();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            prev = next;
            // next is checked to be not null in if statement above
            //noinspection ConstantConditions
            next = next.getNext();
            //noinspection ConstantConditions
            return prev.value;
        }

        @SuppressWarnings("WeakerAccess")
        public boolean hasPrevious() {
            checkConcurrentModification();
            return prev != null;
        }

        @SuppressWarnings("WeakerAccess")
        public @NotNull E previous() {
            checkConcurrentModification();
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            next = prev;

            // prev is checked to be not null in if statement above
            //noinspection ConstantConditions
            prev = prev.getPrev();
            //noinspection ConstantConditions
            return next.value;
        }

        private class DescendingIterator implements Iterator<E> {

            @Override
            public boolean hasNext() {
                return hasPrevious();
            }

            @Override
            public @NotNull E next() {
                return previous();
            }
        }

        private Iterator<E> descendingView() {
            return new DescendingIterator();
        }
    }

    @NotNull
    private final Comparator<? super E> comparator;
    private int size;

    @Nullable
    private TreeNode<E> firstNode;
    @Nullable
    private TreeNode<E> lastNode;
    @Nullable
    private TreeNode<E> root;

    private int version;

    public MyTreeSetBinary() {
        this(Comparator.naturalOrder());
    }

    public MyTreeSetBinary(@NotNull Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    public boolean add(E e) {
        TreeNode<E> newNode;
        if (root == null) {
            newNode = new TreeNode<>(e);
            root = newNode;
        } else {
            // root is not null, so findAdjucent returns not null
            @NotNull var adjacent = findAdjacent(e);
            @SuppressWarnings("ConstantConditions") int dir = comparator.compare(e, adjacent.value);

            if (dir == 0) {
                return false;
            }

            if (dir < 0) {
                adjacent.left = new TreeNode<>(e);
                newNode = adjacent.left;
            } else {
                adjacent.right = new TreeNode<>(e);
                newNode = adjacent.right;
            }
            newNode.up = adjacent;
        }

        if (firstNode == null || comparator.compare(e, firstNode.value) < 0) {
            firstNode = newNode;
        }
        if (lastNode == null || comparator.compare(e, lastNode.value) > 0) {
            lastNode = newNode;
        }

        size++;
        version++;
        return true;
    }

    public int size() {
        return size;
    }

    @NotNull
    public Iterator<E> iterator() {
        return new TreeIterator(null, firstNode);
    }

    @NotNull
    public Iterator<E> descendingIterator() {
        return new TreeIterator(lastNode, null).descendingView();
    }

    @NotNull
    public MyTreeSet<E> descendingSet() {
        return new DescendingSetView();
    }

    public @NotNull E first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        // not empty => firstNode exists
        //noinspection ConstantConditions
        return firstNode.value;
    }

    public @NotNull E last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        // not empty => lastNode exists
        //noinspection ConstantConditions
        return lastNode.value;
    }

    public @Nullable E lower(E e) {
        var node = findAdjacent(e);
        if (node == null) {
            return null;
        }
        int dir = comparator.compare(node.value, e);
        if (!(dir < 0)) {
            node = node.getPrev();
        }
        return TreeNode.getValueOrNull(node);
    }

    public @Nullable E floor(E e) {
        var node = findAdjacent(e);
        if (node == null) {
            return null;
        }
        int dir = comparator.compare(node.value, e);
        if (!(dir <= 0)) {
            node = node.getPrev();
        }
        return TreeNode.getValueOrNull(node);
    }

    public @Nullable E ceiling(E e) {
        var node = findAdjacent(e);
        if (node == null) {
            return null;
        }
        int dir = comparator.compare(node.value, e);
        if (!(dir >= 0)) {
            node = node.getNext();
        }
        return TreeNode.getValueOrNull(node);
    }

    public @Nullable E higher(E e) {
        var node = findAdjacent(e);
        if (node == null) {
            return null;
        }
        int dir = comparator.compare(node.value, e);
        if (!(dir > 0)) {
            node = node.getNext();
        }
        return TreeNode.getValueOrNull(node);
    }

    /**
     * @return if size() is 0
     */
    public boolean isEmpty() {
        return size == 0;
    }


    /**
     * {@inheritDoc}
     */
    public boolean contains(Object o) {
        E element = (E) o;
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        // Will throw ClassCastError if o's type isn't successor of E
        // not empty => findAdjucent returns not null
        //noinspection unchecked,ConstantConditions
        return comparator.compare(findAdjacent(element).value, element) == 0;
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
            if (dir > 0) {
                if (i.left == null) {
                    return i;
                } else {
                    i = i.left;
                }
            } else { // dir < 0
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
        public boolean add(E e) {
            return MyTreeSetBinary.this.add(e);
        }

        @Override
        public @NotNull Iterator<E> descendingIterator() {
            return MyTreeSetBinary.this.iterator();
        }

        @Override
        public @NotNull MyTreeSet<E> descendingSet() {
            return MyTreeSetBinary.this;
        }

        @Override
        public @Nullable E first() {
            return MyTreeSetBinary.this.last();
        }

        @Override
        public @Nullable E last() {
            return MyTreeSetBinary.this.first();
        }

        @Override
        public @Nullable E lower(E e) {
            return MyTreeSetBinary.this.higher(e);
        }

        @Override
        public @Nullable E floor(E e) {
            return MyTreeSetBinary.this.ceiling(e);
        }

        @Override
        public @Nullable E ceiling(E e) {
            return MyTreeSetBinary.this.floor(e);
        }

        @Override
        public @Nullable E higher(E e) {
            return MyTreeSetBinary.this.lower(e);
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return MyTreeSetBinary.this.descendingIterator();
        }

        @Override
        public int size() {
            return MyTreeSetBinary.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return MyTreeSetBinary.this.contains(o);
        }
    }
}