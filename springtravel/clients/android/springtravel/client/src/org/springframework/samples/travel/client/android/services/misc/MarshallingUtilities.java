package org.springframework.samples.travel.client.android.services.misc;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;

import java.io.*;

@SuppressWarnings("unchecked")
public class MarshallingUtilities {

	public MarshallingUtilities() {
	}

	public MarshallingUtilities(HttpMessageConverter simpleXmlHttpMessageConverter) {
		this.simpleXmlHttpMessageConverter = simpleXmlHttpMessageConverter;
	}

	private HttpMessageConverter simpleXmlHttpMessageConverter = new SimpleXmlHttpMessageConverter();

	public <T> T from(Class<? extends T> c, String i) throws Throwable {
		return (T) simpleXmlHttpMessageConverter.read(c, new MockHttpInputMessage(i));
	}

	public String to(Object b) throws Throwable {
		MockHttpOutputMessage mhom = new MockHttpOutputMessage();
		simpleXmlHttpMessageConverter.write(b, MediaType.TEXT_XML, mhom);
		return mhom.toString();
	}

	static class MockHttpInputMessage implements HttpInputMessage {

		private InputStream inputStream;

		private String body;

		public MockHttpInputMessage(String b) {
			this.body = b;
			this.inputStream = new ByteArrayInputStream(this.body.getBytes());
		}

		public InputStream getBody() throws IOException {
			return this.inputStream;
		}

		public HttpHeaders getHeaders() {
			return new HttpHeaders();
		}
	}

	static class MockHttpOutputMessage implements HttpOutputMessage {
		private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		public OutputStream getBody() throws IOException {
			return outputStream;
		}

		public HttpHeaders getHeaders() {
			return new HttpHeaders();
		}

		public String toString() {
			return new String(outputStream.toByteArray());
		}
	}
}
