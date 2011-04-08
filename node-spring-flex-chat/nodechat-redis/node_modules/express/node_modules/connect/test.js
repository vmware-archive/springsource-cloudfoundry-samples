
/**
 * Module dependencies.
 */

var connect = require('./');

connect(
  connect.limit('50mb'),
  function(req, res){
    var received = 0;
    req.on('data', function(chunk){
      received += chunk.length;
      process.stdout.write('received ' + (received / 1024 / 1024).toFixed(0) + 'mb\r');
    });
    req.on('end', function(){
      res.end('upload complete');
    });
  }
).listen(3000);