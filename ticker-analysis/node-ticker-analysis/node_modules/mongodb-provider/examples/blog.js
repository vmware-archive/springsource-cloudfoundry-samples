(function() {
  var ArticleProvider, AuthorProvider, LINE_SIZE, MongoProvider, UserProvider, articleProvider, authorProvider, authors, db, host, mongo, port, sys, userProvider, users;
  var __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  GLOBAL.DEBUG = true;
  sys = require('sys');
  mongo = require('mongodb');
  MongoProvider = require('../lib/provider').MongoProvider;
  host = process.env['MONGO_NODE_DRIVER_HOST'] || 'localhost';
  port = process.env['MONGO_NODE_DRIVER_PORT'] || mongo.Connection.DEFAULT_PORT;
  LINE_SIZE = 120;
  authors = {};
  users = {};
  AuthorProvider = (function() {
    __extends(AuthorProvider, MongoProvider);
    function AuthorProvider(db) {
      AuthorProvider.__super__.constructor.call(this, db, 'authors');
    }
    AuthorProvider.prototype.doInsert = function(callback) {
      return this.insert([
        {
          'name': 'William Shakespeare',
          'email': 'william@shakespeare.com',
          'age': 587
        }, {
          'name': 'Jorge Luis Borges',
          'email': 'jorge@borges.com',
          'age': 123
        }
      ], function(err, docs) {
        if (err) {
          return sys.puts(err.stack);
        } else {
          return docs.forEach(function(doc) {
            sys.puts(sys.inspect(doc));
            return authors[doc.name] = doc;
          });
        }
      });
    };
    return AuthorProvider;
  })();
  UserProvider = (function() {
    __extends(UserProvider, MongoProvider);
    function UserProvider(db) {
      UserProvider.__super__.constructor.call(this, db, "users");
    }
    return UserProvider;
  })();
  ArticleProvider = (function() {
    __extends(ArticleProvider, MongoProvider);
    function ArticleProvider(db) {
      ArticleProvider.__super__.constructor.call(this, db, "article");
    }
    return ArticleProvider;
  })();
  sys.puts('Connecting to ' + host + ':' + port);
  db = new mongo.Db('node-mongo-blog', new mongo.Server(host, port, {}), {});
  authorProvider = new AuthorProvider(db);
  userProvider = new UserProvider(db);
  articleProvider = new ArticleProvider(db);
  db.open(function(err, db) {
    return db.dropDatabase(function(err, result) {
      sys.puts('===================================================================================');
      sys.puts('>> Adding Authors');
      sys.puts('===================================================================================');
      authorProvider.doInsert();
      return authorProvider.createIndex(['meta', ['_id', 1], ['name', 1], ['age', 1]], function(err, indexName) {
        sys.puts('===================================================================================');
        sys.puts('===================================================================================');
        sys.puts('>> Authors ordered by age ascending');
        sys.puts('===================================================================================');
        return authorProvider.find({}, {
          'sort': [['age', 1]]
        }, function(err, cursor) {
          return cursor.each(function(err, author) {
            if (author !== null) {
              return sys.puts('[' + author.name + ']:[' + author.email + ']:[' + author.age + ']');
            } else {
              sys.puts('===================================================================================');
              sys.puts('>> Adding users');
              sys.puts('===================================================================================');
              userProvider.insert([
                {
                  'login': 'jdoe',
                  'name': 'John Doe',
                  'email': 'john@doe.com'
                }, {
                  'login': 'lsmith',
                  'name': 'Lucy Smith',
                  'email': 'lucy@smith.com'
                }
              ], function(err, docs) {
                if (err) {
                  return sys.puts(sys.inspect(err));
                } else {
                  return docs.forEach(function(doc) {
                    sys.puts(sys.inspect(doc));
                    return users[doc.login] = doc;
                  });
                }
              });
              sys.puts('===================================================================================');
              sys.puts('>> Users ordered by login ascending');
              sys.puts('===================================================================================');
              return userProvider.find({}, {
                'sort': [['login', 1]]
              }, function(err, cursor) {
                return cursor.each(function(err, user) {
                  if (user !== null) {
                    return sys.puts('[' + user.login + ']:[' + user.name + ']:[' + user.email + ']');
                  } else {
                    sys.puts('===================================================================================');
                    sys.puts('>> Adding articles');
                    sys.puts('===================================================================================');
                    articleProvider.insert([
                      {
                        'title': 'Caminando por Buenos Aires',
                        'body': 'Las callecitas de Buenos Aires tienen ese no se que...',
                        'author_id': authors['Jorge Luis Borges']._id
                      }, {
                        'title': 'I must have seen thy face before',
                        'body': 'Thine eyes call me in a new way',
                        'author_id': authors['William Shakespeare']._id,
                        'comments': [
                          {
                            'user_id': users['jdoe']._id,
                            'body': 'great article!'
                          }
                        ]
                      }
                    ], function(err, docs) {
                      return docs.forEach(function(doc) {
                        return sys.puts(sys.inspect(doc));
                      });
                    });
                    sys.puts('===================================================================================');
                    sys.puts('>> Articles ordered by title ascending');
                    sys.puts('===================================================================================');
                    return articleProvider.find({}, {
                      'sort': [['title', 1]]
                    }, function(err, cursor) {
                      return cursor.each(function(err, article) {
                        if (article !== null) {
                          sys.puts('[' + article.title + ']:[' + article.body + ']:[' + article.author_id.toHexString() + ']');
                          sys.puts('>> Closing connection');
                          return db.close();
                        }
                      });
                    });
                  }
                });
              });
            }
          });
        });
      });
    });
  });
}).call(this);
