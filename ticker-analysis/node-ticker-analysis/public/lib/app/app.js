function handleSymbolClick(evt) {
	var symbol = evt.target.attributes["data"].value;
	dojo.xhrGet({
		url: "/summary/" + symbol,
		handleAs: "json",
		load: function(data) {
			if(data) {
				dojo.byId("t-d-symb-val").innerHTML = symbol;
				dojo.byId("t-d-s-p-high-val").innerHTML = data.max.toFixed(2);
				dojo.byId("t-d-s-p-avg-val").innerHTML = data.average.toFixed(2);
				dojo.byId("t-d-s-p-low-val").innerHTML = data.min.toFixed(2);
				dojo.byId("t-d-v-val").innerHTML = data.volume;

				var dpStyle = dojo.style("ticker-detail");
				if(dpStyle["opacity"] == "0") {
					dojo.fadeIn({ node: "ticker-detail" }).play();
				}
			}
		}
	});
}

dojo.addOnLoad(function() {
	var socket = new io.Socket();
	socket.connect();
	socket.on("message", function(msg) {
		var data = dojo.fromJson(msg);
		var html = dojo.byId("ticker-symbols").innerHTML;
		dojo.byId("ticker-symbols").innerHTML = "<div class='ticker-data'>" +
				"<div class='ticker-symbol'><a class='ticker-symb-link' href='#' data='" + data.symbol + "' title='Ticker Symbol Analysis'>" + data.symbol + "</a></div>" +
				"<div class='ticker-meta ticker-price'>Price: $" + data.price + "</div>" +
				"<div class='ticker-meta ticker-volume'>Volume: " + data.volume + "</div>" +
			"</div>" + html;

		dojo.query(".ticker-symb-link").onclick(handleSymbolClick);

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