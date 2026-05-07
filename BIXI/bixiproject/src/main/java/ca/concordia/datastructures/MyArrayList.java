package ca.concordia.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;
/*
Unavoidable warning I encountered, apparently Java can't create a new T[]
so we're allocating & casting
 */
@SuppressWarnings("unchecked")
public class MyArrayList<T> implements Iterable<T>  {
    private T[] data;
    private int size;

    private static final int DEFAULT_CAPACITY = 10;

    public MyArrayList() {
        data = (T[]) new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public void add(T element) {
        ensureCapacity();
        data[size++] = element;
    }

    public T get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        return data[index];
    }

    public void set(int index, T value) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        data[index] = value;
    }

    public int size() {
        return size;
    }

    private void ensureCapacity() {
        if (size == data.length) {
            T[] newData = (T[]) new Object[data.length * 2];
            for (int i = 0; i < data.length; i++) {
                newData[i] = data[i];
            }
            data = newData;
        }
    }

    public T[] toArray() {
        T[] result = (T[]) new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = data[i];
        }
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {return i < size;}

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException(); //iterator interface states this is required
                return data[i++];
            }
        };
    }
}
