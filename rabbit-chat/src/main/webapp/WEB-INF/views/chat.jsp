<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>RabbitMQ Chat</title>
	<script type="text/javascript" src="static/js/jquery.min.js"></script>
	<script type="text/javascript">
	var running = false;
	var timer;
	function load() {
		if (running) {
			$.ajax({
				url : "chatlog",
				success : function(message) {
					if (message && message.length) {
						var messagesDiv = $('#messages');
						messagesDiv.html(message);
						messagesDiv.animate({ scrollTop: messagesDiv.attr("scrollHeight") - messagesDiv.height() }, 150);
					}
					timer = poll();
				},
				error : function() {
					timer = poll();
				},
				cache : false
			});
		}
	}
	function start() {
		if (!running) {
			running = true;
			if (timer != null) {
				clearTimeout(timer);
			}
			timer = poll();
		}
	}
	function poll() {
		if (timer != null) {
			clearTimeout(timer);
		}
		return setTimeout(load, 1000);
	}
	$(function() {
		$.ajaxSetup({cache:false});
		start();
		$('#chatForm').submit(
			function() {
				$.post(
					$('#chatForm').attr("action"),
					$('#chatForm').serialize(),
					function(response) {
						if (response) {
							confirm(response.id);
						}
					});
				$('#text').val("");
				return false;
			});
	});
</script>
</head>
<body>
	<h1>RabbitMQ Chat</h1>

	<form id="chatForm" method="post" action="publish">
		<table>
			<tr>
				<th>Username:</th>
				<td><input id="username" name="username" value="${username}"/></td>
			</tr>
			<tr>
				<th>Message: </th>
				<td><input id="text" name="text"/><input id="send" type="submit" value="Send" /></td>
			</tr>
		</table>
	</form>

	<hr/>

	<div id="messages" style="border : solid 2px #cccccc; background : #000000; color : #ffffff; padding : 4px; height : 300px; overflow : auto;"> </div>
	
</body>
</html> 