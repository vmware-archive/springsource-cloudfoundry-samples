GLOBAL.DEBUG = true

sys = require('sys')

mongo = require('mongodb')
{MongoProvider} = require('../lib/provider')

host = process.env['MONGO_NODE_DRIVER_HOST'] or 'localhost'
port = process.env['MONGO_NODE_DRIVER_PORT'] or  mongo.Connection.DEFAULT_PORT

LINE_SIZE = 120

authors = {}
users = {}

class AuthorProvider extends MongoProvider

  constructor: (db) ->
    super(db, 'authors')

  doInsert: (callback) ->
    # Insert authors
    this.insert [{
      'name': 'William Shakespeare', 'email': 'william@shakespeare.com', 'age': 587
    }, {
      'name': 'Jorge Luis Borges', 'email': 'jorge@borges.com', 'age': 123
    }], (err, docs) ->
        if err
          sys.puts err.stack
        else
          docs.forEach (doc) ->
            sys.puts(sys.inspect(doc))
            authors[doc.name] = doc

class UserProvider extends MongoProvider

  constructor: (db) ->
    super db, "users"

class ArticleProvider extends MongoProvider

  constructor: (db) ->
    super db, "article"

sys.puts('Connecting to ' + host + ':' + port)

db = new mongo.Db('node-mongo-blog', new mongo.Server(host, port, {}), {})
authorProvider = new AuthorProvider db
userProvider = new UserProvider db
articleProvider = new ArticleProvider db

db.open (err, db) ->
  db.dropDatabase (err, result) ->
    sys.puts('===================================================================================')
    sys.puts('>> Adding Authors')
    sys.puts('===================================================================================')
    authorProvider.doInsert()
    authorProvider.createIndex ['meta', ['_id', 1], ['name', 1], ['age', 1]], (err, indexName) ->
      sys.puts('===================================================================================')
      sys.puts('===================================================================================')
      sys.puts('>> Authors ordered by age ascending')
      sys.puts('===================================================================================')
      authorProvider.find {}, {'sort': [['age', 1]]}, (err, cursor) ->
        cursor.each (err, author) ->
          if (author != null) 
            sys.puts('[' + author.name + ']:[' + author.email + ']:[' + author.age + ']')
          else
            sys.puts('===================================================================================')
            sys.puts('>> Adding users')
            sys.puts('===================================================================================')


            userProvider.insert [{
              'login': 'jdoe', 'name': 'John Doe', 'email': 'john@doe.com'
            }, {
              'login': 'lsmith', 'name': 'Lucy Smith', 'email': 'lucy@smith.com'
            }], (err, docs) ->
                if err
                  sys.puts(sys.inspect(err))
                else
                  docs.forEach (doc) ->
                    sys.puts(sys.inspect(doc))
                    users[doc.login] = doc

            sys.puts('===================================================================================')
            sys.puts('>> Users ordered by login ascending')
            sys.puts('===================================================================================')
            userProvider.find {}, {'sort': [['login', 1]]}, (err, cursor) ->
              cursor.each (err, user) ->
                if (user != null) 
                  sys.puts('[' + user.login + ']:[' + user.name + ']:[' + user.email + ']')
                else
                  sys.puts('===================================================================================')
                  sys.puts('>> Adding articles')
                  sys.puts('===================================================================================')
                  articleProvider.insert [ { 
                      'title': 'Caminando por Buenos Aires',
                      'body': 'Las callecitas de Buenos Aires tienen ese no se que...',
                      'author_id': authors['Jorge Luis Borges']._id
                    }, { 
                      'title': 'I must have seen thy face before',
                      'body': 'Thine eyes call me in a new way',
                      'author_id': authors['William Shakespeare']._id,
                      'comments': [{'user_id': users['jdoe']._id, 'body': 'great article!'}] 
                    } ], (err, docs) ->
                      docs.forEach (doc) ->
                        sys.puts(sys.inspect(doc))

                  sys.puts('===================================================================================')
                  sys.puts('>> Articles ordered by title ascending')
                  sys.puts('===================================================================================')
                  articleProvider.find {}, {'sort': [['title', 1]]}, (err, cursor) ->
                    cursor.each (err, article) ->
                      if (article != null) 
                        sys.puts('[' + article.title + ']:[' + article.body + ']:[' + article.author_id.toHexString() + ']')
                        sys.puts('>> Closing connection')
                        db.close()
