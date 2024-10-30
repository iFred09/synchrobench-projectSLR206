package linkedlists.custom;

import contention.abstractions.AbstractCompositionalIntSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;

public class HandOverHand extends AbstractCompositionalIntSet {
    private final Node head;
    private final AtomicInteger size;

    public HandOverHand() {
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
        size = new AtomicInteger(0);
    }

    @Override
    public boolean addInt(int item) {
        Node pred = head;
        pred.lock();
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (curr.key < item) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.key == item) {
                    return false;
                }
                Node newNode = new Node(item);
                newNode.next = curr;
                pred.next = newNode;
                size.incrementAndGet();
                return true;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    @Override
    public boolean removeInt(int item) {
        Node pred = head;
        pred.lock();
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (curr.key < item) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.key == item) {
                    pred.next = curr.next;
                    size.decrementAndGet();
                    return true;
                }
                return false;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    @Override
    public boolean containsInt(int item) {
        Node pred = head;
        pred.lock();
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (curr.key < item) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                return curr.key == item;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    @Override
    public void clear() {
        head.lock();
        try {
            head.next = new Node(Integer.MAX_VALUE);
            size.set(0);
        } finally {
            head.unlock();
        }
    }

    @Override
    public int size() {
        return size.get();
    }

    private static class Node {
        final int key;
        volatile Node next;
        private final ReentrantLock lock = new ReentrantLock();

        Node(int key) {
            this.key = key;
        }

        void lock() {
            lock.lock();
        }

        void unlock() {
            lock.unlock();
        }
    }
}
