package com.standarddeviationanalysis.httprequest;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpRequest {
	private static HttpClient client = HttpClients.createDefault();
	private static HttpUriRequest httpRequest = null;

	public static String executeRequestAndGetResponse(String url) throws ClientProtocolException, IOException {
		httpRequest = new HttpGet(url);
		return EntityUtils.toString(client.execute(httpRequest).getEntity());
	}
}
