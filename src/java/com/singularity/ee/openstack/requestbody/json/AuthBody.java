package com.singularity.ee.openstack.requestbody.json;

import com.google.gson.annotations.SerializedName;

public class AuthBody
{
	@SerializedName("auth")
	private Authentication authBody;

	public void setApiKeyCredentials(String tenantId, String userName, String apiKey)
	{
		authBody = new Authentication();

		authBody.setTenantId(tenantId);
		authBody.setApiKeyCredentials(userName, apiKey);
	}

	public void setPasswordCredentials(String tenantId, String userName, String password)
	{
		authBody = new Authentication();

		authBody.setTenantId(tenantId);
		authBody.setPasswordCredentials(userName, password);
	}

	public void setApiAccessKeyCredentials(String tenantId, String accessKey, String secretKey)
	{
		authBody = new Authentication();

		authBody.setTenantId(tenantId);
		authBody.setApiAccessKeyCredentials(accessKey, secretKey);
	}

}
