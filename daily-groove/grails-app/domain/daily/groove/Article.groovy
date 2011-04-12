package daily.groove

class Article {
    static mapWith = "redis"

    String title
    String body
    URL link
    Date dateCreated
    boolean unread = true
    
    static mapping = {
        title index:true
        unread index:true
    }
    
    static constraints = {
        title blank:false
        body blank:false
    }
}
