<%@ page language="java" import="org.apache.commons.httpclient.HttpClient"%>
<%@ page language="java" import="org.apache.commons.httpclient.HttpStatus"%>
<%@ page language="java" import="org.apache.commons.httpclient.methods.GetMethod"%>
<%@ page language="java" import="org.apache.commons.httpclient.methods.PostMethod"%>
<%@ page language="java" import="java.util.Enumeration"%>
<%@ page language="java" import="java.net.URLEncoder"%>
<%@ page language="java" import="javax.servlet.http.HttpServletRequest"%>
<%@ page language="java" import="java.io.IOException"%>
<%@ page language="java" import="java.io.InputStream"%>
<%@ page language="java" import="java.io.ByteArrayOutputStream"%>
<%@ page language="java" import="org.apache.commons.httpclient.methods.ByteArrayRequestEntity"%>





<%
	String rsp = "";
	String httpType = request.getMethod();
	String params = "";
	for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
		String paramName = (String) e.nextElement();
		String component = paramName + "=" + URLEncoder.encode(request.getParameter(paramName), "UTF-8");
		if(params.equals("")) {
			params = component;
		} else {
			params = params + "&" + component;
		}
	 }


	try {
		if(httpType.contains("GET")) {
		
			String url = "http://localhost:8080/travel/ws/hotels/search/?"+params;
			rsp = makeHttpGET(url);
		} else if(httpType.contains("POST")) {
			byte[] body = copyPostedData(request);
			String url = "http://localhost:8080/travel/ws/bookings/josh";
			rsp = makeHttpPOST(url, body);
		}
	} catch (Exception e) {
			rsp = e.toString();
	}
%>

<%=  rsp %>


<%!public String makeHttpGET(String url) {
	String result = "";
	HttpClient client = new HttpClient();
	GetMethod method = new GetMethod(url);
	method.addRequestHeader("Accept", "application/json");
	try {
		int statusCode = client.executeMethod(method);
		if (statusCode != HttpStatus.SC_OK) {
			result = method.getStatusLine().toString();
		} else {
			byte[] responseBody = method.getResponseBody();
			result = new String(responseBody);
		}
	} catch (Exception e) {
		result = e.toString();
	}
	return result;
}
%>

<%!public String makeHttpPOST(String url, byte[] body) {//just a demo http post
	String result = "";

	HttpClient client = new HttpClient();
    PostMethod method = new PostMethod(url);
    if (body != null) {
         method.setRequestEntity(new ByteArrayRequestEntity(body, "Application/json"));
    }
                    
    method.addRequestHeader("Content-Type", "Application/json");
    method.addRequestHeader("Accept", "Application/json");
	try {
		int statusCode = client.executeMethod(method);
		if (statusCode != HttpStatus.SC_OK) {
			result = method.getStatusLine().toString();
		} else {
			byte[] responseBody = method.getResponseBody();
			result = new String(responseBody);
		}
	} catch (Exception e) {
		result = e.toString();
	}
	return result;
}
%>

<%! public byte[] copyPostedData(HttpServletRequest req) throws IOException {
        int size = req.getContentLength();
        if (req.getMethod().equalsIgnoreCase("GET") || size <= 0) {
            return null;
        }
        InputStream is = req.getInputStream();
        ByteArrayOutputStream baos = null;
        try {
            if (size < 0)
                size = 0; 
            baos = new ByteArrayOutputStream(size);
            byte[] buffer = new byte[8192];
            int num;
            while ((num = is.read(buffer)) != -1) {
                baos.write(buffer, 0, num);
            }
            return baos.toByteArray();
        } finally {
            baos.close();
        }
    }
   %>
