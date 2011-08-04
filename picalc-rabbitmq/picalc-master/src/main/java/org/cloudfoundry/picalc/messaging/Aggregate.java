package org.cloudfoundry.picalc.messaging;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Aggregate {

	private static final Logger logger = LoggerFactory.getLogger(Aggregate.class);
	
	
	private CountDownLatch latch;
	private double result;
	
	// For logging
	private double completionNumber;
	private String correlationId;
	
	public Aggregate(String correlationId, int completionNumber) {
		this.correlationId = correlationId;
		this.completionNumber = completionNumber;
		latch = new CountDownLatch(completionNumber);
	}
	
	public void process(double value) {
		synchronized (this) {
			result += value;
		}
		double percentComplete = 100 - ((latch.getCount() / completionNumber) * 100);
		if (  percentComplete % 5 == 0) {
			logger.info( percentComplete + " % complete, correlation id = " + correlationId);
		}
		latch.countDown();
	}
	
	public synchronized double getResult() {
		return result;
	}
	
	public void await() throws InterruptedException {
		logger.info("Wating up to 20 seconds for reply to " + completionNumber + " messages for correlation Id = " + correlationId);
		latch.await(20, TimeUnit.SECONDS);
		logger.info("Done waiting.");
	}
}
