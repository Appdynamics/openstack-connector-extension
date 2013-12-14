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

//simple class used to relay floating ip info back to connector
public class FloatingIp
{
	private String fixedIp;
	private String id;
	private String instanceId;
	private String ip;
	private String pool;
	
	public String getFixedIp()
	{
		return fixedIp;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getInstanceId()
	{
		return instanceId;
	}
	
	public String getIp()
	{
		return ip;
	}
	
	public String getPool()
	{
		return pool;
	}
	
	public void setFixedIp(String fixedIp)
	{
		this.fixedIp = fixedIp;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void setInstanceId(String instanceId)
	{
		this.instanceId = instanceId;
	}
	
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	
	public void setPool(String pool)
	{
		this.pool = pool;
	}
}
