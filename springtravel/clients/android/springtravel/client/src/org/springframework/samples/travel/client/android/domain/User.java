package org.springframework.samples.travel.client.android.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Simple XML-based client side classes that mirror the server-side entities.
 * <p/>
 * Hotel information is represented through this class.
 *
 * @author Josh Long
 */
@Root
public class User {

	@Attribute(required = false)
	private String name;

	@Attribute(required = false)
	private String username;

	@Attribute(required = false)
	private String password;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public User() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User(" + username + ", " + this.password + ", " + this.name + ")";
	}
}
