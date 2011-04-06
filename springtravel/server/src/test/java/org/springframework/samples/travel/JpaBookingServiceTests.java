package org.springframework.samples.travel;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.samples.travel.domain.Booking;
import org.springframework.samples.travel.domain.Hotel;
import org.springframework.samples.travel.services.JpaBookingService;
import org.springframework.samples.travel.services.SearchCriteria;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Set;

public class JpaBookingServiceTests {
	public static void main(String[] args) throws Throwable {

		ApplicationContext annotationConfigApplicationContext
				= new ClassPathXmlApplicationContext("/services.xml");
		PlatformTransactionManager transactionManager = annotationConfigApplicationContext.getBean(PlatformTransactionManager.class);

		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.afterPropertiesSet();

		final JpaBookingService jpaBookingService = annotationConfigApplicationContext.getBean(JpaBookingService.class);

		final SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setPage(0);
		searchCriteria.setPageSize(100);
		searchCriteria.setSearchString("standard");
		searchCriteria.setMaximumPrice(43);

		transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				List<Hotel> hotels = jpaBookingService.findHotels(searchCriteria);
				for (Hotel h : hotels) {
					System.out.println("-------------------------------");
					System.out.println(h.toString());
					Set<Booking> bookings = h.getReservations();
					for (Booking b : bookings) {
						System.out.println(b.toString());
					}
				}
				return null;
			}
		});
	}
}
