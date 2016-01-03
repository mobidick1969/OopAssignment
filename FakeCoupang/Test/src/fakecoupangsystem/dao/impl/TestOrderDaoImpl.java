package fakecoupangsystem.dao.impl;

import fakecoupangsystem.dao.LogisticsDao;
import fakecoupangsystem.dao.OrderDao;
import fakecoupangsystem.dao.ProductDao;
import fakecoupangsystem.domain.Order;
import fakecoupangsystem.domain.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Coupang on 2015. 12. 26..
 * Just for Test.
 * Make fake order, lookup, logistics process
 */
public final class TestOrderDaoImpl implements ProductDao, LogisticsDao, OrderDao {

	private static long orderId = 0;

	@Override
	public void updateStatus(long orderId, Order.DeliverStatus deliverStatus) {
		System.out.println("update status order-id : " + orderId + " status : " + deliverStatus);
	}

	@Override
	public void insertOrder(Order order) {
		System.out.println("insert order : " + order);
	}

	@Override public long nextOrderId() {
		return orderId;
	}

	@Override
	public List<Order> getOrdersByCoupangId(String coupangId) {
		return makeFakeOrderList();
	}

	private List<Order> makeFakeOrderList() {
		List<Order> orders = new ArrayList<Order>();

		for(int i = 0 ; i<10 ; i++ ) {
			orders.add(makeFakeOrder(i));
		}

		return orders;
	}

	public static Order makeFakeOrder(int hint) {
		Product product = new Product();
		product.setCompany("company" + hint);
		product.setCost(10000*hint);
		product.setDeliverCost(100*hint);
		product.setDescription("description" + hint);
		product.setMakeDate(new Date());
		product.setName("name" + hint);

		Order order = new Order();
		order.addProduct(product);

		order.setCoupangId("coupang_id" + hint);
		order.setOrderId(orderId);
		order.setOrdererName("orderer_name" + orderId);
		order.setDeliverStatus(Order.DeliverStatus.NONE);
		orderId++;
		return order;
	}

	@Override
	public Order getOrderById(long orderId) {
		return makeFakeOrder(0);
	}
}
