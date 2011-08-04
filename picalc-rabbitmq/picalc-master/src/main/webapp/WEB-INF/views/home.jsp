<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<html>
<head>
	<title>RabbitMQ &pi; Calculator</title>
</head>
<body>
<h1>Calculate &pi; using RabbitMQ workers</h1>
<form:form modelAttribute="calcParams" action="${pageContext.request.contextPath}/calculate" method="post">
  <form:label for="nrOfMessages" path="nrOfMessages">Number of messages to publish per calculation request:</form:label>
  <form:input path="nrOfMessages" type="text"/>
  
  <input type="submit" value="Calculate"/>
</form:form>


<c:if test="${calculated}">
	<p>Value: ${piValue}</p>
	<p>Error: ${error}</p>
	<p>Calc Time: ${calcTime} ms.</p>
</c:if>

</body>
</html>
