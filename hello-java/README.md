Hello Java Sample
=================

This sample aims to demonstrate the simplest possible Servlet-based Java webapp. Here we walk through the entire content of the application.

The Servlet
-----------

In the *org.cloudfoundry.samples* package under *src/main/java*, you will see *HelloServlet.java*. It's contents are:

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
You will find that under *src/main/webapp*. It's contents are:

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
