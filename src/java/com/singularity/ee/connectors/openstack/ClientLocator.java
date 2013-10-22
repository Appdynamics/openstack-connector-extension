package com.singularity.ee.connectors.openstack;

import java.util.HashMap;
import java.util.Map;

import com.singularity.ee.connectors.api.ConnectorException;
import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.entity.api.IProperty;
import com.singularity.ee.openstack.nova.OpenStackClient;

public class ClientLocator
{
	private static final ClientLocator INSTANCE = new ClientLocator();

	private final Map<String, OpenStackClient> openStackClients = new HashMap<String, OpenStackClient>();

	private final Object connectorLock = new Object();

	public static ClientLocator getInstance()
	{
		return INSTANCE;
	}

	public OpenStackClient getClient(IProperty[] props, IControllerServices controllerServices,
			String authenticationUrl) throws Exception
	{
		String userName = Utils.getUserName(props, controllerServices);
		String password = Utils.getPassword(props, controllerServices);
		String apiKey = Utils.getApiKey(props, controllerServices);
		String accessKey = Utils.getAccessKey(props, controllerServices);
		String secretKey = Utils.getSecretKey(props, controllerServices);
		String tenantId = Utils.getTenantId(props, controllerServices);


		return getClient(userName, password, apiKey, accessKey, secretKey, tenantId, authenticationUrl, null);
	}

	public OpenStackClient getClient(IProperty[] props, IControllerServices controllerServices,
			String authenticationUrl, String computeServiceName) throws Exception
	{
		String userName = Utils.getUserName(props, controllerServices);
		String password = Utils.getPassword(props, controllerServices);
		String apiKey = Utils.getApiKey(props, controllerServices);
		String accessKey = Utils.getAccessKey(props, controllerServices);
		String secretKey = Utils.getSecretKey(props, controllerServices);
		String tenantId = Utils.getTenantId(props, controllerServices);


		return getClient(userName, password, apiKey, accessKey, secretKey, tenantId, authenticationUrl,
				computeServiceName);
	}

	public OpenStackClient getClient(String userName, String password, String apiKey, String accessKey,
			String secretKey, String tenantId, String authenticationUrl, String computeServiceName)
			throws ConnectorException, Exception
	{
		synchronized (connectorLock)
		{
			String identifier = userName + password + apiKey + accessKey + secretKey + tenantId + authenticationUrl + computeServiceName;
			

			if (openStackClients.containsKey(identifier))
			{
				return openStackClients.get(identifier); 
			}

			// for now there are only two connectors, Rackspace and HP; neither uses password for authentication.
			OpenStackClient client = new OpenStackClient(userName, password, tenantId, apiKey, secretKey,
					accessKey, authenticationUrl, computeServiceName);

			if (!client.authenticate())
			{
				throw new Exception("Invalid authentication");
			}
			
			openStackClients.put(identifier, client);

			return client;

		}

	}
}
