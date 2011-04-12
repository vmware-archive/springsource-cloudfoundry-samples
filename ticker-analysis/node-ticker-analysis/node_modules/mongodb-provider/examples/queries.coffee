GLOBAL.DEBUG = true

sys = require("sys")
#test = require("mjsunit")

provider = require('../lib')

host = process.env['MONGO_NODE_DRIVER_HOST'] or 'localhost'
port = process.env['MONGO_NODE_DRIVER_PORT'] or 27017

sys.puts("Connecting to " + host + ":" + port)
db = provider.connect "mongodb://#{host}:#{port}/node-mongo-examples"
tests = new provider.MongoProvider db, "test"
db.open (err, db) ->
  if err
    sys.puts(err.stack)
  db.dropDatabase () ->
    console.log 'dropDatabase ...'
    # Remove all records in collection if any
    tests.remove (err, collection) ->
      # Insert three records
      console.log 'do insert ...'
      tests.insert [{'a':1}, {'a':2}, {'b':3}], (err, docs) ->

        console.log 'done insert ...'
        console.log sys.inspect docs
        console.log sys.inspect docs[0]._id.toHexString()

        tests.findById docs[0]._id.toHexString(), (err, doc) ->
          console.log 'result of findById is '
          console.dir doc

        tests.ensureIndex [['a',1]], (err, rep) ->
        # Count the number of records
        tests.count (err, count) ->
          sys.puts("There are " + count + " records.")

        # Find all records. find() returns a cursor
        tests.find (err, cursor) ->
          # Print each row, each document has an _id field added on insert
          # to override the basic behaviour implement a primary key factory
          # that provides a 12 byte value
          sys.puts("Printing docs from Cursor Each")
          cursor.each (err, doc) ->
            if(doc != null) then sys.puts("Doc from Each " + sys.inspect(doc))

        sys.puts('Printing docs from Cursor Each')
        tests.findEach (err, doc) ->
          if(doc != null) then sys.puts("Doc from findEach " + sys.inspect(doc))

        # Cursor has an to array method that reads in all the records to memory
        tests.find (err, cursor) ->
          cursor.toArray (err, docs) ->
            sys.puts("Printing docs from Array")
            docs.forEach (doc) ->
              sys.puts("Doc from Array " + sys.inspect(doc))

        # Different methods to access records (no printing of the results)

        # Locate specific document by key
        tests.find {'a':1}, (err, cursor) ->
          cursor.nextObject (err, doc) ->
            sys.puts("Returned #1 documents")

        # Find records sort by 'a', skip 1, limit 2 records
        # Sort can be a single name, array, associate array or ordered hash
        tests.find {}, {'skip':1, 'limit':1, 'sort':'a'}, (err, cursor) ->
          cursor.toArray (err, docs) ->
            sys.puts("Returned #" + docs.length + " documents")

        # Find all records with 'a' > 1, you can also use $lt, $gte or $lte
        tests.find {'a':{'$gt':1}}, (err, cursor) ->
          cursor.toArray (err, docs) ->
            sys.puts("Returned #" + docs.length + " documents")

        tests.find {'a':{'$gt':1, '$lte':3}}, (err, cursor) ->
          cursor.toArray (err, docs) ->
            sys.puts("Returned #" + docs.length + " documents")

        # Find all records with 'a' in a set of values
        tests.find {'a':{'$in':[1,2]}}, (err, cursor) ->
          cursor.toArray (err, docs) ->
            sys.puts("Returned #" + docs.length + " documents")

        # Find by regexp
        tests.find {'a':/[1|2]/}, (err, cursor) ->
          cursor.toArray (err, docs) ->
            sys.puts("Returned #" + docs.length + " documents")

        # Print Query explanation
        tests.find {'a':/[1|2]/}, (err, cursor) ->
          cursor.explain (err, doc) ->
            sys.puts("-------------------------- Explanation")
            sys.puts(sys.inspect(doc))

        # Find and return Array

        # Find records sort by 'a', skip 1, limit 2 records
        # Sort can be a single name, array, associate array or ordered hash
        tests.findItems {}, {'skip':1, 'limit':1, 'sort':'a'}, (err, docs) ->
          sys.puts("findItems returned #" + docs.length + " documents")

        # Find all records with 'a' > 1, you can also use $lt, $gte or $lte
        tests.findItems {'a':{'$gt':1}}, (err, docs) ->
          sys.puts("findItems returned #" + docs.length + " documents")

        tests.findItems {'a':{'$gt':1, '$lte':3}}, (err, docs) ->
          sys.puts("findItems returned #" + docs.length + " documents")

        # Find all records with 'a' in a set of values
        tests.findItems {'a':{'$in':[1,2]}}, (err, docs) ->
          sys.puts("findItems returned #" + docs.length + " documents")

        # Find by regexp
        tests.findItems {'a':/[1|2]/}, (err, docs) ->
          sys.puts("findItems returned #" + docs.length + " documents")


        # Use a hint with a query, hint's can also be store in the collection
        # and will be applied to each query done through the collection.
        # Hint's can also be specified by query which will override the
        # hint's associated with the collection
        tests.createIndex 'a', (err, indexName) ->
          #TODO: not implement yet
          tests.hint = 'a'

          # You will see a different explanation now that the hint was set
          tests.find {'a':/[1|2]/}, (err, cursor) ->
            cursor.explain (err, doc) ->
              sys.puts("-------------------------- Explanation")
              sys.puts(sys.inspect(doc))

          tests.find {'a':/[1|2]/}, {'hint':'a'}, (err, cursor) ->
            cursor.explain (err, doc) ->
              sys.puts("-------------------------- Explanation")
              sys.puts(sys.inspect(doc))
              db.close()

