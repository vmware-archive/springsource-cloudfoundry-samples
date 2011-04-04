<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>WGRUS Store</title>
</head>
<body>
	<h1>Orders</h1>
	
	<c:if test="${!empty orderId}">
	Thank You! Your order number is: ${orderId}
	</c:if>
	
	<hr/>	
	
	<form method="post">
		<table>
			<tr>
				<th>Email: </th>
				<td><input type="text" name="email" /></td>
			</tr>
			<tr>
				<th>Quantity: </th>
				<td>
					<select name="quantity">
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
				</td>
			</tr>
			<tr>
				<th>Product: </th>
				<td>
					<select name="productId">
  						<option>widget</option>
  						<option>gadget</option>
  					</select>
  				</td>
  			</tr>
			<tr height="30">
				<td colspan="2" valign="bottom" align="center">
					<input type="submit" value="Submit">
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
