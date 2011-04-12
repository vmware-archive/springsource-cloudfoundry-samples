package daily.groove

import static daily.groove.Constants.*

class ArticleController {
    def articleService
    def redis

    def index = { 
        redirect action:'all'
    }
    
    def addFeed = {
        try {
            articleService.loadFeed(params.url)
        }
        catch(e) {
            flash.message = "Error processing feed. Please try again later."
            log.error("Error processing feed: ${e.message}", e)
        }
        redirect action: "all"        
    }
    
    def all = {
        def sort = [sort:"dateCreated", order:"desc", max:10]
        def unreadItems = Article.findAllByUnread(true, sort)
        
        [ latestArticles:Article.list(sort),
          unreadItems:unreadItems,
          sampleFeed:articleService.randomSampleFeed(),
          subscribedFeeds:articleService.subscribedFeeds()*.name]
    }
    
    def show = {
        def article = Article.get(params.id)
        
        render template:"articlePreview", model:[article:article]
    }
    
    def read = {
        def article = Article.get(params.id)
        article.unread = false
        article.save(flush:true)
        
        render template:"articlePreview", model:[article:article]
    }
    
    def sampleFeed = {
        render template:"sampleFeed", model:[sampleFeed:articleService.randomSampleFeed()]
    }
}
