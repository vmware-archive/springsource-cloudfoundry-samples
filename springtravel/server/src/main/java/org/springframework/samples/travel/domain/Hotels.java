package org.springframework.samples.travel.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlRootElement    (name = "hotels")
public class Hotels {
	private List<Hotel> hotels ;

	public Hotels() {
		hotels = new ArrayList<Hotel>();
	}

	public Hotels( Collection<Hotel> hotels) {
		this.hotels = new ArrayList<Hotel>(hotels);
	}

	@XmlElement(name = "hotel", required = true, nillable = false)
//	@XmlElementWrapper
	public List<Hotel> getHotels() {
		return hotels;
	}

	public void setHotels(List<Hotel> hotels) {
		this.hotels = hotels;
	}
}
