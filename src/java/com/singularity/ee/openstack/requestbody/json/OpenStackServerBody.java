package com.singularity.ee.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class OpenStackServerBody
{
	@SerializedName("server")
	private OpenStackServer server;
	
	public OpenStackServer getServer()
	{
		return server;
	}
	
	public void setServer(OpenStackServer server)
	{
		this.server = server;
	}
}
