###
Part of codes borrow from mongoose
https://github.com/LearnBoost/mongoose/blob/master/lib/mongoose/connection.js

@param path e.g. 'db://localhost:27017/blog?auto_reconnect=true'
@param options the options of Db
@param callback function(error, db)
@return db
###
url = require 'url'
{Db, Server} = require 'mongodb'

exports.connect = (path, options, callback) ->
  uri = url.parse path, true
  host = uri.hostname
  port = uri.port || 27017
  database = uri.pathname.replace(/\//g, '')
  serverOptions = uri.query
  serverOptions['auto_reconnect'] = serverOptions['auto_reconnect'] is 'true'

  if typeof options is 'function'
    callback = options
    options = {}

  # handle authentication
  if uri and uri.auth
    auth = uri.auth.split(':')
    user = auth[0]
    pass = auth[1]
  else 
    user = pass = undefined
  
  if (!host) 
    if ('function' == typeof callback)
      callback(new Error('Please provide a valid hostname.'))
    return 

  if (!database) 
    if ('function' == typeof callback)
      callback(new Error('Please provide a database to connect to.'))
    return

  db = new Db(database, new Server(host, port, serverOptions), options)
  db.open (err, db)->
    return callback err if err and callback
    if user and pass
      return db.authenticate user, pass, (err, replies) ->
        return callback err if err and callback
        callback null, db if callback
    callback err, db if callback
  return db

