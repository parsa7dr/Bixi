package ca.concordia.datastructures;

@SuppressWarnings("unchecked")
public class MyHashTable<K, V> {

    private static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final MyLinkedList<Entry<K, V>>[] buckets;
    private int size;

    public MyHashTable(int capacity) {
        buckets = (MyLinkedList<Entry<K, V>>[]) new MyLinkedList[capacity];
        size = 0;
    }

        //did it this way to avoid edge cases
    private int indexFor(K key) {
        int h = key.hashCode();
        h = h & 0x7fffffff;
        return h % buckets.length;
    }

    public V get(K key) {
        int index = indexFor(key);
        MyLinkedList<Entry<K, V>> bucket = buckets[index];

        if (bucket == null) return null;

        MyLinkedList.Node<Entry<K, V>> current = bucket.getHead();
        while (current != null) {
            Entry<K, V> e = current.data;
            if (e.key.equals(key)) {
                return e.value;
            }
            current = current.next;
        }

        return null;
    }

    public void put(K key, V value) {
        int index = indexFor(key);

        if (buckets[index] == null) {
            buckets[index] = new MyLinkedList<>();
        }

        MyLinkedList.Node<Entry<K, V>> current = buckets[index].getHead();

        while (current != null) {
            Entry<K, V> e = current.data;
            if (e.key.equals(key)) {
                e.value = value;  //(updates the existing one)
                return;
            }
            current = current.next;
        }

        buckets[index].addFirst(new Entry<>(key, value));
        size++;
    }

    public int size() {
        return size;
    }

    public MyArrayList<K> keys() {
        MyArrayList<K> out = new MyArrayList<>();
        for (int i = 0; i < buckets.length; i++) {
            MyLinkedList<Entry<K, V>> bucket = buckets[i];
            if (bucket == null) continue;

            MyLinkedList.Node<Entry<K, V>> cur = bucket.getHead();
            while (cur != null) {
                out.add(cur.data.key);
                cur = cur.next;
            }
        }
        return out;
    }

    public static class EntryView<K, V> {
        public final K key;
        public final V value;

        public EntryView(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public MyArrayList<EntryView<K, V>> entries() {
        MyArrayList<EntryView<K, V>> out = new MyArrayList<>();

        for (int i = 0; i < buckets.length; i++) {
            MyLinkedList<Entry<K, V>> bucket = buckets[i];
            if (bucket == null) continue;

            MyLinkedList.Node<Entry<K, V>> cur = bucket.getHead();
            while (cur != null) {
                Entry<K, V> e = cur.data;
                out.add(new EntryView<>(e.key, e.value));
                cur = cur.next;
            }
        }

        return out;
    }

}
