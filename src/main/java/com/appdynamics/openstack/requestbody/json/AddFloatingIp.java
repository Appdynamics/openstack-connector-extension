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

public class AddFloatingIp
{
	@SerializedName("server")
	private String server;
	
	@SerializedName("address")
	private String address;
	
	public String getServerId()
	{
		return server;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public void setServerId(String server)
	{
		this.server = server;
	}
	
	public void setAddress(String address)
	{
		this.address = address;
	}
}
