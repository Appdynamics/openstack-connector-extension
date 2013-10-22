package com.singularity.ee.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class AddFloatingIpBody
{
	@SerializedName("addFloatingIp")
	AddFloatingIp floatingIp;
	
	public AddFloatingIp getFloatingIp()
	{
		return floatingIp;
	}
	
	public void setFloatingIp(String serverId, String address)
	{
		floatingIp = new AddFloatingIp();
		floatingIp.setAddress(address);
		floatingIp.setServerId(serverId);
	}
}
