<html>
	<head>
		<meta name="layout" content="news">
		<title>The Daily Groove</title>
		<g:setProvider library="jquery"/>
		<r:script disposition="head">
function subscribe(feedUrl) {
	jQuery.post('${createLink(controller: 'article', action: 'addFeed')}', {url: feedUrl}, function() {
		updateSampleFeed();
	}); 
}

function updateSampleFeed() {
	jQuery.ajax(
			{ url: '${createLink(controller: 'article', action: 'sampleFeed')}',
			  success: function(data, textStatus) {
			  	  var el = jQuery("#sampleFeed");
			  	  el.fadeOut(500, function() {
			  	  	  jQuery(this).html(data).fadeIn(500);
			  	  });
			  },
			  type: 'GET'
			});
}

setInterval(updateSampleFeed, 5000)
		</r:script>
	</head>
	
	<body id="all">
		<div class="container">
			<div id="errors" class="span-24 last">
				${flash.message}
			</div>
			<div class="span-12 addFeed">
				<g:form action="addFeed">
					Add Feed: <g:textField name="url" /> <g:submitButton name="Add"></g:submitButton>
				</g:form>
				<h4>Subscribed:</h4>
				<ul>
					<g:each in="${subscribedFeeds}" var="feed">
						<li>${feed}</li>
					</g:each>
				</ul>
			</div>
			<div class="span-4">&nbsp;</div>
			<div class="span-8 last">
				<label for="sampleFeed">Why not try:</label>
				<div id="sampleFeed"><tmpl:sampleFeed/></div>
			</div>
			<div class="span-24 last">
				<hr>
			</div>

			<div class="span-4">
				<h2>In the News</h2>
				<article:list value="${latestArticles}"></article:list>
			</div>
			<div class="span-15" id="contentPane">
				<article:repeat times="3">
					<article:random />
				</article:repeat>

			</div>
			<div class="span-5 last">
				<h2>Unread Items</h2>
				<article:list action="read" value="${unreadItems}"></article:list>				
			</div>
		</div>
	</body>
</html>
