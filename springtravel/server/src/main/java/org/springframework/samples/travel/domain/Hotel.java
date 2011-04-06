package org.springframework.samples.travel.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A hotel where users may book stays.
 */
@XmlRootElement
@Entity
public class Hotel implements Serializable {

	private Set<Booking> reservations = new HashSet<Booking>();

	private Long id;

	private String name;

	private String address;

	private String city;

	private String state;

	private String zip;

	private String country;

	private BigDecimal price;

	@Id
	@XmlAttribute
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@XmlAttribute
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@XmlAttribute
	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@XmlAttribute
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@XmlAttribute
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@XmlAttribute
	@Column(precision = 6, scale = 2)
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Booking createBooking(User user) {
		return new Booking(this, user);
	}

	@Override
	public String toString() {
		return "Hotel(" + name + "," + address + "," + city + "," + zip + ")";
	}


	@OneToMany(mappedBy = "hotel")
	@XmlTransient
	@JsonIgnore
	public Set<Booking> getReservations() {
		return reservations;
	}

	public void setReservations(Set<Booking> reservations) {
		this.reservations = reservations;
	}
}
