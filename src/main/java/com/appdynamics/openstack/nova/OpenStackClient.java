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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.appdynamics.connectors.openstack.enums.RebootType;
import com.appdynamics.connectors.openstack.enums.ServerStatus;
import com.appdynamics.openstack.requestbody.json.AddFloatingIpBody;
import com.appdynamics.openstack.requestbody.json.AuthBody;
import com.appdynamics.openstack.requestbody.json.OpenStackServer;
import com.appdynamics.openstack.requestbody.json.OpenStackServerBody;
import com.appdynamics.openstack.requestbody.json.RebootBody;
import com.singularity.ee.util.clock.ClockUtils;

//TODO: add better/helpful error messages.

/**
 * 
 * @author Jeffrey
 * 
 *         class used to call the Openstack Rest Api. The api calls use json request bodies since different service providers have
 *         different xml namespace for the request body; Json negates the namespace issues and should work for all providers. The
 *         response bodies are in xml format. We only need a few fields from the responses, so they are not deserialized (also to
 *         avoid possible namespace issues).
 */

public class OpenStackClient
{
	private String userName;
	private String tenantId;
	private String password;
	private String apiKey;
	private String secretKey;
	private String accessKey;
	private String authenticationUrl;
	private String serviceName;
	private String authenticationToken;
	private long tokenCreationTime;

	
	// security token expires in 24 hours for openstack
	public static final int TOKEN_LIFETIME_HOUR = 24;

	private static Map<String, String> serviceEndPointURLs = new HashMap<String, String>();

	/**
	 * Must provide the necessary credential when initiating this class
	 * 
	 * serviceName : The name of the compute service shown in the service catalogue in the authentication response body. If this
	 * is not specified, the code will search for any service with the type "compute" in the authentication response
	 */
	public OpenStackClient(String userName, String password, String tenantId, String apiKey,
			String secretKey, String accessKey, String authenticationUrl, String serviceName)
	{
		this.serviceName = serviceName;
		this.userName = userName;
		this.authenticationUrl = authenticationUrl;
		this.tenantId = tenantId;
		this.password = password;
		this.accessKey = accessKey;
		this.apiKey = apiKey;
		this.secretKey = secretKey;
		this.authenticationToken = null;

	}

	public boolean authenticate() throws Exception
	{
		AuthBody authBody = new AuthBody();

		if (apiKey != null && userName != null)
		{
			authBody.setApiKeyCredentials(tenantId, userName, apiKey);
		}
		else if (accessKey != null && secretKey != null)
		{
			authBody.setApiAccessKeyCredentials(tenantId, accessKey, secretKey);
		}
		else if (password != null && userName != null)
		{
			authBody.setPasswordCredentials(tenantId, userName, password);
		}
		else
		{
			throw new Exception("Must supply the valid credentials");
		}

		Gson gson = new Gson();

		String requestBody = gson.toJson(authBody);
		String response = RestClient.doPost(authenticationUrl, requestBody);

		Document doc = stringToXmlDocument(response);

		if (doc.getElementsByTagName("unauthorized").getLength() != 0)
		{
			return false;
		}

		if (containsErrorMessage(response))
		{
			throw new Exception("Error authenticating credentials. Server Response:"
					+ getErrorMessage(response));
		}

		// only specific elements need to be parsed from the massive response
		// body..
		NodeList token = doc.getElementsByTagName("token");

		try
		{
			authenticationToken = token.item(0).getAttributes().getNamedItem("id").getTextContent().trim();
			tokenCreationTime = ClockUtils.getCurrentTime();

			XPathFactory xpf = XPathFactory.newInstance();
			XPath xPath = xpf.newXPath();
			XPathExpression xpe = null;

			if (serviceName == null)
			{
				xpe = xPath.compile(String.format("//service[@type=\"%s\"]", "compute"));
			}
			else
			{
				xpe = xPath.compile(String.format("//service[@name=\"%s\"]", serviceName));
			}

			NodeList service = (NodeList) xpe.evaluate(doc, XPathConstants.NODESET);

			for (int s = 0; s < service.getLength(); s++)
			{
				NodeList endPoints = service.item(s).getChildNodes();

				for (int i = 0; i < endPoints.getLength(); i++)
				{
				
					if (endPoints.item(i).getNodeType() != Node.ELEMENT_NODE || endPoints.item(i).getAttributes().getNamedItem("region") == null
							|| endPoints.item(i).getAttributes().getNamedItem("publicURL") == null)
					{
						continue;
					}

					String region = endPoints.item(i).getAttributes().getNamedItem("region").getTextContent()
							.trim();
					String publicUrl = endPoints.item(i).getAttributes().getNamedItem("publicURL")
							.getTextContent().trim();

					serviceEndPointURLs.put(region, publicUrl);
				}
			}

		}
		catch (Exception e)
		{
			throw new Exception("Unable to retrieve compute service endpoints. " + e);
		}

		if (serviceEndPointURLs.size() == 0 )
		{
			throw new Exception("Openstack Compute public URL endpoints not found");
		}

		return true;

	}

	public Server createServer(String region, String flavorRef, String imageRef, String name,
			CreateServerOptions options) throws Exception
	{
		OpenStackServer oss = new OpenStackServer();
		oss.setFlavorRef(flavorRef);
		oss.setImageRef(imageRef);
		oss.setName(name);
		// oss.setDiskConfig("AUTO");
		oss.setIPv4(options.getAccessIPv4());
		oss.setIPv6(options.getAccessIPv6());
		oss.setAdminPass(options.getAdminPass());
		oss.addPersonality(options.getFilePath(), options.getFile());

		oss.setKeyPair(options.getKeyPair());
		oss.setSecurityGroup(options.getSecurityGroup());
        oss.setMetadata(options.getMetadata());

		OpenStackServerBody rssb = new OpenStackServerBody();
		rssb.setServer(oss);

		Gson gson = new Gson();
		String requestBody = gson.toJson(rssb);
		String token = getAuthenticationToken();
		String url = getEndpointURL(region) + OpenStackApiUri.CREATE_SERVER_URI;

		String response = RestClient.doPost(url, requestBody, token);

		Document doc = stringToXmlDocument(response);

		if (containsErrorMessage(response))
		{
			throw new Exception("Error creating server. Server Response:" + getErrorMessage(response));
		}

		String id = doc.getElementsByTagName("server").item(0).getAttributes().getNamedItem("id")
				.getTextContent().trim();

		String adminPassword = doc.getElementsByTagName("server").item(0).getAttributes()
				.getNamedItem("adminPass").getTextContent().trim();

		Server server = new Server();
		server.setId(id);
		server.setName(name);
		server.setAdminPass(adminPassword);

		return server;
	}

	private String getEndpointURL(String region) throws Exception
	{
		if (authenticationToken == null)
		{
			getAuthenticationToken();
		}
		

		if (serviceEndPointURLs.containsKey(region))
		{
			return serviceEndPointURLs.get(region);
		}
		else
		{
			throw new Exception("Region: " + region + " not found");
		}
	}


	
	// TODO:better error message..
	// grab and/or refresh the authentication token
	private String getAuthenticationToken() throws Exception
	{
		if (authenticationToken == null)
		{
			if (!authenticate())
			{
				throw new Exception("Could not validate credentials");
			}
		}
		else
		{
			// update token if it only has 1 hour of its lifetime left
			long minAge = ClockUtils.getCurrentTime() - (TOKEN_LIFETIME_HOUR - 1) * 60 * 60 * 1000;

			if (tokenCreationTime < minAge)
			{
				if (!authenticate())
				{
					throw new Exception("Could not validate credentials");
				}
			}
		}

		return authenticationToken;
	}

	public boolean rebootServer(String id, String region, RebootType rebootType) throws Exception
	{
		RebootBody rebootBody = new RebootBody();
		rebootBody.setReboot(rebootType.toString());
		Gson gson = new Gson();
		String requestBody = gson.toJson(rebootBody);
		String token = getAuthenticationToken();

		String url = getEndpointURL(region) + String.format(OpenStackApiUri.REBOOT_SERVER_URI, id);

		// the response should be empty if the reboot is successful
		String response = RestClient.doPost(url, requestBody, token);

		if (containsErrorMessage(response))
		{
			throw new Exception("Error rebooting server. Server Response:" + getErrorMessage(response));
		}

		return response.trim().equals("");

	}

	public ServerStatus getServerStatus(String id, String region) throws Exception
	{
		String token = getAuthenticationToken();
		String url = getEndpointURL(region) + String.format(OpenStackApiUri.GET_SERVER_URI, id);

		String response = RestClient.doGet(url, token);

		Document doc = stringToXmlDocument(response);

		// means server is not found.
		if (containsErrorMessage(response))
		{
			return null;
		}

		String status = doc.getElementsByTagName("server").item(0).getAttributes().getNamedItem("status")
				.getTextContent().trim();

		try
		{
			ServerStatus serverStatus = ServerStatus.valueOf(status.trim().toUpperCase());
			return serverStatus;
		}
		catch (Exception e)
		{
			return ServerStatus.UNKOWN;
		}
	}

	private Document getIpAddressDoc(String id, String region) throws Exception
	{
		String token = getAuthenticationToken();
		String url = getEndpointURL(region) + String.format(OpenStackApiUri.GET_SERVER_IP_ADDRESSES_URI, id);

		String response = RestClient.doGet(url, token);

		if (containsErrorMessage(response))
		{
			throw new Exception("Error retrieving Ip Address. Server Response:" + getErrorMessage(response));
		}

		Document doc = stringToXmlDocument(response);

		return doc;
	}

	public List<String> getPublicIpAddress(String id, String region) throws Exception
	{
		Document doc = getIpAddressDoc(id, region);

		XPathFactory xpf = XPathFactory.newInstance();
		XPath xPath = xpf.newXPath();
		XPathExpression xpe = xPath.compile("//network[@id=\"public\"]/ip");

		NodeList publicNetwork = (NodeList) xpe.evaluate(doc, XPathConstants.NODESET);

		if (publicNetwork.getLength() == 0)
		{
			return Collections.<String> emptyList();
		}
		else
		{
			List<String> publicIpAddresses = new ArrayList<String>();

			for (int i = 0; i < publicNetwork.getLength(); i++)
			{
				publicIpAddresses.add(publicNetwork.item(i).getAttributes().getNamedItem("addr")
						.getTextContent().trim());
			}

			return publicIpAddresses;
		}

	}

	public List<String> getPublicIPv4Address(String id, String region) throws Exception
	{
		Document doc = getIpAddressDoc(id, region);

		XPathFactory xpf = XPathFactory.newInstance();
		XPath xPath = xpf.newXPath();
		XPathExpression xpe = xPath.compile("//network[@id=\"public\"]/ip[@version=\"4\"]");

		NodeList ipv4Addresses = (NodeList) xpe.evaluate(doc, XPathConstants.NODESET);

		if (ipv4Addresses.getLength() == 0)
		{
			return Collections.<String> emptyList();
		}
		else
		{
			List<String> publicIPv4Addresses = new ArrayList<String>();

			for (int i = 0; i < ipv4Addresses.getLength(); i++)
			{
				publicIPv4Addresses.add(ipv4Addresses.item(i).getAttributes().getNamedItem("addr")
						.getTextContent().trim());
			}

			return publicIPv4Addresses;
		}
	}

	public List<String> getPublicIPv6Address(String id, String region) throws Exception
	{
		Document doc = getIpAddressDoc(id, region);

		XPathFactory xpf = XPathFactory.newInstance();
		XPath xPath = xpf.newXPath();
		XPathExpression xpe = xPath.compile("//network[@id=\"public\"]/ip[@version=\"6\"]");

		NodeList ipv6Addresses = (NodeList) xpe.evaluate(doc, XPathConstants.NODESET);

		if (ipv6Addresses.getLength() == 0)
		{
			return Collections.<String> emptyList();
		}
		else
		{
			List<String> publicIPv6Addresses = new ArrayList<String>();

			for (int i = 0; i < ipv6Addresses.getLength(); i++)
			{
				publicIPv6Addresses.add(ipv6Addresses.item(i).getAttributes().getNamedItem("addr")
						.getTextContent().trim());
			}

			return publicIPv6Addresses;
		}

	}

	public List<String> getPrivateIpAddress(String id, String region) throws Exception
	{
		Document doc = getIpAddressDoc(id, region);

		XPathFactory xpf = XPathFactory.newInstance();
		XPath xPath = xpf.newXPath();
		XPathExpression xpe = xPath.compile("//network[@id=\"private\"]/ip");

		NodeList privateNetwork = (NodeList) xpe.evaluate(doc, XPathConstants.NODESET);

		if (privateNetwork.getLength() == 0)
		{
			return Collections.<String> emptyList();
		}
		else
		{
			List<String> privateIpAddresses = new ArrayList<String>();

			for (int i = 0; i < privateNetwork.getLength(); i++)
			{
				privateIpAddresses.add(privateNetwork.item(i).getAttributes().getNamedItem("addr")
						.getTextContent().trim());
			}

			return privateIpAddresses;
		}
	}

	public boolean deleteServer(String id, String region) throws Exception
	{
		String token = getAuthenticationToken();
		String url = getEndpointURL(region) + String.format(OpenStackApiUri.DELETE_SERVER_URI, id);

		// the response is empty if the delete is successful
		String response = RestClient.doDelete(url, token);

		if (containsErrorMessage(response))
		{
			// if server doesn't actually exist anymore, return true;
			if (getServerStatus(id, region) == null)
			{
				return true;
			}

			throw new Exception("Error deleting server. Server Response:" + getErrorMessage(response));
		}

		return response.trim().equals("");
	}

	public boolean assignFloatingIP(String id, String address, String region) throws Exception
	{
		String token = getAuthenticationToken();
		String url = getEndpointURL(region) + String.format(OpenStackApiUri.ADD_FLOATING_IP, id);

		AddFloatingIpBody floatingIp = new AddFloatingIpBody();
		floatingIp.setFloatingIp(id, address);
		Gson gson = new Gson();
		String requestBody = gson.toJson(floatingIp);

		String response = RestClient.doPost(url, requestBody, token);

		if (containsErrorMessage(response))
		{
			throw new Exception("Error assigning server with floating Ip address. Server Response:"
					+ getErrorMessage(response));
		}

		return true;
	}

	public List<FloatingIp> listFloatingIp(String region) throws Exception
	{
		String token = getAuthenticationToken();
		String url = getEndpointURL(region) + OpenStackApiUri.LIST_FLOATING_IPS;

		String response = RestClient.doGet(url, token);

		Document doc = stringToXmlDocument(response);

		NodeList list = doc.getElementsByTagName("floating_ip");
		List<FloatingIp> ips = new ArrayList<FloatingIp>();

		for (int i = 0; i < list.getLength(); i++)
		{
			Element element = (Element) list.item(i);
			FloatingIp ip = new FloatingIp();
			ip.setFixedIp(element.getElementsByTagName("fixed_ip").item(0).getTextContent().trim());
			ip.setId(element.getElementsByTagName("id").item(0).getTextContent().trim());
			ip.setInstanceId(element.getElementsByTagName("instance_id").item(0).getTextContent().trim());
			ip.setIp(element.getElementsByTagName("ip").item(0).getTextContent().trim());

			if (element.getElementsByTagName("pool").getLength() != 0)
			{
				ip.setPool(element.getElementsByTagName("pool").item(0).getTextContent().trim());
			}

			ips.add(ip);
		}

		return ips;
	}

	// error messages contains message tags in the format of i.e.<message> The resource could not be found. </message>
	public boolean containsErrorMessage(String response) throws Exception
	{
		if (response!= null && response.trim().length() != 0 )
		{
			Document doc = stringToXmlDocument(response);

			return (doc.getElementsByTagName("message").getLength() != 0);
		}

		return false;
	}

	public String getErrorMessage(String response) throws Exception
	{
		if (response == null)
		{
			return "Null response from server";
		}

		Document doc = stringToXmlDocument(response);

		return doc.getElementsByTagName("message").item(0).getTextContent().trim();
	}

	private Document stringToXmlDocument(String document) throws Exception
	{
		try
		{
			InputStream ouputStream = new ByteArrayInputStream(document.getBytes("UTF-8"));

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(ouputStream);

			doc.getDocumentElement().normalize();

			return doc;
		}
		catch (Exception e)
		{
			// TODO:throw exceptions
			throw new Exception("Error reading xml response. Response body:" + document + "\n" + e);
		}
	}
}
