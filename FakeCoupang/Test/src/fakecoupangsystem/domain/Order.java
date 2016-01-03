package fakecoupangsystem.domain;

import fakecoupangsystem.database.annotation.Column;
import fakecoupangsystem.database.annotation.Data;
import fakecoupangsystem.database.annotation.Index;
import fakecoupangsystem.database.annotation.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coupang on 2015. 12. 30..
 */
@Data(name="SampleOrder")
public class Order implements Comparable<Order> {
	public enum DeliverStatus {
		NONE, //배송 시작전
		ING, //배송 중
		END; //배송 끝끝

		@Override
		public String toString() {
			return "" + this.ordinal();
		}
	}

	@Key
	@Column(order=0)
	private long orderId;

	@Index
	@Column(order=1)
	private String coupangId;

	@Column(order=2)
	private String ordererName;

	@Column(order=3)
	private DeliverStatus deliverStatus;

	@Column(order=4)
	private List<Integer> products = new ArrayList<Integer>(5);

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public String getCoupangId() {
		return coupangId;
	}

	public void setCoupangId(String coupangId) {
		this.coupangId = coupangId;
	}

	public String getOrdererName() {
		return ordererName;
	}

	public void setOrdererName(String ordererName) {
		this.ordererName = ordererName;
	}

	public DeliverStatus getDeliverStatus() {
		return deliverStatus;
	}

	public void setDeliverStatus(DeliverStatus deliverStatus) {
		this.deliverStatus = deliverStatus;
	}

	public List<Integer> getProducts() {
		return products;
	}

	public void setProducts(List<Integer> products) {
		this.products = products;
	}

	public void addProduct(int productId) {
		products.add(productId);
	}

	public void addProduct(Product product) {
		addProduct(product.getId());
	}

	@Override
	public int compareTo(Order o) {
		return (int)(orderId - o.orderId);
	}
}
