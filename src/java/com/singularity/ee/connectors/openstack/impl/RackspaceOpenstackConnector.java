package com.singularity.ee.connectors.openstack.impl;

import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.entity.api.IProperty;
import com.singularity.ee.connectors.openstack.ClientLocator;
import com.singularity.ee.connectors.openstack.OpenStackConnector;
import com.singularity.ee.connectors.openstack.Utils;
import com.singularity.ee.openstack.nova.OpenStackClient;

public class RackspaceOpenstackConnector extends OpenStackConnector
{
	protected OpenStackClient getOpenStackClient(IProperty[] props, IControllerServices controllerServices)
			throws Exception
	{
		// location property field is either US/UK
		String location = Utils.getLocation(props, controllerServices);

		// assume location is US
		String authenticationUrl = "https://identity.api.rackspacecloud.com/v2.0/tokens";

		if (location.equals("UK"))
		{
			authenticationUrl = "https://lon.identity.api.rackspacecloud.com/v2.0/tokens";
		}

		return ClientLocator.getInstance().getClient(props, controllerServices, authenticationUrl);
	}

	protected String getFlavorRef(IProperty[] props, IControllerServices controllerServices)
	{
		String flavorRef = Utils.getFlavorRef(props, controllerServices);

		if (flavorRef.equals("RAM: 512MB Disk: 20GB"))
		{
			return "2";
		}
		else if (flavorRef.equals("RAM: 1GB Disk: 40GB"))
		{
			return "3";
		}
		else if (flavorRef.equals("RAM: 2GB Disk: 80GB"))
		{
			return "4";
		}
		else if (flavorRef.equals("RAM: 4GB Disk: 160GB"))
		{
			return "5";
		}
		else if (flavorRef.equals("RAM: 8GB Disk: 320GB"))
		{
			return "6";
		}
		else if (flavorRef.equals("RAM: 115GB Disk: 620GB"))
		{
			return "7";
		}
		else if (flavorRef.equals("RAM: 30GB Disk: 12TB"))
		{
			return "8";
		}
		else
		{
			return "2";
		}

	}
}
