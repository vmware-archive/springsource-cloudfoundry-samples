package org.cloudfoundry.picalc.messaging;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterHandler {

	private static final Logger logger = LoggerFactory.getLogger(MasterHandler.class);

	Map<String, Aggregate> activeAggregates = new HashMap<String, Aggregate>();
	
	public void handleMessage(ResultMessage resultMessage) {
		
		//get a correlation Id from incoming message
		String corrId = resultMessage.getCorrelationId();
		
		Aggregate aggregate = activeAggregates.get(corrId);			
		
		double result = resultMessage.getResult();
		logger.debug("Received: " + result);
		if (aggregate != null) {
			aggregate.process(result);
		} else {
			logger.error("Could not find aggregate processor for correlation Id = " + corrId);
		}

	}
	
	public void registerAggregate(String correlationId, int completionNumber) {
		activeAggregates.put(correlationId, new Aggregate(correlationId, completionNumber));
		logger.debug("registered Aggregate under correlation Id = " + correlationId);		
	}
	
	public void await(String correlationId) throws InterruptedException {
		Aggregate aggregate = activeAggregates.get(correlationId);		
		if (aggregate != null) {
			aggregate.await();
		} else {
			logger.error("Could not 'await' for correlationId = " + correlationId);
		}
			
	}
	
	public double getResult(String correlationId) {
		Aggregate aggregate = activeAggregates.get(correlationId);	
		if (aggregate != null) {
			return aggregate.getResult();
		} else {
			logger.error("Could not 'getResult' for correlationId = " + correlationId);
			return 0;
		}
	}
	
}
