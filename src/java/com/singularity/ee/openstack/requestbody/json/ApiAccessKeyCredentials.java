package com.singularity.ee.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class ApiAccessKeyCredentials
{

	@SerializedName("accessKey")
	private String accessKey;
	
	@SerializedName("secretKey")
	private String secretKey;
	
	public String getAccessKey()
	{
		return accessKey;
	}
	
	public String getSecretKey()
	{
		return secretKey;
	}
	
	public void setAccessKey(String accessKey)
	{
		this.accessKey = accessKey;
	}
	
	public void setSecretKey(String secretKey)
	{
		this.secretKey = secretKey;
	}

}
