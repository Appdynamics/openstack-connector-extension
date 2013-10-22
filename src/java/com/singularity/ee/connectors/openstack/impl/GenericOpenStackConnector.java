package com.singularity.ee.connectors.openstack.impl;

import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.entity.api.IProperty;
import com.singularity.ee.connectors.openstack.ClientLocator;
import com.singularity.ee.connectors.openstack.OpenStackConnector;
import com.singularity.ee.connectors.openstack.Utils;
import com.singularity.ee.openstack.nova.OpenStackClient;

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
