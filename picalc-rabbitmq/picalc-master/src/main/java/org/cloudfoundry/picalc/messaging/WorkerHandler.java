package org.cloudfoundry.picalc.messaging;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerHandler {

	private static final Logger logger = LoggerFactory.getLogger(WorkerHandler.class);
	private int instanceIndex = -1;
	public WorkerHandler() {
		CloudEnvironment environment = new CloudEnvironment();
		if (environment.getInstanceInfo() != null) {
			instanceIndex = environment.getInstanceInfo().getInstanceIndex();
		}
		
	}
	
	private double calculatePiFor(int start, int nrOfElements) {
		double acc = 0.0;
		for (int i = start * nrOfElements; i <= ((start + 1) * nrOfElements - 1); i++) {
			acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
		}
		return acc;
	}

	public ResultMessage handleMessage(WorkMessage workMessage) {

		int startNumber = workMessage.getStart();
		if (startNumber % 100 == 0) {
			logger.info("Worker " + "(" + instanceIndex + ") Received " + workMessage);
		}

		
		double result = calculatePiFor(workMessage.getStart(), workMessage.getNrOfElements());

		ResultMessage resultMessage = new ResultMessage();
		resultMessage.setCorrelationId(workMessage.getCorrelationId());
		resultMessage.setResult(result);
		
		//logger.debug("Working Returning " + resultMessage);

		return resultMessage;
	}

}
