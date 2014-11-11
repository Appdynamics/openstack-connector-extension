/**
 * Copyright 2013 AppDynamics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appdynamics.openstack.nova;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

//used by openstack client to make the rest api calls
//TODO:possibly add async calls instead.
class RestClient
{

	private static final String PROXY_HOST_PROPERTY = "com.singularity.httpclientwrapper.proxyHost";

	private static final String PROXY_PORT_PROPERTY = "com.singularity.httpclientwrapper.proxyPort";

	private static final String xmlContentType = "application/xml";
	private static final String jsonContentType = "application/json";

	private static final int timeout = 60000;

	static String doPost(String url, String entity) throws Exception
	{
		return doPost(url, entity, null);
	}

	// for authorization, creating servers, reboot server
	static String doPost(String url, String entity, String token) throws Exception
	{
		HttpClient httpClient = httpClientWithTrustManager();
		StringEntity strEntity = new StringEntity(entity);
		strEntity.setContentType(jsonContentType);

		HttpPost post = new HttpPost(url);
		post.addHeader("accept", xmlContentType);
		post.setEntity(strEntity);

		if (token != null)
		{
			post.addHeader("X-Auth-Token", token);
		}

		HttpResponse response = httpClient.execute(post);

		return getResponseString(httpClient, response);

	}

	// for get server ip address, server status, get server
	static String doGet(String url, String token) throws Exception
	{
		HttpClient httpClient = httpClientWithTrustManager();

		HttpGet get = new HttpGet(url);
		get.addHeader("accept", xmlContentType);
		get.addHeader("X-Auth-Token", token);

		HttpResponse response = httpClient.execute(get);

		return getResponseString(httpClient, response);
	}

	// for delete server
	static String doDelete(String url, String token) throws Exception
	{
		HttpClient httpClient = httpClientWithTrustManager();

		HttpDelete delete = new HttpDelete(url);
		delete.addHeader("accept", xmlContentType);
		delete.addHeader("X-Auth-Token", token);

		HttpResponse response = httpClient.execute(delete);

		return getResponseString(httpClient, response);
	}

	static String getResponseString(HttpClient httpClient, HttpResponse response) throws Exception
	{
		try
		{
			HttpEntity reponseEntity = response.getEntity();

			if (reponseEntity != null)
			{
				String responseString = EntityUtils.toString(reponseEntity);

				return responseString;
			}
			else
			{
				return "";
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			httpClient.getConnectionManager().shutdown();
		}
	}

	// prevent javax from throwing authentication exception if the host server does not have valid SSL certificates
	static HttpClient httpClientWithTrustManager() throws KeyManagementException, NoSuchAlgorithmException
	{
		HttpClient httpClient = new DefaultHttpClient();

		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);

		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);

		httpClient.getParams().setParameter("http.connection-manager.max-per-host", 1);
		
		X509TrustManager tm = new X509TrustManager()
		{

			@Override
			public X509Certificate[] getAcceptedIssuers()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
			{
				// TODO Auto-generated method stub

			}
		};

		SSLContext ctx = SSLContext.getInstance("TLS");

		ctx.init(null, new TrustManager[] { tm }, null);

		SSLSocketFactory ssf = new SSLSocketFactory(ctx);

		ClientConnectionManager ccm = httpClient.getConnectionManager();

		SchemeRegistry sr = ccm.getSchemeRegistry();

		sr.register(new Scheme("https", ssf, 443)); // Scheme("https", ssf, 443));

		return new DefaultHttpClient(ccm, httpClient.getParams());

	}
}
