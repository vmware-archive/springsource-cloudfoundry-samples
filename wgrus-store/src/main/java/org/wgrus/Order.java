package org.wgrus;

public class Order {

	private final long id;
	private int quantity;
	private String productId;
	private String email;

	public Order(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public String getProductId() {
		return productId;
	}
	
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

}
