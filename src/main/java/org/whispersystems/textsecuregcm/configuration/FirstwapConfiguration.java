package org.whispersystems.textsecuregcm.configuration;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FirstwapConfiguration {

	@NotEmpty
	@JsonProperty
	private String url;
	
	@NotEmpty
	@JsonProperty
	private String username;
	
	@NotEmpty
	@JsonProperty
	private String password;
	
	@NotEmpty
	@JsonProperty
	private String senderId;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
}
