package fakecoupangsystem.service.impl;

import fakecoupangsystem.dao.OrderDao;
import fakecoupangsystem.datastructure.Indexing;
import fakecoupangsystem.datastructure.Queue;
import fakecoupangsystem.domain.Order;
import fakecoupangsystem.domain.Product;
import fakecoupangsystem.domain.dto.OrderDto;
import fakespring.annotation.Autowired;
import fakecoupangsystem.service.LookupService;
import fakecoupangsystem.service.OrderService;
import fakecoupangsystem.service.ProductService;
import fakespring.annotation.Bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Coupang on 2015. 12. 26..
 */
@Bean
public class LookupServiceImpl implements LookupService {

	@Autowired
	private OrderService orderService;
	@Autowired
	private ProductService productService;
	@Autowired
	private OrderDao orderDao;

	public LookupServiceImpl() {
	}

	@Override
	public List<OrderDto> lookupByCoupangId(String coupangId) {
		Queue pendingQueue = orderService.pendingOrders();
		List<Order> pendingOrders = pendingQueue.find(Indexing.COUPANG_ID, coupangId);
		List<Order> dbOrders = orderDao.getOrdersByCoupangId(coupangId);

		List<Order> orders = new ArrayList<>(pendingOrders.size() + dbOrders.size());
		orders.addAll(pendingOrders);
		orders.addAll(dbOrders);

		return transformOrderDtoList(orders);
	}

	private List<OrderDto> transformOrderDtoList(List<Order> orders) {
		if( orders == null || orders.isEmpty() ) {
			return Collections.emptyList();
		}

		List<OrderDto> orderDtos = new ArrayList<>(orders.size());

		for(Order order : orders ) {
			orderDtos.add(transformOrderDto(order));
		}

		return orderDtos;
	}

	private OrderDto transformOrderDto(Order order) {
		if( order == null )
			return null;

		OrderDto orderDto = new OrderDto();
		orderDto.setFromOrder(order);

		for(int productId : order.getProducts() ) {
			Product product = productService.getProduct(productId);
			orderDto.addProduct(product);
		}

		return orderDto;
	}

	@Override
	public OrderDto lookupByOrderId(long orderId) {
		Queue pendingQueue = orderService.pendingOrders();
		List<Order> pendingOrders = pendingQueue.find(Indexing.ORDER_ID, orderId);

		if( pendingOrders != null && !pendingOrders.isEmpty() ) {
			return transformOrderDto(pendingOrders.get(0));
		}

		Order order = orderDao.getOrderById(orderId);
		return transformOrderDto(order);
	}
}
