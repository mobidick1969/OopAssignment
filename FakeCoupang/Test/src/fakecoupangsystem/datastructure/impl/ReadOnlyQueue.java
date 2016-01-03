package fakecoupangsystem.datastructure.impl;

import fakecoupangsystem.datastructure.Indexing;
import fakecoupangsystem.datastructure.Queue;
import fakecoupangsystem.domain.Order;

import java.util.List;

/**
 * Created by Coupang on 2015. 12. 27..
 */
public class ReadOnlyQueue implements Queue {

	private Queue queue;

	public ReadOnlyQueue(Queue queue) {
		this.queue = queue;
	}

	@Override public void enqueue(Order data) {
		throw new UnsupportedOperationException();
	}

	@Override public Order dequeue() {
		throw new UnsupportedOperationException();
	}

	@Override public int size() {
		return queue.size();
	}

	@Override public int capacity() {
		return queue.capacity();
	}

	@Override public Order at(int idx) {
		return queue.at(idx);
	}

	@Override
	public List<Order> find(Indexing indexing, Object target) {
		return queue.find(indexing, target);
	}
}
