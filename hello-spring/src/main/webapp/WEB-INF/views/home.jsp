<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Hello cloud services!
</h1>

<p>
Demonstration of using the 'cloud' namespace to create Spring beans backed by services bound to an application.
</p>

<h2>Usage</h2>
<p>
To use this, add the following lines in your application context:
<pre>
	&lt;cloud:data-source/&gt;

	&lt;cloud:rabbit-connection-factory/&gt;

	&lt;cloud:redis-connection-factory/&gt;

	&lt;cloud:mongo/&gt;
</pre>
<p>
This style works as long as you have only one service of each type bound to the application.
</p>

<p>
If you want to bind to specific services (perhaps because you have more than one of a kind of service, 
for example two MySql services), you need to specify the <code>service-name</code> attribute as follows:
<pre>
	&lt;cloud:data-source service-name="inventory-db"/&gt;

	&lt;cloud:data-source service-name="pricing-db"/&gt;
</pre>
</p>

<p>
In your Java code, simply add @Autowired dependencies for each bounded service:
<pre>
	@Autowired DataSource dataSource;
	@Autowired ConnectionFactory rabbitConnectionFactory;
	@Autowired RedisConnectionFactory redisConnectionFactory;
	@Autowired Mongo mongo;
</pre>

You have access to services without breaking a sweat.

The service name will be used as the bean id by default, but if you would prefer to use a separate value for that, you can specify the 'id' attribute. Regardless of how the id is determined, you can specify the <code>@Qualifier</code> 
annotation to pick the right bean or even use the XML based configuration. Please check the Spring Framework documentation
for more details. 
</p>

<h2>Here are the beans bound to this application:</h2>

<c:forEach items="${services}" var="service">
	<li><p>${service}</p></li>	
</c:forEach>

</body>
</html>
