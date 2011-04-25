var sys      = require("sys");
var util     = require("util");
// Use locally-installed modules
require.paths.unshift('./node_modules');
// Cloud Foundry
var cf       = require("cloudfoundry");
// MongoDB
var mongoose = require("mongoose"),
    Schema   = mongoose.Schema,
    ObjectId = Schema.ObjectId,
    DocumentObjectId = mongoose.Types.ObjectId;
// Redis
var redis    = require("redis");
// Express
var express  = require("express");
var app      = express.createServer();
// Configure the app
app.configure(function() {
	app.use(express.methodOverride());
	app.use(express.bodyParser());
	app.use(app.router);	
	app.use(express.static(__dirname + '/public'));
	
	app.set('view engine', 'jade');
	app.set('running in cloud', cf.isRunningInCloud());

	if(!cf.isRunningInCloud()) {
		// Only use this in public for samples or development
		app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
	}
});
// Socket.IO (sort of :)
var io       = require("socket.io").listen(app, {
	transports: ['xhr-polling'], 
	transportOptions: {
		'xhr-polling': {duration: 10000}
	} 
});

// Mongoose Models
var TickerEvent = new Schema({
	symbol: { type: String },
	 price: { type: Number },
	volume: { type: Number }
});
mongoose.model('TickerEvent', TickerEvent);
var TickerSummary = new Schema({
	      _id: { type: String },
	timestamp: { type: Number },
	      max: { type: Number },
	      min: { type: Number },
	  average: { type: Number },
	   volume: { type: Number }
});
mongoose.model('TickerSummary', TickerSummary);

var mongoConfig = cf.getServiceConfig("ticker-analysis");
var db = mongoose.createConnection("mongo://" + mongoConfig.username + ":" + mongoConfig.password + "@" + mongoConfig.hostname + ":" + mongoConfig.port + "/" + mongoConfig.db);

// Connect to Redis
var redisConfig = cf.getServiceConfig("ticker-stream");
// util.debug("redis config: "+JSON.stringify(redisConfig));
var redisClient = redis.createClient(redisConfig.port, redisConfig.hostname);
var redisPublisher = redis.createClient(redisConfig.port, redisConfig.hostname);
if(redisConfig.password) {
	redisClient.auth(redisConfig.password);
	redisPublisher.auth(redisConfig.password);
}
var watchers = {};
redisClient.subscribe("ticker-stream");
redisClient.on("message", function(channel, json) {
	var data = JSON.parse(json);
	var TickerEvent = db.model('TickerEvent', 'tickerdata');
	var te = new TickerEvent({
		symbol: data.symbol,
		price: data.price,
		volume: data.volume
	});
	te.save(function(err) {
		if(err) {
			throw(err);
		}
	});
	io.broadcast(json);
});

// Make up ticker information at random
var SYMBOLS = [
	"DSA",
	"YZI",
	"KRQ",
	"HWW",
	"TXE",
	"DMT",
	"PIK",
	"HVF",
	"UWB",
	"MDL",
	"EAA",
	"SWL",
	"OX",
	"DYP",
	"WVL",
	"DPN",
	"WRB",
	"MI",
	"FCI",
	"BIW",
	"PWK",
	"IAO",
	"MYD",
	"EBF",
	"JTX",
	"XWE",
	"PRU",
	"HQG",
	"UVZ",
	"MXT",
	"YPB",
	"OJY",
	"FVC",
	"URI",
	"GVD",
	"DMQ",
	"UCJ",
	"QWW"
];
function getRandomSymbol() {
	var index = Math.round(Math.random() * SYMBOLS.length);
	if(index) {
		return SYMBOLS[index];
	} else {
		return getRandomSymbol();
	}
}

function getRandomPrice(lastPrice) {
	if(lastPrice) {
		return (Math.random() * (lastPrice * 2)).toFixed(2);
	} else {
		return (Math.random() * 100).toFixed(2);
	}
}

function getRandomVolume() {
	var vol = Math.round(Math.random() * 10);
	if(vol == 0) {
		return 1;
	}
	return vol;
}

var tickerSender;
function sendTickerEvent() {
	var symbolInfo = {
		symbol: getRandomSymbol(), 
		price: getRandomPrice(),
		volume: getRandomVolume()
	};
	// util.debug("sending ticker event: " + JSON.stringify(symbolInfo));
	redisPublisher.publish("ticker-stream", JSON.stringify(symbolInfo));
	
	var timeout = Math.round(Math.random() * 7000);
	if(timeout < 3000) {
		timeout += 3000;
	}
	tickerSender = setTimeout(sendTickerEvent, timeout);
}

// Routes
app.get("/", function(req, resp) {
	resp.render("home", {
		pageTitle: "Ticker Analysis Sample"
	});
});

app.get("/summary/:symbol", function(req, resp) {
	var TickerSummary = db.model("TickerSummary", "tickersummary");
	TickerSummary.findById(req.params.symbol, function(err, data) {
		if(err) {
			throw(err);
		}
		// util.debug("params: "+JSON.stringify(data));
		resp.send(JSON.stringify(data));
	});
});

// Ticker Stream
io.on("connection", function(client) {
	if(!tickerSender) {
		sendTickerEvent();
	}
	// util.debug("connection made..." + client);
});

// Listen for requests
app.listen(cf.getAppPort());
