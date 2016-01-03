package fakecoupangsystem.domain;

import java.util.Date;

/**
 * Created by Coupang on 2015. 12. 26..
 */
public class Product implements Comparable<Product> {
	private int id;
	private int cost;
	private int deliverCost;
	private String name;
	private String description;
	private String company;
	private Date makeDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getDeliverCost() {
		return deliverCost;
	}

	public void setDeliverCost(int deliverCost) {
		this.deliverCost = deliverCost;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Date getMakeDate() {
		return makeDate;
	}

	public void setMakeDate(Date makeDate) {
		this.makeDate = makeDate;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("    ======== PRODUCT ===========\n\n");
		buffer.append("      Id : " + id + "\n");
		buffer.append("      Cost : " + cost + "\n");
		buffer.append("      Deliver Cost : " + deliverCost + "\n");
		buffer.append("      Name : " + name + "\n");
		buffer.append("      Description : " + description + "\n");
		buffer.append("      Company : " + company + "\n");
		buffer.append("      Create Date : " + makeDate + "\n\n");
		buffer.append("    ======== PRODUCT ===========\n\n");

		return buffer.toString();
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Product product = (Product) o;

		return id == product.id;

	}

	@Override public int hashCode() {
		return id;
	}

	@Override
	public int compareTo(Product o) {
		return (int)(id - o.id);
	}
}
