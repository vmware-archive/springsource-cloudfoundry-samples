package org.grails.twitter

import org.grails.twitter.auth.Person

class StatusService {
    def springSecurityService
    def tagService
    
    void updateStatus(long userId, String message) {
        def status = new Status(message: message, authorId: userId).save(flush: true, failOnError: true)
        rabbitSend 'search.sync', '', "${status.id}:${status.class.name}"
        
        runAsync {
            tagService.extractTagsFromMessage(status)
        }
    }

    void follow(long userId, long followId) {
        def person = Person.get(followId)
        if (person) {
            def currentUser = Person.get(userId)
            currentUser.addToFollowed(person)
        }
    }
    
    def currentTimeline(long userId) {
        currentTimeline(userId, 20)
    }

    def currentTimeline(long userId, int maxMessages) {
        maxMessages = maxMessages ?: 20
        def per = Person.get(userId)
        def messages = Status.withCriteria {
            or {
                eq 'authorId', per.id
                if (per.followed) {
                    inList 'authorId', per.followed*.id
                }
            }
            maxResults maxMessages
            order 'dateCreated', 'desc'
        }
        return messages
    }

    def postsByTag(String tagName, int maxMessages) {
        maxMessages = maxMessages ?: 20
        def messages = Status.withCriteria {
            'in'("tags", tagName.toLowerCase())
            maxResults maxMessages
            order 'dateCreated', 'desc'
        }
        return messages
    }
}
