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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class OpenStackServer
{
	@SerializedName("name")
	private String name;
	@SerializedName("imageRef")
	private String imageRef;
	@SerializedName("flavorRef")
	private String flavorRef;
	@SerializedName("diskConfig")
	private String diskConfig;

	@SerializedName("personality")
	private List<Personality> personality = null;

	@SerializedName("security_groups")
	private List<SecurityGroup> securityGroup = null;

	@SerializedName("accessIPv4")
	private String accessIPv4;
	@SerializedName("accessIPv6")
	private String accessIPv6;
	@SerializedName("adminPass")
	private String adminPass;
	
	@SerializedName("key_name")
	private String keyPair;

	public void setKeyPair(String keyPair)
	{
		this.keyPair = keyPair;
	}
	
	public String getKeyPair()
	{
		return keyPair;
	}
	
	public String getAccessIPv4()
	{
		return accessIPv4;
	}

	public String getAccessIPv6()
	{
		return accessIPv6;
	}

	public String getAdminPass()
	{
		return adminPass;
	}

	public void setIPv4(String accessIPv4)
	{
		this.accessIPv4 = accessIPv4;
	}

	public void setIPv6(String accessIPv6)
	{
		this.accessIPv6 = accessIPv6;
	}

	public void setAdminPass(String adminPass)
	{
		this.adminPass = adminPass;
	}

	public List<Personality> getPersonality()
	{
		return personality;
	}
	
	public List<SecurityGroup> getSecurityGroup()
	{
		return securityGroup;
	}

	public String getImageRef()
	{
		return imageRef;
	}

	public String getFlavorRef()
	{
		return flavorRef;
	}

	public String getDiskConfig()
	{
		return diskConfig;
	}

	public String getName()
	{
		return name;
	}

	public void setImageRef(String imageId)
	{
		this.imageRef = imageId;
	}

	public void setFlavorRef(String flavorId)
	{
		this.flavorRef = flavorId;
	}

	public void setDiskConfig(String diskConfig)
	{
		this.diskConfig = diskConfig;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void addSecurityGroup(String securityGroup)
	{
		SecurityGroup s = new SecurityGroup();

		if (this.securityGroup == null)
		{
			this.securityGroup = new ArrayList<SecurityGroup>();
		}

		this.securityGroup.add(s);
	}
	
	public void setSecurityGroup(List<SecurityGroup> securityGroup)
	{
		this.securityGroup = securityGroup;
	}

	public void addPersonality(String path, byte[] file)
	{
		if (file != null)
		{
			Personality p = new Personality();
			p.setContent(file);
			p.setPath(path);

			if (this.personality == null)
			{
				this.personality = new ArrayList<Personality>();
			}

			this.personality.add(p);
		}
	}
}
