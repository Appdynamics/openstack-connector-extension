package com.appdynamics.connectors.openstack.impl;

import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.entity.api.IProperty;
import com.appdynamics.connectors.openstack.ClientLocator;
import com.appdynamics.connectors.openstack.OpenStackConnector;
import com.appdynamics.connectors.openstack.Utils;
import com.appdynamics.openstack.nova.OpenStackClient;

public class GenericOpenStackConnector extends OpenStackConnector
{

	@Override
	protected OpenStackClient getOpenStackClient(IProperty[] props, IControllerServices controllerServices)
			throws Exception
	{
		String authenticationUrl = Utils.getAuthenticationUrl(props, controllerServices);
		String computeServicename = Utils.getComputeServiceName(props, controllerServices);
		
		return ClientLocator.getInstance().getClient(props, controllerServices, authenticationUrl, computeServicename);
		
	}

	@Override
	protected String getFlavorRef(IProperty[] props, IControllerServices controllerServices) throws Exception
	{
		String flavorRef = Utils.getFlavorRef(props, controllerServices);
		
		return flavorRef;
	}

}
