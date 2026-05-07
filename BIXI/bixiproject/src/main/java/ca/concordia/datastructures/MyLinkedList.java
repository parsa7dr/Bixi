package ca.concordia.datastructures;

public class MyLinkedList<T> {

    static class Node<T> {   //for hash table
        T data;
        Node<T> next;

        Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<T> head;
    private int size;

    public void addFirst(T value) {
        head = new Node<>(value, head);
        size++;
    }

    public Node<T> getHead() {
        return head;
    }

    public int size() {
        return size;
    }
}
