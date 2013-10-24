package com.appdynamics.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class RebootBody
{
	@SerializedName("reboot")
	private Reboot reboot;
	
	public void setReboot(String type)
	{
		this.reboot = new Reboot();
		reboot.setType(type);
	}

	public Reboot getReboot()
	{
		return reboot;
	}
}
