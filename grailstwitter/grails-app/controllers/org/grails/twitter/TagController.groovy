package org.grails.twitter

class TagController {
    def tagService
    
    def index() {
        return [tags: tagService.getTags() ]
    }
}
