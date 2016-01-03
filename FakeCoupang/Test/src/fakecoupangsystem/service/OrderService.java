package fakecoupangsystem.service;

import fakecoupangsystem.datastructure.Queue;
import fakecoupangsystem.domain.Order;

import java.util.List;

/**
 * Created by Coupang on 2015. 12. 26..
 */
public interface OrderService {
	long orderProducts(Order order);
	Queue pendingOrders();
}
