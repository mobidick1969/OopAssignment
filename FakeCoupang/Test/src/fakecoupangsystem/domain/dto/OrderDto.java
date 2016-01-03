package fakecoupangsystem.domain.dto;

import fakecoupangsystem.domain.Order;
import fakecoupangsystem.domain.Product;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coupang on 2016. 1. 3..
 */
public class OrderDto {
	private long orderId;
	private String coupangId;
	private String ordererName;
	private Order.DeliverStatus deliverStatus;
	private List<Product> products = new ArrayList<Product>(5);

	public void setFromOrder(Order order) {
		this.orderId = order.getOrderId();
		this.coupangId = order.getCoupangId();
		this.ordererName = order.getOrdererName();
		this.deliverStatus = order.getDeliverStatus();
	}

	public void addProduct(Product product) {
		products.add(product);
	}

	public long getOrderId() {
		return orderId;
	}

	public String getCoupangId() {
		return coupangId;
	}

	public String getOrdererName() {
		return ordererName;
	}

	public Order.DeliverStatus getDeliverStatus() {
		return deliverStatus;
	}

	public List<Product> getProducts() {
		return products;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("==================" + "\n");

		buffer.append("Order Id : " + orderId + "\n");
		buffer.append("Coupang Id : " + coupangId + "\n");
		buffer.append("Orderer Name : " + ordererName + "\n");
		buffer.append("Deliver Status : " + deliverStatus + "\n");


		for(Product product : products ) {
			buffer.append("*******************\n");
			buffer.append("Product Name : " + product.getName() + "\n");
			buffer.append("Product Cost : " + product.getCost() + "\n");
			buffer.append("Product Company : " + product.getCompany() + "\n");
			buffer.append("Product Description : " + product.getDescription() + "\n");
			buffer.append("*******************\n");
		}


		buffer.append("==================\n\n");

		return buffer.toString().trim();
	}
}
