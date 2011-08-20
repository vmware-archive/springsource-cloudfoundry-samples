package org.grails.twitter.auth

import grails.plugins.springsecurity.Secured

@Secured('IS_AUTHENTICATED_FULLY')
class PersonController {
    static scaffold = Person

    @Secured('IS_AUTHENTICATED_REMEMBERED')
    def search = {
        def query = "+(alias:Person)"
        if (params.q) query += " +(${params.q})"

        redirect controller: "searchable", action: "index", params: [q: query]
    }
}
