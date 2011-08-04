package org.cloudfoundry.picalc.messaging;

public class WorkMessage {

	private int start;

	private int nrOfElements;
	
	private String correlationId;


	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getNrOfElements() {
		return nrOfElements;
	}

	public void setNrOfElements(int nrOfElements) {
		this.nrOfElements = nrOfElements;
	}


	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	@Override
	public String toString() {
		return "WorkMessage [start=" + start + ", nrOfElements=" + nrOfElements
				+ ", correlationId=" + correlationId + "]";
	}
	

}
