import static daily.groove.Constants.*

class BootStrap {
    private static final SAMPLE_FEEDS = [
            "http://feeds.bbci.co.uk/news/rss.xml": "BBC News",
            "http://www.theregister.co.uk/headlines.rss": "The Register",
            "http://www.theonion.com/feeds/daily/": "The Onion",
            "http://grails.org/plugin/latest?format=rss": "Grails Plugins",
            "http://groovyblogs.org/feed/rss": "Groovy Blogs",
            "http://rss.slashdot.org/Slashdot/slashdot": "Slashdot",
            "http://blog.springsource.com/feed/": "SpringSource Team Blog",
            "http://news.sky.com/sky-news/rss/home/rss.xml": "Sky News",
            "http://feeds.dzone.com/javalobby/frontpage": "JavaLobby",
            "http://feeds.pheedo.com/techtarget/tsscom/home": "The Server Side"]

    def redis
    
    def init = { servletContext ->
        // Clear the database and start afresh...
        redis.flushall()
        
        if (!redis.scard(SAMPLE_FEEDS_KEY)) {
            SAMPLE_FEEDS.each { url, name ->
                redis.set url, name
                redis.sadd SAMPLE_FEEDS_KEY, url
            }
        }
    }

    def destroy = {
    }
}
