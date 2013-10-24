package com.appdynamics.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class Personality
{
	@SerializedName("path")
	private String path;
	
	@SerializedName("contents")
	private String file;
	
	public String getPath()
	{
		return path;
	}
	
	public String getContent()
	{
		return file;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public void setContent(byte[] file)
	{
		this.file = new String(file);
	}
}
