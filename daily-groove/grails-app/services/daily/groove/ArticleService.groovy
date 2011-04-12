package daily.groove

import static daily.groove.Constants.*

class ArticleService {

    static transactional = false

    def redis

    def loadFeed(url) {
        def feedTitle = saveArticles(url)

        // Add the feed URL to the database. If it's one of the sample feeds
        // then the URL key will already be there - in which case we don't
        // want to add it again.
        if (!redis.exists(url)) {
            redis.set url, feedTitle
        }
        redis.sadd SUBSCRIBED_FEEDS_KEY, url
    }

    def refreshFeed(url) {
        if (!redis.exists(url)) {
            throw new IllegalStateException("Cannot refresh feed '${url}' because it hasn't been subscribed to!")
        }

        saveArticles url
    }

    def randomSampleFeed() {
        // Get a random sample feed that hasn't already been subscribed to.
        def allSampleFeedsSubscribed = redis.sinter(SAMPLE_FEEDS_KEY, SUBSCRIBED_FEEDS_KEY)?.size() == redis.scard(SAMPLE_FEEDS_KEY)
        def sampleFeed = [:]
        while (!allSampleFeedsSubscribed && !sampleFeed) {
            def feed = redis.srandmember(SAMPLE_FEEDS_KEY)
            if (!redis.sismember(SUBSCRIBED_FEEDS_KEY, feed)) {
                sampleFeed["url"] = feed
                sampleFeed["name"] = redis.get(feed)
            }
        }

        return sampleFeed
    }

    def subscribedFeeds() {
        return redis.smembers(SUBSCRIBED_FEEDS_KEY).collect { feedUrl -> new Expando(name: redis.get(feedUrl), url: feedUrl) }
    }

    private saveArticles(feedUrl) {
        def rss = new XmlSlurper().parseText(new URL(feedUrl).text)

        def articles = []
        for (item in rss.channel.item) {
            if (!Article.findByTitle(item.title.text())) {
                articles << new Article(
                    title: item.title.text(),
                    body: item.description.text(),
                    link: new URL(item.link.text()))
            }
        }

        Article.withTransaction { status ->
            for (article in articles) {
                article.save()
            }
        }

        return rss.channel.title.text()
    }
}
