package org.springframework.samples.travel.client.android.domain;

import org.simpleframework.xml.Root;

@Root
public enum Amenity {
	//todo remember 'smoking' isnt a proper amenity, but well display it as one
	OCEAN_VIEW, LATE_CHECKOUT, MINIBAR, SMOKING
}
