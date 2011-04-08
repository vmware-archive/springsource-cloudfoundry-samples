#!/usr/bin/env node

var http = require('http'), url = require('url'), sys = require('sys');

var port = process.env.VMC_APP_PORT || process.env.VCAP_APP_PORT || 8124;

http.createServer(function (req, res) {
    res.writeHead(200, {'Content-Type': 'text/plain'});
    product = url.parse(req.url).pathname.substring(1);
    sys.puts(new Date()+" ("+req.connection.remoteAddress+") request: " + product);
    var qty = 0;
    if ('widget' == product) {
	    qty = 100;
    }
    res.end('' + qty);
}).listen(port);
console.log('Server listening on port '+port);
