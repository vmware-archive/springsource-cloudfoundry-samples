mongodb = require 'mongodb'
Collection = mongodb.Collection

class MongoProvider
  constructor: (@db, @collectionName) ->
    #TODO: setter of @hint

  collection: (fn) ->
    if @_collection
      fn undefined, @_collection
    else
      @db.collection @collectionName, (err, collection) =>
        if err
          fn err
        else
          @_collection = collection
          fn undefined, collection

  findItems: (args..., fn) ->
    @find args..., (err, cursor) ->
      if err
        fn err
      else
        cursor.toArray fn

  findEach: (args..., fn) ->
    @find args..., (err, cursor) ->
      if err
        fn err
      else
        cursor.each fn

  findById: (args...) ->
    @findOne {_id: @db.bson_serializer.ObjectID.createFromHexString(args[0])}, args[1..]...

  sub: (sub) ->
    @__defineGetter__ sub, ()-> 
      @["sub-#{sub}"] or= new Provider "#{@collectionName}.#{sub}"
###
  bind these methods from Collection.prototype to Provider

  methods:
    insert
    checkCollectionName
    remove
    rename
    insertAll
    save
    update
    distinct
    count
    drop
    findAndModify
    find
    normalizeHintField
    findOne
    createIndex
    ensureIndex
    indexInformation
    dropIndex
    dropIndexes
    mapReduce
    group
    options
###
bind = (name, method) ->
  MongoProvider.prototype[name] = (args...) ->
    @collection (err, collection) ->
      if err
        args[args.length-1] err
      else
        method.apply collection, args

bind name, method for name, method of Collection.prototype

exports.MongoProvider = MongoProvider
