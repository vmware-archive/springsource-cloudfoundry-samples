package org.cloudfoundry.picalc.messaging;

public class ResultMessage {

	private double result;
	
	private String correlationId;

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
	
	@Override
	public String toString() {
		return "ResultMessage [result=" + result + ", correlationId="
				+ correlationId + "]";
	}
	

}
