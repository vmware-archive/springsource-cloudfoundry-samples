package org.grails.twitter

class TagService {
    static final TAGS_CACHE_KEY = "tagList"
     
    static transactional = false
    
    def redisService

    def extractTagsFromMessage(Status status) {
        status = Status.get(status.id)
        try {
            // Scan the status message for tags, i.e. text beginning with a '#'.
            def m = status.message =~ /#(\w+)/
            def tags = [] as Set
            for (match in m) {
                // Limit tags to 20 characters or less.
                if (match[1].size() <= 20) {
                    tags << match[1]
                }
            }
            
            if (!tags) return
        
            log.debug "Adding tags '${tags.join(', ')}' to status ${status.id}"
            status.tags.addAll tags
            status.message += ' '   // HACK
            status.save(flush: true, failOnError: true)
            
            // Update tag cache in Redis.
            cacheTags()
        }
        catch (e) {
            e.printStackTrace()
        }
    }
    
    def cacheTags() {
        def result = Status.collection.mapReduce("""\
                function map() {
                    if (!this.tags) {
                        return;
                    }

                    for (index in this.tags) {
                        emit(this.tags[index], 1);
                    }
                }
                """.stripIndent(),
                """\
                function reduce(key, values) {
                    var count = 0;

                    for (index in values) {
                        count += values[index];
                    }

                    return count;
                }
                """.stripIndent(),
                "mrresult",
                [:])

        redisService.withTransaction { txn ->
            txn.del TAGS_CACHE_KEY
            for (r in result.results()) {
                txn.zadd TAGS_CACHE_KEY, r.value.toDouble(), r._id 
            }
        }
    }
    
    Map getTags() {
        def tags = getTagsInternal()
        if (!tags) {
            cacheTags()
            tags = getTagsInternal()
        }
        if (tags) {
            // Return a map of tag names -> tag counts. At this point,
            // 'tags' is a list of tag names, so we use redis.zscore()
            // to get the associated tag count.
            return tags.reverse().inject([:]) { map, k -> map[k] = redisService.zscore(TAGS_CACHE_KEY, k).toInteger(); return map }
        }
        else {
            return [:]
        }
    }

    private getTagsInternal() {
        return redisService.zrange(TAGS_CACHE_KEY, 0, -1) as List
    }
}
