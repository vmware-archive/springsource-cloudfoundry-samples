package org.grails.twitter

/**
 * A AMQP listener that keeps search indexes in sync across application
 * instances.
 */
class SyncService {
    static rabbitSubscribe = "search.sync"
    static transactional = false

    def grailsApplication
    def searchableService

    /**
     * Expects messages of the form "&lt;id>:&lt;class>" and reindexes the
     * corresponding domain instance.
     */
    void handleMessage(String message) {
        def parts = message.split(/:/)
        if (parts.size() != 2) {
            log.error "Invalid message: $message"
            return
        }

        def domainClass = grailsApplication.getDomainClass(parts[1])
        log.debug "Reindexing instance ${parts[0]} of ${parts[1]}"
        try {
            searchableService.reindex(domainClass.clazz.get(parts[0]))
        }
        catch (Exception ex) {
            log.error "Failed to index instance ${parts[0]} of ${parts[1]}", ex
        }
    }
}
