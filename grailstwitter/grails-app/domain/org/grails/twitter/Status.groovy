package org.grails.twitter

import org.grails.twitter.auth.Person

class Status {
    static mapWith = "mongo"
    static transients = ["author"]
	
    String message
    Long authorId
    List<String> tags = []
    Date dateCreated
	
    Person getAuthor() {
        return Person.get(authorId)
    }

    static constraints = {
        message maxSize: 160
    }
}
