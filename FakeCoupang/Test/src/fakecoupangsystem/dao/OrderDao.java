package fakecoupangsystem.dao;


import fakecoupangsystem.domain.Order;

import java.util.List;

/**
 * Created by Coupang on 2015. 12. 26..
 */
public interface OrderDao {
	long nextOrderId();
	void insertOrder(Order order);
	List<Order> getOrdersByCoupangId(String coupangId);
	Order getOrderById(long orderId);
}
