package daily.groove

class ArticleTagLib {

	static namespace = 'article'
	
	def redis
	
	def list = { attrs, body ->
		out << g.render(template:'articleList', model:[action: attrs.action ?: 'show',articles:attrs.value])
	}
	
	def random = { attrs, body ->
		def article = Article.random()
		
		if(article) {
			out << g.render( template:"articlePreview", model:[article:article] )
		}
	}
		
	def repeat = { attrs, body ->
		attrs.times.toInteger().times {
			out << body()
		}
	}
}
