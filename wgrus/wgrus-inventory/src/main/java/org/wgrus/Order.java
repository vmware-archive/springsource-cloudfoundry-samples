package org.wgrus;

public class Order {

	private long id;
	private int quantity;
	private String productId;
	private String email;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String toString() {
		return "id=" + this.id + ", quantity=" + this.quantity + ", product=" + this.productId + ", email=" + this.email; 
	}

}
