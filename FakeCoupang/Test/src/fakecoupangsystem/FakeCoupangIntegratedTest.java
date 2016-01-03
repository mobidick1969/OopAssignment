package fakecoupangsystem;

import fakecoupangsystem.domain.Order;
import fakecoupangsystem.domain.Product;
import fakecoupangsystem.domain.dto.OrderDto;
import fakecoupangsystem.service.LogisticsService;
import fakecoupangsystem.service.LookupService;
import fakecoupangsystem.service.OrderService;
import fakespring.AnnotationFakeSpring;
import fakespring.FakeSpring;
import fakecoupangsystem.launch.FakeCoupangSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by Coupang on 2016. 1. 3..
 */
public class FakeCoupangIntegratedTest {

	private FakeCoupangSystem fakeCoupangSystem;
	private LogisticsService logisticsService;
	private LookupService lookupService;
	private OrderService orderService;
	private FakeSpring fakeSpring;

	long orderId = 0;

	@Before
	public void init() {
		fakeSpring = new AnnotationFakeSpring();
		//fakecoupangsystem.**.*
		fakeSpring.assemble("fakecoupangsystem");

		fakeCoupangSystem = fakeSpring.getBean(FakeCoupangSystem.class);
		logisticsService = fakeSpring.getBean(LogisticsService.class);
		lookupService = fakeSpring.getBean(LookupService.class);
		orderService = fakeSpring.getBean(OrderService.class);

		assertThat(fakeCoupangSystem, notNullValue());
		assertThat(logisticsService, notNullValue());
		assertThat(lookupService, notNullValue());
		assertThat(orderService, notNullValue());
	}

	@After
	public void clean() {
		fakeSpring.shutdown();
	}

	//@Test
	public void serviceLayerTest() {

		int elemNum = 100;

		for(int i = 0 ; i<elemNum ; i++ ) {
			orderService.orderProducts(getOrder());
		}


		//======== Order Service Test
		OrderDto orderDto = lookupService.lookupByOrderId(10);
		validOrderDto(orderDto);


		List<OrderDto> orderDtos = lookupService.lookupByCoupangId("coupangId" + 17);
		assertThat(orderDtos.size(), is(1));


		//======== Logistics Service Test
		Order changeOrder = new Order();
		changeOrder.setOrderId(13);
		changeOrder.setDeliverStatus(Order.DeliverStatus.END);

		logisticsService.updateLogisticsStatus(changeOrder);

		OrderDto afterChangedDto = lookupService.lookupByOrderId(13);
		assertThat(afterChangedDto.getDeliverStatus(), is(Order.DeliverStatus.END));


	}

	public void validOrderDto(OrderDto orderDto) {
		long orderId = orderDto.getOrderId();
		assertThat(orderDto.getDeliverStatus(), is(Order.DeliverStatus.NONE));
		assertThat(orderDto.getOrdererName(), is("ordererName" + orderId));
		assertThat(orderDto.getCoupangId(), is("coupangId" + orderId));

		List<Product> products = orderDto.getProducts();
		for(int i = 0 ; i<products.size() ; i++ ) {
			Product product = products.get(i);
			assertThat(i, is(product.getId()));
		}
	}

	public Order getOrder() {
		Order order = new Order();
		order.setCoupangId("coupangId" + orderId);
		order.setDeliverStatus(Order.DeliverStatus.NONE);
		order.setOrdererName("ordererName" + orderId);

		for(int i = 0 ; i<5 ; i++ ) {
			Product product = new Product();
			product.setId(i);
			order.addProduct(i);
		}

		orderId++;
		return order;
	}


}
