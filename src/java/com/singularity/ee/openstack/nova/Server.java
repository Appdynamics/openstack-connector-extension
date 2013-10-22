package com.singularity.ee.openstack.nova;

import com.singularity.ee.connectors.openstack.enums.ServerStatus;

//simple java bean used to relay info back to the connector
public class Server
{
	private String name;
	private String id;
	private String adminPass;
	private ServerStatus status;
	
	public void setStatus(ServerStatus status)
	{
		this.status = status;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void setAdminPass(String adminPass)
	{
		this.adminPass = adminPass;
	}
	
	public ServerStatus getStatus()
	{
		return status;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getAdminPass()
	{
		return adminPass;
	}
}
