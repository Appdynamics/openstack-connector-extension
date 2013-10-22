package com.singularity.ee.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class AddFloatingIp
{
	@SerializedName("server")
	private String server;
	
	@SerializedName("address")
	private String address;
	
	public String getServerId()
	{
		return server;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public void setServerId(String server)
	{
		this.server = server;
	}
	
	public void setAddress(String address)
	{
		this.address = address;
	}
}
