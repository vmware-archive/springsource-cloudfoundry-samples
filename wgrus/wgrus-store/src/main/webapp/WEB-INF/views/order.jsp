<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>WGRUS Store</title>
<link rel="stylesheet" href="static/css/main.css" type="text/css"></link>
<link rel="stylesheet" href="static/css/colors.css" type="text/css"></link>
<link rel="stylesheet" href="static/css/local.css" type="text/css"></link>
</head>
<body>
	<div id="page">
		<div id="header">
			<div id="name-and-company">
				<div id='site-name'>
					<a href="" title="WGRUS Store" rel="home">WGRUS Store</a>
				</div>
				<div id='company-name'>
					<a href="http://www.springsource.org/spring-integration"
						title="Spring Integration">Spring Integration Home</a>
				</div>
			</div>
			<!-- /name-and-company -->
		</div>
		<!-- /header -->
		<div id="container">
			<div id="content" class="no-side-nav">

				<h1>Order</h1>
				<c:if test="${!empty orderId}">
					<span>Thank You! Your order number is: ${orderId}</span>
					<hr />
				</c:if>

				<form method="post">
					<ol>
						<li><label for="email">Email</label> <input id="email"
							type="text" name="email" /></li>
						<li><label for="quantity">Quantity</label> <select
							id="quantity" name="quantity">
								<option>1</option>
								<option>2</option>
								<option>3</option>
								<option>4</option>
								<option>5</option>
								<option>6</option>
								<option>7</option>
								<option>8</option>
								<option>9</option>
								<option>10</option>
						</select>
						</li>
						<li><label for="product">Product</label> <select id="product"
							name="productId">
								<option>widget</option>
								<option>gadget</option>
						</select>
						</li>
						<li class="buttonGroup"><input type="submit" value="Submit">
						</li>
					</ol>
				</form>
			</div>
		</div>
	</div>
</body>
</html>
