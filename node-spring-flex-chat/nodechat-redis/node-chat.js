var http = require('http'),  
    io = require('socket.io'),
    express = require('express'),
    redis = require('redis'),
    redisListener = null,
    redisSender = null,

app = express.createServer();

app.configure(function(){
    app.use(express.methodOverride());
    app.use(express.bodyParser());
    app.use(app.router);
});

app.configure('development', function(){
    app.use(express.static(__dirname + '/public'));
    app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
});

app.configure('production', function(){
    var oneYear = 31557600000;
    app.use(express.static(__dirname + '/public', { maxAge: oneYear }));
    app.use(express.errorHandler());
});

app.get('/', function(req, res){
    res.sendfile(__dirname + '/public/chat.html');
});

app.listen(process.env.VMC_APP_PORT || 8088);

//Set up Redis, dynamically discovering and connecting to the bound CloudFoundry service
if (process.env.VMC_SERVICES) {
    console.log("Bound services detected.");
    var services = JSON.parse(process.env.VMC_SERVICES);
    services.forEach(function(service) {
        console.log("Service Info: "+JSON.stringify(service));
        if (service.vendor == "redis") {
            console.log("Connecting to Redis service "+service.name+":"+service.options.hostname+":"+service.options.port);
            redisListener = redis.createClient(service.options.port, service.options.hostname);
            redisListener.auth(service.options.password);
            redisSender = redis.createClient(service.options.port, service.options.hostname);
            redisSender.auth(service.options.password);
            return;
        }
    });
}

//Fall-back Redis connections for local development outside of CloudFoundry
if (!redisListener && !process.env.VMC_APP_PORT) {
    console.log("Connecting to local Redis service");
    redisListener = redis.createClient();
    redisSender = redis.createClient();
}

if (!redisListener || !redisSender) {
    console.error("Fatal condition - no connection to Redis established");
    process.exit(1);
}

redisListener.on("connect", function(){ console.log("Redis listener connection established."); });

redisListener.on("error", function(err) { 
    console.log("Error thrown by redis listener connection: "+err);
    process.exit(1);
});

redisSender.on("connect", function(){ 
    console.log("Redis sender connection established.");
});

redisSender.on("error", function(err) {
    console.log("Error thrown by redis sender connection: "+err);
    process.exit(1);
});
  
// socket.io 
var io = io.listen(app, {transports: ['xhr-polling'], transportOptions: {'xhr-polling': {duration: 10000}} });

console.log(io.options);
  
io.on('connection', function(client){
    redisSender.publish("chat", JSON.stringify({chatMessage:"I am connected.",userId:client.sessionId}));
  
    client.on('message', function(message){
        var msg = { chatMessage: message, userId: client.sessionId };
        redisSender.publish("chat", JSON.stringify(msg));
    });

    client.on('disconnect', function(){
        redisSender.publish("chat", JSON.stringify({chatMessage:"I am disconnected.",userId:client.sessionId}));
    });
});

redisListener.subscribe("chat");

redisListener.on("message", function(channel, message){
    var msg = JSON.parse(message);
    if (!msg.userId) {
        io.broadcast(message);
    } else {
        io.broadcast(message, msg.userId);
    }
});