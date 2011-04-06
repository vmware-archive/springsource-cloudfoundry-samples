package org.springframework.samples.travel.client.android.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.math.BigDecimal;

/**
 * Simple XML-based client side classes that mirror the server-side entities.
 * <p/>
 * Hotel information is represented through this class.
 *
 * @author Josh Long
 */
@Root
public class Hotel {

	@Attribute(required = false)
	private BigDecimal price;

	@Attribute(required = false)
	private Long id;

	@Attribute(required = false)
	private String name;

	@Attribute(required = false)
	private String address;

	@Attribute(required = false)
	private String city;

	@Attribute(required = false)
	private String state;

	@Attribute(required = false)
	private String zip;

	@Attribute(required = false)
	private String country;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Hotel(" + this.name + "," + this.address + "," + this.city + "," + this.zip + " at the price of " + this.price + ")";
	}
}
