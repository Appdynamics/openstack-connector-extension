
package com.singularity.ee.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class ApiKeyCredentials
{
	@SerializedName("username")
	private String username;
	
	@SerializedName("apiKey")
	private String apiKey;
	
	public String getUserName()
	{
		return username;
	}
	
	public String getApiKey()
	{
		return apiKey;
	}
	
	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}
	
	public void setUserName(String userName)
	{
		this.username = userName;
	}
	
	
}
