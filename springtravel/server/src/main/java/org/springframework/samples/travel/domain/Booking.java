package org.springframework.samples.travel.domain;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.travel.domain.util.DateFormatXmlAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A Hotel Booking made by a User.
 */
@XmlRootElement //(name = "BookAuthors") //, namespace = DomainConstants.NAMESPACE)
@Entity
public class Booking implements Serializable {

	private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

	private Long id;

	private User user;

	private Hotel hotel;


	@DateTimeFormat(pattern = "MM-dd-yyyy")
	private Date checkinDate;

	@DateTimeFormat(pattern = "MM-dd-yyyy")
	private Date checkoutDate;

	private String creditCard;

	private String creditCardName;

	private int creditCardExpiryMonth;

	private int creditCardExpiryYear;

	private boolean smoking;

	private int beds;

	private Set<Amenity> amenities = new HashSet<Amenity>();

	public Booking() {
		Calendar calendar = Calendar.getInstance();
		setCheckinDate(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		setCheckoutDate(calendar.getTime());
	}

	public Booking(Hotel hotel, User user) {
		this();
		this.hotel = hotel;
		this.user = user;
	}

	@XmlAttribute
	@Transient
	public BigDecimal getTotal() {
		return hotel.getPrice().multiply(new BigDecimal(getNights()));
	}

	@XmlAttribute
	@Transient
	public int getNights() {
		if (checkinDate == null || checkoutDate == null) {
			return 0;
		} else {
			return (int) (checkoutDate.getTime() - checkinDate.getTime())
					/ 1000 / 60 / 60 / 24;
		}
	}

	@XmlAttribute
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlJavaTypeAdapter( DateFormatXmlAdapter.class)
	@XmlAttribute
	@Basic
	@Temporal(TemporalType.DATE)
	public Date getCheckinDate() {
		return checkinDate;
	}

	public void setCheckinDate(Date datetime) {
		this.checkinDate = datetime;
	}

	@ManyToOne
	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	@ManyToOne
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@XmlJavaTypeAdapter( DateFormatXmlAdapter.class)
	@XmlAttribute
	@Basic
	@Temporal(TemporalType.DATE)
	public Date getCheckoutDate() {
		return checkoutDate;
	}

	public void setCheckoutDate(Date checkoutDate) {
		this.checkoutDate = checkoutDate;
	}

	@XmlAttribute
	public String getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}

	@Transient
	public String getDescription() {

		return hotel == null ? null : hotel.getName() + ", "
				+ dateFormat.format(getCheckinDate()) + " to "
				+ dateFormat.format(getCheckoutDate());
	}

	@XmlAttribute
	public boolean isSmoking() {
		return smoking;
	}

	public void setSmoking(boolean smoking) {
		this.smoking = smoking;
	}

	@XmlAttribute
	public int getBeds() {
		return beds;
	}

	public void setBeds(int beds) {
		this.beds = beds;
	}

	@XmlAttribute
	public String getCreditCardName() {
		return creditCardName;
	}

	public void setCreditCardName(String creditCardName) {
		this.creditCardName = creditCardName;
	}

	@XmlAttribute
	public int getCreditCardExpiryMonth() {
		return creditCardExpiryMonth;
	}

	public void setCreditCardExpiryMonth(int creditCardExpiryMonth) {
		this.creditCardExpiryMonth = creditCardExpiryMonth;
	}

	@XmlAttribute
	public int getCreditCardExpiryYear() {
		return creditCardExpiryYear;
	}

	public void setCreditCardExpiryYear(int creditCardExpiryYear) {
		this.creditCardExpiryYear = creditCardExpiryYear;
	}

	@Transient
	@XmlElement(name = "amenity", required = true, nillable = false)
	@XmlElementWrapper
	public Set<Amenity> getAmenities() {
		return amenities;
	}

	public void setAmenities(Set<Amenity> amenities) {
		this.amenities = amenities;
	}

	// TODO replace with JSR 303
	public void validateEnterBookingDetails(ValidationContext context) {
		MessageContext messages = context.getMessageContext();
		if (checkinDate.before(today())) {
			messages.addMessage(new MessageBuilder().error().source(
					"checkinDate").code("booking.checkinDate.beforeToday")
					.build());
		} else if (checkoutDate.before(checkinDate)) {
			messages.addMessage(new MessageBuilder().error().source( "checkoutDate").code( "booking.checkoutDate.beforeCheckinDate").build());
		}
	}

	private Date today() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	@Override
	public String toString() {
		return "Booking(" + user + "," + hotel +"; from "  +dateFormat.format(getCheckinDate())+" to "+ dateFormat.format(getCheckoutDate())+ ")";
	}
}
