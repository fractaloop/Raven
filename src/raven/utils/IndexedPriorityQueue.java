package raven.utils;

import java.util.ArrayList;
import java.util.List;

public class IndexedPriorityQueue<K extends Comparable<K>> {
	private List<K>	keys = new ArrayList<K>();
	
	private List<Integer> heap;
	
	private List<Integer> invHeap;
	
	private int size, maxSize;
	
	public IndexedPriorityQueue(List<K> keys, int maxSize) {
		this.keys = keys;
		this.maxSize = maxSize;
		invHeap = new ArrayList<Integer>(maxSize);
		heap = new ArrayList<Integer>(maxSize);
		size = 0;
		
		// Allocate blanks
		for (int i = 0; i < maxSize+1; i++) {
			heap.add(0);
			invHeap.add(0);			
		}
	}
	
	private void swap(int a, int b) {
		int temp = heap.get(a);
		heap.set(a, heap.get(b));
		heap.set(b, temp);
		
		// change the handles too
		invHeap.set(heap.get(a), a);
		invHeap.set(heap.get(b), b);
	}
	
	private void reorderUpwards(int node) {
		// move up the heap swapping the elements until the heap is ordered
		while (node > 1 && keys.get(heap.get(node/2)).compareTo(keys.get(heap.get(node))) > 0) {
			swap(node/2, node);
			
			node /= 2;
		}
	}
	
	private void reorderDownwards(int node, int heapSize) {
		// move down the heap from node nd swapping the elements until the
		// heap is reordered
		while (2 * node <= heapSize) {
			int child = 2 * node;
			
			// set child to smaller of node's two children
			if (child < heapSize && keys.get(heap.get(child)).compareTo(keys.get(heap.get(child+1))) > 0) {
				child++;
			}
			
			// if this node is larger than its child, swap
			if (keys.get(heap.get(node)).compareTo(keys.get(heap.get(child))) > 0) {
				swap(child, node);
			} else {
				break;
			}
		}
	}
	
	/** to insert an item into the queue it gets added to the end of the heap
	 * and then the heap is reordered from the bottom up. */
	public void insert(int index) {
		if (size+1 > maxSize)
			throw new IndexOutOfBoundsException("IndexPriorityQueue grew too large!");
		
		size++;
		heap.set(size, index);
		invHeap.set(index, size);
		
		reorderUpwards(size);
	}
	
	/** to get the min item the first element is exchanged with the lowest in
	 * the heap and then the heap is reordered from the top down. */
	public int pop() {
		swap(1, size);
		
		reorderDownwards(1, size-1);
		
		return heap.get(size--);
	}
	
	public void changePriority(int index) {
		reorderUpwards(invHeap.get(index));
	}
	
	public boolean isEmpty() { return size == 0; }
}
