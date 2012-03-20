package org.grails.twitter

import grails.plugins.springsecurity.Secured
import org.grails.twitter.auth.Person

@Secured('IS_AUTHENTICATED_REMEMBERED')
class StatusController {

    def springSecurityService
    def statusService

    def index() {
        def currentUser = lookupPerson()
        def messages = statusService.currentTimeline(currentUser.id)
        [statusMessages: messages, currentUser: currentUser, moreMessagesAction: "fetchMessagesForUser"]
    }
    
    def tag() {
        def currentUser = lookupPerson()
        def messages = statusService.postsByTag(params.id, params.max?.toInteger() ?: 0)
        assert currentUser != null
        [tag: params.id,
         statusMessages: messages,
         currentUser: currentUser,
         moreMessagesAction: "fetchMessagesForTag",
         moreMessagesId: params.id]
    }

    def updateStatus() {
        try {
            statusService.updateStatus lookupPersonId(), params.message

            def messages = statusService.currentTimeline(lookupPersonId(), params.max?.toInteger() ?: 0)
            render template: 'statusMessages', collection: messages, var: 'statusMessage'
        }
        catch (Exception ex) {
            log.error "Failed to update status", ex
            render "Message is too long"
        }
    }

    def fetchMessagesForUser() {
        def messages = statusService.currentTimeline(lookupPersonId(), params.max?.toInteger() ?: 0)
        render template: 'statusMessages', collection: messages, var: 'statusMessage'
    }

    def fetchMessagesForTag() {
        def messages = statusService.postsByTag(params.id, params.max?.toInteger() ?: 0)
        render template: 'statusMessages', collection: messages, var: 'statusMessage'
    }

    def follow() {
        statusService.follow lookupPersonId(), params.long('id')
        redirect action: 'index'
    }
    
    private lookupPerson() {
        return Person.get(lookupPersonId())
    }

    private lookupPersonId() {
        return springSecurityService.principal.id
    }
}
