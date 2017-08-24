package org.whispersystems.textsecuregcm.sms;

import static com.codahale.metrics.MetricRegistry.name;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.configuration.FirstwapConfiguration;
import org.whispersystems.textsecuregcm.util.Constants;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.google.common.base.Optional;

import io.dropwizard.client.HttpClientBuilder;

public class FirstwapSmsSender {
	
	private final Logger logger = LoggerFactory.getLogger(FirstwapSmsSender.class);
	
	private final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);
	private final Meter          smsMeter       = metricRegistry.meter(name(getClass(), "sms", "delivered"));
	
	HttpClient client;
	
	private final String url;
	private final String username;
	private final String password;
	private String senderId;
	
	public FirstwapSmsSender(FirstwapConfiguration config) {
		this.url = config.getUrl();
		this.username = config.getUsername();
		this.password = config.getPassword();
		this.senderId = config.getSenderId();
		
		client = new HttpClientBuilder(metricRegistry).build("FirstwapRestClient");
	}
	
	public void deliverSmsVerification(String destination, Optional<String> clientType, String verificationCode) throws Exception {
		destination = destination.replaceAll(Pattern.quote("+"), ""); // international format without plus sign
		
		String msg;
		if ("ios".equals(clientType.orNull())) {
			msg = String.format(SmsSender.SMS_IOS_VERIFICATION_TEXT, verificationCode, verificationCode);
		} else {
			msg = String.format(SmsSender.SMS_VERIFICATION_TEXT, verificationCode);
		}
		
		URI uri = new URIBuilder(url)
				.addParameter("g3p4i", username)
				.addParameter("G4PIpw", password)
				.addParameter("src", senderId)
				.addParameter("dst", destination)
				.addParameter("msg", msg)
				.build();
		
		HttpGet request = new HttpGet(uri);
		
		HttpResponse response = client.execute(request);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		
		StringBuilder result = new StringBuilder();
		result.append("Destination= " + destination + "\n");
		result.append("StatusCode= " + response.getStatusLine().getStatusCode() + "\n");
		result.append("Content:\n");
		
		String line = "";
		while ((line = br.readLine()) != null) {
			result.append(line);
		}
		
		logger.info(result.toString());
		
		smsMeter.mark();
	}

}
