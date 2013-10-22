package com.singularity.ee.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class Reboot
{
	@SerializedName( "type")
	private String type;
	
	public void setType(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return type;
	}
}
