package com.appdynamics.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class PasswordCredentials
{
	@SerializedName("username")
	private String username;
	
	@SerializedName("password")
	private String password;
	
	public String getUserName()
	{
		return username;
	}
	
	public String getApiKey()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void setUserName(String userName)
	{
		this.username = userName;
	}
	
}
