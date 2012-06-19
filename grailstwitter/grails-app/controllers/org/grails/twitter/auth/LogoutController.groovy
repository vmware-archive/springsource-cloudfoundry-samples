package org.grails.twitter.auth
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

import grails.plugins.springsecurity.Secured

@Secured('IS_AUTHENTICATED_ANONYMOUSLY')
class LogoutController {

    /**
     * Index action. Redirects to the Spring security logout uri.
     */
    def index() {
        // TODO  put any pre-logout code here
        redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl // '/j_spring_security_logout'
    }
}
