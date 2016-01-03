package fakecoupangsystem.datastructure.impl;

import fakecoupangsystem.datastructure.Queue;
import fakecoupangsystem.exception.UnderFlowException;
import fakecoupangsystem.domain.Order;
import fakecoupangsystem.datastructure.Indexing;

import java.util.*;

/**
 * Created by Buzz on 2015. 12. 27..
 */
public class FixedCachingQueue implements Queue {

	private Map<Order, Integer> check;
	private List<Order> list;
	private int capacity;

	public FixedCachingQueue(int capacity) {
		this.capacity = capacity;
		list = new LinkedList<Order>();
		check = new TreeMap<Order, Integer>();
	}

	private boolean isOverflow() {
		return size() > capacity();
	}

	@Override
	public void enqueue(Order data) {
		if( check.containsKey(data) ) {
			list.remove(check.get(data));
		}

		list.add(0, data);

		if( isOverflow() ) {
			list.remove(list.size()-1);
		}
	}

	@Override
	public Order dequeue() {
		if( list.isEmpty() )
			throw new UnderFlowException();

		Order order = list.get(0);
		list.remove(0);
		check.remove(order);
		return order;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public int capacity() {
		return capacity;
	}

	@Override
	public Order at(int idx) {
		if( idx < list.size() && idx >= 0 && !list.isEmpty() ) {
			return list.get(idx);
		}

		return null;
	}

	@Override
	public List<Order> find(Indexing indexing, Object target) {
		Iterator<Order> it = list.iterator();
		List<Order> orders = new ArrayList<Order>(5);

		while(it.hasNext()) {
			Order order = it.next();
			boolean found = false;
			switch(indexing) {
				case COUPANG_ID :
					if( order.getCoupangId().equals((String)target) ) {
						found = true;
					}
					break;
				case ORDER_ID:
					if( order.getOrderId() == (Long)target )  {
						found = true;
					}
					break;
				default :
					break;
			}

			if( found ) {
				orders.add(order);
			}
		}

		return orders;
	}
}
