package com.singularity.ee.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class Authentication
{
	
	@SerializedName("tenantId")
	private String tenantId;
	
	//api key credential field for Rackspace api.
	@SerializedName("RAX-KSKEY:apiKeyCredentials")
	private ApiKeyCredentials apiKeyCredentials;
	
	@SerializedName("apiAccessKeyCredentials")
	private ApiAccessKeyCredentials apiAccessKeyCredentials;
	
	@SerializedName("passwordCredentials")
	private PasswordCredentials passwordCredentials;

	public ApiAccessKeyCredentials getApiAccessKeyCredentials()
	{
		return apiAccessKeyCredentials;
	}
	
	public ApiKeyCredentials getApiKeyCredentials()
	{
		return apiKeyCredentials;
	}
	
	public PasswordCredentials getPasswordCredentials()
	{
		return passwordCredentials;
	}
	
	public String getTenantId()
	{
		return tenantId;
	}
	
	public void setTenantId(String tenantId)
	{
		this.tenantId = tenantId;
	}
	
	
	public void setPasswordCredentials(String userName, String password)
	{
		passwordCredentials = new PasswordCredentials();
		passwordCredentials.setUserName(userName);
		passwordCredentials.setPassword(password);
	}
	
	/**
	 * This is specific for Rackspace with field name: "RAX-KSKEY:apiKeyCredentials"
	 */
	public void setApiKeyCredentials(String userName, String apiKey)
	{
		apiKeyCredentials = new ApiKeyCredentials();
		apiKeyCredentials.setApiKey(apiKey);
		apiKeyCredentials.setUserName(userName);
	}
	
	public void setApiAccessKeyCredentials(String accessKey, String secretKey)
	{
		apiAccessKeyCredentials = new ApiAccessKeyCredentials();
		apiAccessKeyCredentials.setAccessKey(accessKey);
		apiAccessKeyCredentials.setSecretKey(secretKey);
	}
	
}


