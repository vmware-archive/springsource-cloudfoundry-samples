package org.grails.twitter.auth

import grails.plugins.springsecurity.Secured

@Secured('IS_AUTHENTICATED_FULLY')
class PersonController {
    static scaffold = Person
}
