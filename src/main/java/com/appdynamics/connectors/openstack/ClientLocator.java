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
package com.appdynamics.connectors.openstack;

import com.appdynamics.openstack.nova.OpenStackClient;
import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.entity.api.IProperty;

import java.util.HashMap;
import java.util.Map;

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
			throws Exception
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
