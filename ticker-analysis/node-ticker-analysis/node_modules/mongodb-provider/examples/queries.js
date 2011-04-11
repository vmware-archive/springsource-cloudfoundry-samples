(function() {
  var db, host, port, provider, sys, tests;
  GLOBAL.DEBUG = true;
  sys = require("sys");
  provider = require('../lib');
  host = process.env['MONGO_NODE_DRIVER_HOST'] || 'localhost';
  port = process.env['MONGO_NODE_DRIVER_PORT'] || 27017;
  sys.puts("Connecting to " + host + ":" + port);
  db = provider.connect("mongodb://" + host + ":" + port + "/node-mongo-examples");
  tests = new provider.MongoProvider(db, "test");
  db.open(function(err, db) {
    if (err) {
      sys.puts(err.stack);
    }
    return db.dropDatabase(function() {
      console.log('dropDatabase ...');
      return tests.remove(function(err, collection) {
        console.log('do insert ...');
        return tests.insert([
          {
            'a': 1
          }, {
            'a': 2
          }, {
            'b': 3
          }
        ], function(err, docs) {
          console.log('done insert ...');
          console.log(sys.inspect(docs));
          console.log(sys.inspect(docs[0]._id.toHexString()));
          tests.findById(docs[0]._id.toHexString(), function(err, doc) {
            console.log('result of findById is ');
            return console.dir(doc);
          });
          tests.ensureIndex([['a', 1]], function(err, rep) {});
          tests.count(function(err, count) {
            return sys.puts("There are " + count + " records.");
          });
          tests.find(function(err, cursor) {
            sys.puts("Printing docs from Cursor Each");
            return cursor.each(function(err, doc) {
              if (doc !== null) {
                return sys.puts("Doc from Each " + sys.inspect(doc));
              }
            });
          });
          sys.puts('Printing docs from Cursor Each');
          tests.findEach(function(err, doc) {
            if (doc !== null) {
              return sys.puts("Doc from findEach " + sys.inspect(doc));
            }
          });
          tests.find(function(err, cursor) {
            return cursor.toArray(function(err, docs) {
              sys.puts("Printing docs from Array");
              return docs.forEach(function(doc) {
                return sys.puts("Doc from Array " + sys.inspect(doc));
              });
            });
          });
          tests.find({
            'a': 1
          }, function(err, cursor) {
            return cursor.nextObject(function(err, doc) {
              return sys.puts("Returned #1 documents");
            });
          });
          tests.find({}, {
            'skip': 1,
            'limit': 1,
            'sort': 'a'
          }, function(err, cursor) {
            return cursor.toArray(function(err, docs) {
              return sys.puts("Returned #" + docs.length + " documents");
            });
          });
          tests.find({
            'a': {
              '$gt': 1
            }
          }, function(err, cursor) {
            return cursor.toArray(function(err, docs) {
              return sys.puts("Returned #" + docs.length + " documents");
            });
          });
          tests.find({
            'a': {
              '$gt': 1,
              '$lte': 3
            }
          }, function(err, cursor) {
            return cursor.toArray(function(err, docs) {
              return sys.puts("Returned #" + docs.length + " documents");
            });
          });
          tests.find({
            'a': {
              '$in': [1, 2]
            }
          }, function(err, cursor) {
            return cursor.toArray(function(err, docs) {
              return sys.puts("Returned #" + docs.length + " documents");
            });
          });
          tests.find({
            'a': /[1|2]/
          }, function(err, cursor) {
            return cursor.toArray(function(err, docs) {
              return sys.puts("Returned #" + docs.length + " documents");
            });
          });
          tests.find({
            'a': /[1|2]/
          }, function(err, cursor) {
            return cursor.explain(function(err, doc) {
              sys.puts("-------------------------- Explanation");
              return sys.puts(sys.inspect(doc));
            });
          });
          tests.findItems({}, {
            'skip': 1,
            'limit': 1,
            'sort': 'a'
          }, function(err, docs) {
            return sys.puts("findItems returned #" + docs.length + " documents");
          });
          tests.findItems({
            'a': {
              '$gt': 1
            }
          }, function(err, docs) {
            return sys.puts("findItems returned #" + docs.length + " documents");
          });
          tests.findItems({
            'a': {
              '$gt': 1,
              '$lte': 3
            }
          }, function(err, docs) {
            return sys.puts("findItems returned #" + docs.length + " documents");
          });
          tests.findItems({
            'a': {
              '$in': [1, 2]
            }
          }, function(err, docs) {
            return sys.puts("findItems returned #" + docs.length + " documents");
          });
          tests.findItems({
            'a': /[1|2]/
          }, function(err, docs) {
            return sys.puts("findItems returned #" + docs.length + " documents");
          });
          return tests.createIndex('a', function(err, indexName) {
            tests.hint = 'a';
            tests.find({
              'a': /[1|2]/
            }, function(err, cursor) {
              return cursor.explain(function(err, doc) {
                sys.puts("-------------------------- Explanation");
                return sys.puts(sys.inspect(doc));
              });
            });
            return tests.find({
              'a': /[1|2]/
            }, {
              'hint': 'a'
            }, function(err, cursor) {
              return cursor.explain(function(err, doc) {
                sys.puts("-------------------------- Explanation");
                sys.puts(sys.inspect(doc));
                return db.close();
              });
            });
          });
        });
      });
    });
  });
}).call(this);
