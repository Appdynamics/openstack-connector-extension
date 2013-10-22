package com.singularity.ee.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class SecurityGroup
{
	@SerializedName("name")
	private String securityGroup;
	
	public String getSecurityGroup()
	{
		return securityGroup;
	}
	
	public void setSecurityGroup(String securityGroup)
	{
		this.securityGroup = securityGroup;
	}
}
