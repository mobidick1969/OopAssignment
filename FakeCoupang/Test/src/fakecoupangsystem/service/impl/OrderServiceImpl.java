package fakecoupangsystem.service.impl;

import fakecoupangsystem.dao.OrderDao;
import fakecoupangsystem.datastructure.Queue;
import fakecoupangsystem.datastructure.impl.CircularPendingQueue;
import fakecoupangsystem.datastructure.impl.ReadOnlyQueue;
import fakecoupangsystem.domain.Order;
import fakespring.annotation.Autowired;
import fakecoupangsystem.service.OrderService;
import fakespring.annotation.Bean;
import fakespring.annotation.PreDestroy;

import java.util.Collections;
import java.util.List;

/**
 * Created by Coupang on 2015. 12. 26..
 */
@Bean
public class OrderServiceImpl implements OrderService {

	private static final int MAX_QUEUE = 20;

	@Autowired
	private OrderDao orderDao;

	private Queue queue;
	private Queue readOnlyQueue;

	public OrderServiceImpl() {
		queue = new CircularPendingQueue(20);
		//wrapping pending queue
		readOnlyQueue = new ReadOnlyQueue(queue);
	}

	@Override
	public long orderProducts(Order order) {
		long orderId = orderDao.nextOrderId();
		order.setOrderId(orderId);
		queue.enqueue(order);
		if( queue.size() >= MAX_QUEUE ) {
			orderProcess();
		}

		return orderId;
	}

	@Override
	public Queue pendingOrders() {
		return readOnlyQueue;
	}

	private void orderProcess() {
		while(queue.size() > 0) {
			Order order = queue.dequeue();
			orderDao.insertOrder(order);
		}
	}

	@PreDestroy
	private void cleanPending() {
		orderProcess();
	}
}
