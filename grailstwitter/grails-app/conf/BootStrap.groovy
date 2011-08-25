import org.grails.twitter.Status
import org.grails.twitter.auth.Authority
import org.grails.twitter.auth.Person
import org.grails.twitter.auth.PersonAuthority

class BootStrap {

    def searchableService
    def springSecurityService

    def init = { servletContext ->
        if (!Person.count()) {
            createData()
        }

        // Index all Hibernate mapped domain classes.
        searchableService.reindex()

        // Index all status messages.
        def statusMessages = Status.list()
        log.info "Indexing ${statusMessages.size()} status messages"
        Status.reindex(statusMessages)
        log.info "Finished indexing"
    }

    def destroy = {
    }

    private void createData() {
        def users = []
        Authority.withTransaction {
            def userRole = new Authority(authority: 'ROLE_USER').save()
    
            String password = springSecurityService.encodePassword('password')
            [jeff: 'Jeff Brown', graeme: 'Graeme Rocher', burt: 'Burt Beckwith', peter: 'Peter Ledbrook'].each { userName, realName ->
                def user = new Person(username: userName, realName: realName, password: password, enabled: true).save()
                PersonAuthority.create user, userRole, true
                users << user
            }
        }
	
        // Create a large number of messages to really exercise MongoDB.
        def sampleMessages = [
                "Going out for a drink tonight",
                "Hacking away on #grails",
                "Bed time",
                "Checking out BBC news",
                "Delivering the #groovy & #grails training course",
                "BBQ time!",
                "Going to a friend's wedding",
                "Team meeting approaching",
                "Test message",
                "Flying to a conference in Copenhagen"]
        def messageCount = 1000
        if (Status.count() < messageCount) {
            def random = new Random()
            def start = System.currentTimeMillis()
            for (int i in 0..<messageCount) {
                def u = users[random.nextInt(users.size())]
                def m = sampleMessages[random.nextInt(sampleMessages.size())]
                new Status(message: m, authorId: u.id).save(failOnError: true, flush: true)
            }
            
            log.info ">> Time taken to load Mongo data: ${(System.currentTimeMillis() - start) / 1000}s"
        }
        else {
            log.info ">> Initial status messages already in database"
        }
    }
}
