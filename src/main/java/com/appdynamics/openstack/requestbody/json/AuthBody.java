/**
 * Copyright 2013 AppDynamics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appdynamics.openstack.requestbody.json;

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
