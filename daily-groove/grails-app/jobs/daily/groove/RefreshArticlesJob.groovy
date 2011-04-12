package daily.groove

class RefreshArticlesJob {
    def timeout = 300000l // execute job every 5 minutes
    def articleService

    def execute() {
        for (feed in articleService.subscribedFeeds()) {
            articleService.refreshFeed feed.url
        }
    }
}
