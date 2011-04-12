package org.grails.samples

class ClinicController {

	def index = {}
	
	def vets = {
		[vets: Vet.list() ]
	}

	def mongo
	
	def test = {
		def clinic = Clinic.findByLocationNear([150, 160])
		render "Yes it is !${clinic['name']}"
	}
}