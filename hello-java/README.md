Hello Java Sample
=================

This sample aims to demonstrate the simplest possible Servlet-based Java webapp. Here we walk through the entire content of the application.

The Servlet
-----------

In the *org.cloudfoundry.samples* package under *src/main/java*, you will see *HelloServlet.java*:

	package org.cloudfoundry.samples;
	
	import java.io.IOException;
	import java.io.PrintWriter;
	
	import javax.servlet.ServletException;
	import javax.servlet.http.HttpServlet;
	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;
	
	public class HelloServlet extends HttpServlet {
	
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("text/plain");
			response.setStatus(200);
			PrintWriter writer = response.getWriter();
			writer.println("Hello from " + System.getenv("VCAP_APP_HOST") + ":" + System.getenv("VCAP_APP_PORT"));
			writer.close();
		}
	}

Notice that it contains only *java* and *javax* imports. In the *doGet(..)* method, it simply writes the
*Content-Type* header value, sets the status to *200* (OK), and then writes a message. The content of that
message includes the HOST and PORT where this application is running. Those environment variables, 
*VCAP_APP_HOST* and *VCAP_APP_PORT* are available anytime the application is running on Cloud Foundry.

The *web.xml* File
------------------

As a standard Java webapp, the configuration of *HelloServlet* is included within the *WEB-INF/web.xml* file.
You will find that under *src/main/webapp*. It looks like this:

	<?xml version="1.0" encoding="UTF-8"?>
	<web-app id="WebApp_ID" version="2.4" xmlns="http://java.sun.com/xml/ns/j2ee"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">
	
		<display-name>Hello Java</display-name>
		
		<servlet>
			<servlet-name>HelloServlet</servlet-name>
			<servlet-class>org.cloudfoundry.samples.HelloServlet</servlet-class>
		</servlet>
		
		<servlet-mapping>
			<servlet-name>HelloServlet</servlet-name>
			<url-pattern>/</url-pattern>
		</servlet-mapping>
		
	</web-app>

There you see the servlet is declared, and then a mapping is provided. In this case, the context root of the
application is mapped directly to HelloServlet since it is the only servlet in the application.

The *pom.xml* File
------------------

Since this application is built with Maven, there is a *pom.xml* file directly in the root directory.
That POM is as simple as it can be.

	<project>
		<modelVersion>4.0.0</modelVersion>
		<groupId>org.cloudfoundry.samples</groupId>
		<artifactId>hello-java</artifactId>
		<version>1.0</version>
		<packaging>war</packaging>
		<dependencies>
			<dependency>
				<groupId>javax.servlet</groupId>
				<artifactId>servlet-api</artifactId>
				<version>2.5</version>
				<scope>provided</scope>
			</dependency>
		</dependencies>
	</project>

Notice that it has the standard project metadata (groupId, artifactId, version, etc.), and it has a single
dependency: the Servlet API. That dependency is marked as *provided* since it's available in the deployment
environment itself.

Building the Application
------------------------

To build the application, make sure you have [Maven](http://maven.apache.org/ "Maven") installed.
Then, *cd* into the root directory and execute:

	mvn clean package

That will create the *hello-java-1.0.war* file within the 'target' directory.

Running the Application
-----------------------

To run the application, make sure you have [VMC](http://support.cloudfoundry.com/entries/20012337-getting-started-guide-command-line-vmc-users "VMC")
installed and that you are logged in successfully for your desired target environment (e.g. http://api.cloudfoundry.com).
Then, *cd* into the *target* directory and execute:

	vmc push

That will ask a few questions, and in most cases you can simply accept the defaults. Here is an example of the output:

	Would you like to deploy from the current directory? [Yn]: Y
	Application Name: hello-java-sample
	Application Deployed URL: 'hello-java-sample.cloudfoundry.com'? 
	Detected a Java Web Application, is this correct? [Yn]: Y
	Memory Reservation [Default:512M] (64M, 128M, 256M or 512M) 256
	Creating Application: OK
	Would you like to bind any services to 'hello-java-sample'? [yN]: N
	Uploading Application:
  	  Checking for available resources: OK
  	  Packing application: OK
  	  Uploading (4K): OK   
	Push Status: OK
	Staging Application: OK                                                         
	Starting Application: OK

Notice that it detected the app type as "Java Web Application". In this case, it's only recognizing a runtime (Java)
but not a framework (e.g. Spring or Grails), since this really is just a barebones Java web application. If you were
to create a Spring or Grails application, you would see that it detects both the runtime and the framework.

The result when visiting the 'Application Deployed URL' should look something like this:

	Hello from 172.30.49.150:20488

That's all. Have fun!
