package org.grails.twitter

import org.grails.twitter.auth.Person

class StatusService {
    def springSecurityService
    def tagService
    
    void updateStatus(long userId, String message) {
        def status = new Status(message: message, authorId: userId).save(flush: true, failOnError: true)
        rabbitSend 'search.sync', '', "${status.id}:${status.class.name}"
        log.info "Event: reindex status ${status.id}"
        
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
        def messages = Status.whereAny {
            authorId == per.id
            if (per.followed) authorId in per.followed*.id
        }.order 'dateCreated', 'desc'

        return messages.list(max: maxMessages)
    }

    def postsByTag(String tagName, int maxMessages) {
        maxMessages = maxMessages ?: 20
        def messages = Status.findAllByTags(tagName.toLowerCase(), [max: maxMessages, sort: 'dateCreated', order: 'desc'])
        return messages
    }
}
