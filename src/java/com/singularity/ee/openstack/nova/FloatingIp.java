package com.singularity.ee.openstack.nova;

//simple class used to relay floating ip info back to connector
public class FloatingIp
{
	private String fixedIp;
	private String id;
	private String instanceId;
	private String ip;
	private String pool;
	
	public String getFixedIp()
	{
		return fixedIp;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getInstanceId()
	{
		return instanceId;
	}
	
	public String getIp()
	{
		return ip;
	}
	
	public String getPool()
	{
		return pool;
	}
	
	public void setFixedIp(String fixedIp)
	{
		this.fixedIp = fixedIp;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void setInstanceId(String instanceId)
	{
		this.instanceId = instanceId;
	}
	
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	
	public void setPool(String pool)
	{
		this.pool = pool;
	}
}
