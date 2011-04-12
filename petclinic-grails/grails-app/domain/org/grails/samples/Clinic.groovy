package org.grails.samples

class Clinic {
	
	List location
	
	static mapping = {
		location geoIndex:true
	}
}