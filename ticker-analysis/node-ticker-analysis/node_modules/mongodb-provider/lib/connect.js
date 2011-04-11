(function() {
  /*
  Part of codes borrow from mongoose
  https://github.com/LearnBoost/mongoose/blob/master/lib/mongoose/connection.js

  @param path e.g. 'db://localhost:27017/blog?auto_reconnect=true'
  @param options the options of Db
  @param callback function(error, db)
  @return db
  */  var Db, Server, url, _ref;
  url = require('url');
  _ref = require('mongodb'), Db = _ref.Db, Server = _ref.Server;
  exports.connect = function(path, options, callback) {
    var auth, database, db, host, pass, port, serverOptions, uri, user;
    uri = url.parse(path, true);
    host = uri.hostname;
    port = uri.port || 27017;
    database = uri.pathname.replace(/\//g, '');
    serverOptions = uri.query;
    serverOptions['auto_reconnect'] = serverOptions['auto_reconnect'] === 'true';
    if (typeof options === 'function') {
      callback = options;
      options = {};
    }
    if (uri && uri.auth) {
      auth = uri.auth.split(':');
      user = auth[0];
      pass = auth[1];
    } else {
      user = pass = void 0;
    }
    if (!host) {
      if ('function' === typeof callback) {
        callback(new Error('Please provide a valid hostname.'));
      }
      return;
    }
    if (!database) {
      if ('function' === typeof callback) {
        callback(new Error('Please provide a database to connect to.'));
      }
      return;
    }
    db = new Db(database, new Server(host, port, serverOptions), options);
    db.open(function(err, db) {
      if (err && callback) {
        return callback(err);
      }
      if (user && pass) {
        return db.authenticate(user, pass, function(err, replies) {
          if (err && callback) {
            return callback(err);
          }
          if (callback) {
            return callback(null, db);
          }
        });
      }
      if (callback) {
        return callback(err, db);
      }
    });
    return db;
  };
}).call(this);
