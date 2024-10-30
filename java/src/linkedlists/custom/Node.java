package linkedlists.custom;

import java.util.concurrent.locks.ReentrantLock;

public class Node {
	 Node(int item){key=item;next=null;}
	 public int key;
	 public Node next;
	 public ReentrantLock lock;
	 
	 public void lock() {
		 lock.lock();
	 }
	 
	 public void unlock() {
		 lock.unlock();
	 }
}
