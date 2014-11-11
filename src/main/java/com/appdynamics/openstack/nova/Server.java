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
package com.appdynamics.openstack.nova;

import com.appdynamics.connectors.openstack.enums.ServerStatus;

//simple java bean used to relay info back to the connector
public class Server
{
	private String name;
	private String id;
	private String adminPass;
	private ServerStatus status;
	
	public void setStatus(ServerStatus status)
	{
		this.status = status;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void setAdminPass(String adminPass)
	{
		this.adminPass = adminPass;
	}
	
	public ServerStatus getStatus()
	{
		return status;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getAdminPass()
	{
		return adminPass;
	}
}
