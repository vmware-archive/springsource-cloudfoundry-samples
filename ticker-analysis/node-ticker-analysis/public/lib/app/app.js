function computeNodeSize(node, index, nodes, total) {
	total += width;
}

dojo.addOnLoad(function() {
	var socket = new io.Socket();
	socket.connect();
	socket.on("message", function(msg) {
		var data = dojo.fromJson(msg);
		var html = dojo.byId("ticker-symbols").innerHTML;
		dojo.byId("ticker-symbols").innerHTML = "<div class='ticker-data'>" +
				"<div class='ticker-symbol'><a href='#' title='Ticker Symbol Analysis'><a href='#'>" + data.symbol + "</a></div>" +
				"<div class='ticker-meta ticker-price'>Price: $" + data.price + "</div>" +
				"<div class='ticker-meta ticker-trend ticker-trend-up'>Change: +0.75</div>" +
			"</div>" + html;

		var nodes = dojo.query("#ticker-symbols .ticker-data")
		var total = 0;
		var d = dojo;
		var lastWidth = 0;

		nodes.forEach(function(node, index, nodes) {
			var style = d.getComputedStyle(node);
			var width = parseInt(style.width.replace(/px/, ""));
			total += width;
			lastWidth = width;
		});
		total += lastWidth;

		var newRight = (lastWidth * -1);
		dojo.style("ticker-symbols", {
			width: (total + (total/2)) + "px", 
			right: newRight + "px"
		});
		dojo.anim("ticker-symbols", {
			right: {
				start: newRight,
				end: 16,
				unit: "px"
			}
		});
	});
	});