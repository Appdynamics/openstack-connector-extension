package com.singularity.ee.connectors.openstack.impl;

import java.net.InetAddress;
import java.util.List;

import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.entity.api.IMachine;
import com.singularity.ee.connectors.entity.api.IProperty;
import com.singularity.ee.connectors.openstack.ClientLocator;
import com.singularity.ee.connectors.openstack.OpenStackConnector;
import com.singularity.ee.connectors.openstack.Utils;
import com.singularity.ee.openstack.nova.OpenStackClient;

public class HPCloudComputeConnector extends OpenStackConnector
{

	protected OpenStackClient getOpenStackClient(IProperty[] props, IControllerServices controllerServices)
			throws Exception
	{
		String authenticationUrl = "https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/tokens";

		return ClientLocator.getInstance().getClient(props, controllerServices, authenticationUrl);
	}

	protected String getFlavorRef(IProperty[] props, IControllerServices controllerServices)
	{
		String flavorRef = Utils.getFlavorRef(props, controllerServices);

		if (flavorRef.equals("standard.xsmall"))
		{
			return "100";
		}
		else if (flavorRef.equals("standard.small"))
		{
			return "101";
		}
		else if (flavorRef.equals("standard.medium"))
		{
			return "102";
		}
		else if (flavorRef.equals("standard.large"))
		{
			return "103";
		}
		else if (flavorRef.equals("standard.xlarge"))
		{
			return "104";
		}
		else if (flavorRef.equals("standard.2xlarge"))
		{
			return "105";
		}
		else
		{
			return "100";
		}

	}

	// The hp Rest api lists both public fixed ips and private ips as
	// "private ip addresses" in the api response body.
	// Only way to differentiate is the public ip address start with 15.*;
	// private start with 10.*
	@Override
	public String getIpAddress(IMachine machine, OpenStackClient client, IControllerServices controllerService)
			throws Exception
	{
		IProperty[] macProps = machine.getMachineDescriptor().getProperties();

		String region = Utils.getRegion(macProps, controllerService);

		List<String> ipAddress = client.getPrivateIpAddress(machine.getName(), region);

		if (ipAddress.size() != 0)
		{
			for (String str : ipAddress)
			{

				if (str.trim().startsWith("15."))
				{
					try
					{
						return InetAddress.getByName(str).getHostAddress();
					}
					catch (Exception e)
					{
						// search for the next ip
					}

				}
			}
		}
		else
		{

			ipAddress = client.getPublicIpAddress(machine.getName(), region);

			if (ipAddress.size() != 0)
			{
				return ipAddress.iterator().next();
			}
		}

		// return a random address
		ipAddress = client.getPrivateIpAddress(machine.getName(), region);

		return ipAddress.iterator().next();
	}

}
