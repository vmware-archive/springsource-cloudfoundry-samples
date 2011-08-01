<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Hello Spring</title>
	<link rel="stylesheet" href="static/css/main.css" type="text/css"></link>
	<link rel="stylesheet" href="static/css/colors.css" type="text/css"></link>
	<link rel="stylesheet" href="static/css/local.css" type="text/css"></link>
</head>
<body>
	<div id="page">
		<div id="header">
			<div id="name-and-company">
				<div id='site-name'>
					<a href="" title="Hello Spring ${environmentName}" rel="home">Hello Spring ${environmentName}</a>
				</div>
				<div id='company-name'>
					<a href="http://www.springsource.com" title="SpringSource">SpringSource Home</a>
				</div>
			</div>
			<!-- /name-and-company -->
		</div>
		<!-- /header -->
		<div id="container">
			<div id="content" class="no-side-nav">

<p>Demonstration of using the 'cloud' namespace to create Spring beans backed by services bound to an application.</p>

<h2>The following services are bound to this application:</h2>
<ul>
	<c:forEach items="${services}" var="service">
		<li><p>${service}</p></li>	
	</c:forEach>
</ul>

<h2>Overview</h2>

<h3>Spring Profiles</h3>

<p>This application makes use of Spring 3.1 Profiles. There is an ApplicationContextInitializer
registered in <code>web.xml</code>:</p>

<pre>
	&lt;context-param&gt;
		&lt;param-name&gt;contextInitializerClasses&lt;/param-name&gt;
		&lt;param-value&gt;org.cloudfoundry.services.CloudApplicationContextInitializer&lt;/param-value&gt;
	&lt;/context-param&gt;
</pre>

<p>That initializer activates the "cloud" profile if it recognizes that it's running in a cloud environment.
Otherwise, it activates the "default" profile.</p>

<p>The configuration contains two different groups of beans that are labeled with the corresponding profile:</p>

<pre>
	&lt;beans profile="default"&gt;
		&lt;!-- default bean definitions go here --&gt;
	&lt;/beans&gt;

	&lt;beans profile="cloud"&gt;
		&lt;!-- cloud bean definitions go here --&gt;
	&lt;/beans&gt;
</pre>

<h3>The "cloud" Namespace</h3>

<p>The beans within the 'cloud' profile section make use of the 'cloud' namespace. That namespace provides
support for defining beans backed by services:</p>

<pre>
	&lt;cloud:data-source/&gt;

	&lt;cloud:redis-connection-factory/&gt;

	&lt;cloud:mongo-db-factory/&gt;
</pre>

<p>This style works as long as you have only one service of each type bound to the application.</p>

<p>If you want to bind to specific services (perhaps because you have more than one of a kind of service,
for example two MySql services), you need to specify the <code>service-name</code> attribute as follows:</p>

<pre>
	&lt;cloud:data-source service-name="inventory-db"/&gt;

	&lt;cloud:data-source service-name="pricing-db"/&gt;
</pre>

<p>In your Java code, simply add @Autowired dependencies for each bounded service:</p>

<pre>
	@Autowired DataSource dataSource;
	@Autowired RedisConnectionFactory redisConnectionFactory;
	@Autowired MongoDbFactory mongoDbFactory;
</pre>
				
<p>You have access to services without breaking a sweat.
<br/>
The service name will be used as the bean id by default, but if you would prefer to use a separate value for that,
you can specify the 'id' attribute. Regardless of how the id is determined, you can specify the <code>@Qualifier</code> 
annotation to pick the right bean or even use the XML based configuration. Please check the Spring Framework
documentation for more details.</p>

			</div>
		</div>
	</div>
</body>
</html>
