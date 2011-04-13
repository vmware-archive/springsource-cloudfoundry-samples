package org.grails.twitter

class TagService {
    static final TAGS_CACHE_KEY = "tagList"
     
    static transactional = false
    
    def redis

    def extractTagsFromMessage(Status status) {
        try {
            // Scan the status message for tags, i.e. text beginning with a '#'.
            def m = status.message =~ /#(\w+)/
            def tags = [] as Set
            for (match in m) {
                tags << match[1]
            }
            
            if (!tags) return
        
            log.debug "Adding tags '${tags.join(', ')}' to status ${status.id}"
            status.tags.addAll tags
            status.save(flush: true, failOnError: true)
            
            // Update tag cache in Redis.
            cacheTags()
        }
        catch (e) {
            e.printStackTrace()
        }
    }
    
    def cacheTags() {
        try {
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

            redis.multi()
            redis.del TAGS_CACHE_KEY
            for (r in result.results()) {
                redis.zadd TAGS_CACHE_KEY, r.value.toDouble(), r._id 
            }
            redis.exec()
        }
        finally {
            redis.close()			
        }
    }
    
    Map getTags() {
        try {
            def tags = getTagsInternal()
            if (!tags) {
                cacheTags()
                tags = getTagsInternal()
            }
            if (tags) {
                return tags.reverse().inject([:]) { map, k -> map[k] = redis.zscore(TAGS_CACHE_KEY, k).toInteger(); return map }
            }
            else {
                return [:]
            }
        }
        finally {
            redis.close()
        }
    }

    private getTagsInternal() {
        return redis.zrange(TAGS_CACHE_KEY, 0, -1) as List
    }
}
