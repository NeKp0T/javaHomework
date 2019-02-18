package com.example.smartList;

import java.util.*;


/**
 * Implements List interface using optimal data structure for storing elements
 * When contains only one elements, stores it as is.
 * When contains from 2 to 5 elements, uses array of size 5
 * If contains more when 5 elements, uses ArrayList.
 * Is mutable.
 * @param <E> type of list elements
 */
public class SmartList<E> extends AbstractList<E> implements List<E> {

    private static final int MAX_ARRAY_SIZE = 5;
    private int size;
    private Object data;

    enum DataType {NULL, ELEMENT, ARRAY, LIST}

    private DataType getDataType() {
        if (size == 0) {
            return DataType.NULL;
        }
        if (size == 1) {
            return DataType.ELEMENT;
        }
        if (size <= MAX_ARRAY_SIZE) {
            return DataType.ARRAY;
        }
        return DataType.LIST;
    }

    /**
     * {@inheritDoc}
     */
    public SmartList() {}

    /**
     * {@inheritDoc}
     */
    public SmartList(Collection<? extends E> c) {
        this.addAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        if (getDataType() == DataType.ELEMENT) {
            return getElementData();
        }
        if (getDataType() == DataType.ARRAY) {
            return getArrayData()[index];
        }
        return getListData().get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        E previous = get(index);

        if (getDataType() == DataType.ELEMENT) {
            data = element;
        } else if (getDataType() == DataType.ARRAY) {
            getArrayData()[index] = element;
        } else {
            getListData().set(index, element);
        }
        return previous;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(int index, E e) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        if (getDataType() == DataType.NULL) {
            data = e;
        }
        else if (getDataType() == DataType.ELEMENT) {
            E old = getElementData();
            data = new Object[MAX_ARRAY_SIZE];
            getArrayData()[index] = e;
            getArrayData()[1 - index] = old;
        } else if (getDataType() == DataType.ARRAY) {
            if (size < MAX_ARRAY_SIZE) {
                if (size - index >= 0) {
                    System.arraycopy(getArrayData(), index, getArrayData(), index + 1, size - index);
                }
                getArrayData()[index] = e;
            } else {
                var l = new ArrayList<E>(MAX_ARRAY_SIZE+1);
                l.addAll(Arrays.asList(getArrayData()).subList(0, index));
                l.add(e);
                l.addAll(Arrays.asList(getArrayData()).subList(index, MAX_ARRAY_SIZE));
                data = l;
            }
        } else {
            getListData().add(index, e);
        }
        size++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        E removed = get(index);

        if (getDataType() == DataType.ELEMENT) {
            data = null;
        } else if (getDataType() == DataType.ARRAY) {
            if (size == 2) {
                data = getArrayData()[1 - index];
            } else {
                for (int i = index; i + 1 < size; i++) {
                    getArrayData()[i] = getArrayData()[i + 1];
                }
            }
        } else {
            if (size == MAX_ARRAY_SIZE + 1) {
                @SuppressWarnings("unchecked") var array = (E[]) new Object[MAX_ARRAY_SIZE];
                var iterator = getListData().iterator();
                for (int i = 0; i < index; i++) {
                    array[i] = iterator.next();
                }
                iterator.next();
                for (int i = index; i < size - 1; i++) {
                    array[i] = iterator.next();
                }
                data = array;
            } else {
                getListData().remove(index);
            }
        }
        size--;
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private E getElementData() {
        return (E) data;
    }

    @SuppressWarnings("unchecked")
    private E[] getArrayData() {
        return (E[]) data;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<E> getListData() {
        return (ArrayList<E>) data;
    }
}