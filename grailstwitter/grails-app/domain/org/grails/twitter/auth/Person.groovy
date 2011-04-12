package org.grails.twitter.auth

class Person {

    String realName
    String username
    String password
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    static hasMany = [followed:Person]
    static searchable = [only: ['username', 'realName']]

    static constraints = {
        username blank: false, unique: true
        password blank: false
    }

    static mapping = {
        password column: '`password`'
}

    Set<Authority> getAuthorities() {
        PersonAuthority.findAllByPerson(this).collect { it.authority } as Set
    }
}
