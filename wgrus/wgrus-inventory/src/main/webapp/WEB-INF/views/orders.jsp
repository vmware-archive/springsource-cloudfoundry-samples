<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>WGRUS Orders</title>
<link rel="stylesheet" href="static/css/main.css" type="text/css"></link>
<link rel="stylesheet" href="static/css/colors.css" type="text/css"></link>
<link rel="stylesheet" href="static/css/local.css" type="text/css"></link>
</head>
<body>
	<div id="page">
		<!--
		<div id="header">
			<div id="name-and-company">
				<div id='site-name'>
					<a href="" title="WGRUS Store" rel="home">WGRUS Orders</a>
				</div>
				<div id='company-name'>
					<a href="http://www.springsource.org/spring-integration"
						title="Spring Integration">Spring Integration Home</a>
				</div>
			</div>
		</div>
		-->
		<div id="container">
			<div id="content" class="no-side-nav">
				<h1>Orders</h1>
				<b>Total Received: ${count}</b>
				<br/>
				Last 25:
				<ol>
					<c:forEach var="order" items="${orders}">
						<li><c:out value="${order}"/></li>
					</c:forEach>
				</ol>
			</div>
		</div>
	</div>
</body>
</html>
