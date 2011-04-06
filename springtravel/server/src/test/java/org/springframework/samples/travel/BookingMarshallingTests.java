package org.springframework.samples.travel;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.samples.travel.domain.Amenity;
import org.springframework.samples.travel.domain.Booking;
import org.springframework.samples.travel.domain.Hotel;
import org.springframework.samples.travel.domain.User;
import org.springframework.samples.travel.rest.RestConfiguration;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

public class BookingMarshallingTests {

	public static void main(String args[]) throws Throwable {

		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(RestConfiguration.class);
		Jaxb2Marshaller jaxb2Marshaller = applicationContext.getBean(Jaxb2Marshaller.class);



		Hotel hotel = new Hotel();
		hotel.setAddress("12332 Street St");
		hotel.setCity("Los Angeles, CA");
		hotel.setCountry("USA");
		hotel.setPrice(new BigDecimal(242));
		hotel.setId((long) (Math.random() * 2302));
		hotel.setZip("90210");
		hotel.setName("The Hotel");

		User u = new User();
		u.setName("Name Of User");
		u.setPassword("PassWord");
		u.setUsername("UserName");

		Booking booking = new Booking();
		booking.setAmenities(new HashSet<Amenity>(Arrays.asList(Amenity.LATE_CHECKOUT, Amenity.MINIBAR)));
		booking.setBeds(2);
		booking.setHotel(hotel);
		booking.setId(242L);
		booking.setSmoking(false);
		booking.setCheckinDate(new Date());
		booking.setUser(u);
		booking.setCheckoutDate(new Date());

		String hotelString = asString(jaxb2Marshaller, booking);

		System.out.println(hotelString);
	}

	private static String asString(Marshaller m, Object graph) throws Throwable {
		StringWriter w = null;
		try {
			w = new StringWriter();
			Result result = new StreamResult(w);
			m.marshal(graph, result);
		} finally {
			if (w != null) {
				w.close();
			}
		}
		return w.toString();
	}
}
