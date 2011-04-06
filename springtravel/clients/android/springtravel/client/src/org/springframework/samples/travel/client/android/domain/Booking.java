package org.springframework.samples.travel.client.android.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.springframework.samples.travel.client.android.services.misc.MapBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Simple XML-based client side classes that mirror the server-side entities.
 * <p/>
 * Hotel information is represented through this class.
 *
 * @author Josh Long
 */
@Root
public class Booking {

	@Attribute(required = false)
	private Long id;

	@Element(required = false)
	private User user;

	@Element(required = false)
	private Hotel hotel;

	@Attribute(required = false)
	private Date checkinDate;

	@Attribute(required = false)
	private Date checkoutDate;

	@Attribute(required = false)
	private String creditCard;

	@Attribute(required = false)
	private String creditCardName;

	@Attribute(required = false)
	private int creditCardExpiryMonth;

	@Attribute(required = false)
	private int creditCardExpiryYear;

	@Attribute(required = false)
	private boolean smoking;

	@Attribute(required = false)
	private int beds;

	@Attribute(required = false)
	private int nights;

	@Attribute(required = false)
	private double total;

	@ElementList(required = false	/*inline = true */, name = "amenities")
	private ArrayList<Amenity> amenities;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Date getCheckinDate() {
		return checkinDate;
	}

	public void setCheckinDate(Date checkinDate) {
		this.checkinDate = checkinDate;
	}

	public Date getCheckoutDate() {
		return checkoutDate;
	}

	public void setCheckoutDate(Date checkoutDate) {
		this.checkoutDate = checkoutDate;
	}

	public String getCreditCard() {
		return creditCard;
	}

	public int getNights() {
		return nights;
	}

	public void setNights(int nights) {
		this.nights = nights;
	}

	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}

	public String getCreditCardName() {
		return creditCardName;
	}

	public void setCreditCardName(String creditCardName) {
		this.creditCardName = creditCardName;
	}

	public int getCreditCardExpiryMonth() {
		return creditCardExpiryMonth;
	}

	public void setCreditCardExpiryMonth(int creditCardExpiryMonth) {
		this.creditCardExpiryMonth = creditCardExpiryMonth;
	}

	public int getCreditCardExpiryYear() {
		return creditCardExpiryYear;
	}

	public void setCreditCardExpiryYear(int creditCardExpiryYear) {
		this.creditCardExpiryYear = creditCardExpiryYear;
	}

	public boolean isSmoking() {
		return smoking;
	}

	public void setSmoking(boolean smoking) {
		this.smoking = smoking;
	}

	public int getBeds() {
		return beds;
	}

	public void setBeds(int beds) {
		this.beds = beds;
	}

	public ArrayList<Amenity> getAmenities() {
		return amenities;
	}

	public void setAmenities(Amenity... ams) {
		this.setAmenities(new ArrayList<Amenity>(Arrays.asList(ams)));
	}

	public void setAmenities(ArrayList<Amenity> amenities) {
		this.amenities = amenities;
	}

	// not required
	// todo make sure the derived values are set on return from the server: nights and total
	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	private String keyValue(String k, Object v) {
		return String.format("%s=%s; ", k, v + "");
	}

	private String mapBasedToString(Map<String, Object> kvs) {
		StringBuilder stringBuilder = new StringBuilder();

		for (String k : kvs.keySet())
			stringBuilder.append(keyValue(k, kvs.get(k)));

		return "(" + stringBuilder.toString() + ")";
	}

	@Override
	public String toString() {

		Map<String, Object> ccKvs = MapBuilder.<String, Object>map().
				param("expirationYear", this.creditCardExpiryYear).
				param("expirationMonth", this.creditCardExpiryMonth).
				param("name", this.creditCardName).
				param("card", this.creditCard).
				build();

		Map<String, Object> bookingKvs = MapBuilder.<String, Object>map().
				param("id", this.id).
				param("hotel", hotel).
				param("user", this.user).
				param("amenities", amenities).
				param("checkin", this.checkinDate).
				param("checkout", this.checkoutDate).
				param("smoking", this.smoking).
				param("beds", this.beds).
				param("cc", "CreditCard" + mapBasedToString(ccKvs)).
				build();

		return "Booking" + mapBasedToString(bookingKvs);
	}
}

/*
	static public void main(String[] args) throws Throwable {

		MarshallingUtilities marshallingUtilities = new MarshallingUtilities();

		String bookingXml = " <booking total=\"350.00\" smoking=\"false\" nights=\"1\" id=\"262144\" creditCardExpiryYear=\"0\" creditCardExpiryMonth=\"0\" checkoutDate=\"2011-03-25 00:00:00.0 PDT\" checkinDate=\"2011-03-24 00:00:00.0 PDT\" beds=\"0\">\n" +
						"<amenities/>\n" +
						"<hotel zip=\"90069\" state=\"CA\" price=\"350.00\" name=\"The Standard\" id=\"24\" country=\"USA\" city=\"Los Angeles\" address=\"8300 Sunset Boulevard West Hollywood\"/>\n" +
						"<user username=\"josh\" name=\"Josh\"/>\n" +
						"</booking>";

		Booking booking = marshallingUtilities.from(Booking.class, bookingXml);

		String roundTrip = marshallingUtilities.to(booking);

		System.out.print("the xml is: " + roundTrip);
	}
 */
