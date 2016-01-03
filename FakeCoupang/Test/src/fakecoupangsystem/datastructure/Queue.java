package fakecoupangsystem.datastructure;


import fakecoupangsystem.domain.Order;

import java.util.List;

/**
 * Created by Coupang on 2015. 12. 26..
 */
public interface Queue {
	void enqueue(Order data);

	Order dequeue();

	int size();

	int capacity();

	Order at(int idx);

	List<Order> find(Indexing indexing, Object target);
}