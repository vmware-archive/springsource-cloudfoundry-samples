/**
 * Important note: this application is not suitable for benchmarks!
 */

var http = require('http')
  , url = require('url')
  , fs = require('fs')
  , io = require('../')
  , sys = require(process.binding('natives').util ? 'util' : 'sys')
  , server;
    
server = http.createServer(function(req, res){
  // your normal server code
  var path = url.parse(req.url).pathname;
  switch (path){
    case '/':
      res.writeHead(200, {'Content-Type': 'text/html'});
      res.write([
          '<h1>Welcome.</h1>'
        , '<h2>Transports:</h2>'
        , '<ul>'
        ,   '<li><a href="/benchmark.html?xhr-polling">Long polling</a></li>'
        ,   '<li><a href="/benchmark.html?xhr-multipart">Multipart XHR</a></li>'
        ,   '<li><a href="/benchmark.html?websocket">WebSocket</a></li>'
        ,   '<li><a href="/benchmark.html?flashsocket">FlashSocket</a></li>'
        ,   '<li><a href="/benchmark.html?htmlfile">HTMLFile</a></li>'
        ,   '<li><a href="/benchmark.html?jsonp-polling">JSONP Polling</a></li>'
        , '</ul>'
      ].join(''));
      res.end();
      break;
      
    case '/json.js':
    case '/benchmark.html':
      fs.readFile(__dirname + path, function(err, data){
        if (err) return send404(res);
        res.writeHead(200, {'Content-Type': path == 'json.js' ? 'text/javascript' : 'text/html'})
        res.write(data.toString().replace('{transport}'
                    , req.url.replace('/benchmark.html', ''))
                , 'utf8');
        res.end();
      });
      break;
      
    default: send404(res);
  }
}),

send404 = function(res){
  res.writeHead(404);
  res.write('404');
  res.end();
};

server.listen(8080);

// socket.io, I choose you
// simplest chat application evar
var io = io.listen(server)
  , buffer = [];
  
io.on('connection', function(client){
  // track latency and number of messages
  client.latency = 0;
  client.messages = 0;

  client.send(Date.now());
  
  client.on('message', function(message){
    client.latency += 
    console.log('Message latency:', Date.now() - Number(message));
  });

  client.on('disconnect', function(){
    console.log();
  });
});
