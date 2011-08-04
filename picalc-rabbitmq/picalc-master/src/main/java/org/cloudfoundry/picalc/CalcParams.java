package org.cloudfoundry.picalc;

public class CalcParams {

	int nrOfMessages;

	public int getNrOfMessages() {
		return nrOfMessages;
	}

	public void setNrOfMessages(int nrOfMessages) {
		this.nrOfMessages = nrOfMessages;
	}

	@Override
	public String toString() {
		return "CalcParams [nrOfMessages=" + nrOfMessages + "]";
	}
	
	
	
}
