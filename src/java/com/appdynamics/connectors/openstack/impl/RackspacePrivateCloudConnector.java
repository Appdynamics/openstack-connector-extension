package com.appdynamics.connectors.openstack.impl;

import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.entity.api.IProperty;
import com.appdynamics.connectors.openstack.ClientLocator;
import com.appdynamics.connectors.openstack.OpenStackConnector;
import com.appdynamics.connectors.openstack.Utils;
import com.appdynamics.openstack.nova.OpenStackClient;

public class RackspacePrivateCloudConnector extends OpenStackConnector {

	@Override
	protected OpenStackClient getOpenStackClient(IProperty[] props,	IControllerServices controllerServices) throws Exception 
	{
		String authenticationUrl = Utils.getAuthenticationUrl(props, controllerServices);

		return ClientLocator.getInstance().getClient(props, controllerServices, authenticationUrl);

	}

	@Override
	protected String getFlavorRef(IProperty[] props, IControllerServices controllerServices)
	{
		String flavorRef = Utils.getFlavorRef(props, controllerServices);

		if (flavorRef.equals("m1.tiny"))
		{
			return "1";
		}
		else if (flavorRef.equals("m1.small"))
		{
			return "2";
		}
		else if (flavorRef.equals("m1.medium"))
		{
			return "3";
		}
		else if (flavorRef.equals("m1.large"))
		{
			return "4";
		}
		else if (flavorRef.equals("m1.xlarge"))
		{
			return "5";
		}
		else
		{
			return "2";
		}

	}

}
