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

import com.appdynamics.openstack.requestbody.json.SecurityGroup;
import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jeffrey
 *
 *         Optional fields when creating a server. Different service provider might have different fields.

 */
public class CreateServerOptions
{

	private byte[] file;
	private String filePath;

	private String adminPass;
	private String accessIPv4;
	private String accessIPv6;

	private String floatingIp;
	private String keyPair;
	private List<SecurityGroup> securityGroup = null;
    private Map<String, String> metadata = new HashMap<String, String>();


    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        if(metadata != null) {
            this.metadata = metadata;
        }
    }

	public void addSecurityGroup(String securityGroup)
	{
		SecurityGroup s = new SecurityGroup();
		s.setSecurityGroup(securityGroup);
		if (this.securityGroup == null)
		{
			this.securityGroup = new ArrayList<SecurityGroup>();
		}

		this.securityGroup.add(s);
	}

	public void setFile(String file, String filePath)
	{
		this.file = Base64.encodeBase64(file.getBytes());
		this.filePath = filePath;
	}

	public void setEncodedFile(String encodedFile, String filePath)
	{
		this.file = encodedFile.getBytes();
		this.filePath = filePath;
	}

	public void setKeyPair(String keyPair)
	{
		this.keyPair = keyPair;
	}

	public void setFloatingIp(String floatingIp)
	{
		this.floatingIp = floatingIp;
	}

	public void setFile(byte[] file, String filePath)
	{
		this.file = Base64.encodeBase64(file);
		this.filePath = filePath;
	}

	public byte[] getFile()
	{
		return file == null || file.length == 0 ? null : file;
	}

	public String getFilePath()
	{
		return filePath == null || filePath.trim().length() == 0 ? null : filePath.trim();
	}

	public void setAdminPass(String adminPass)
	{
		this.adminPass = adminPass;
	}

	public void setAccessIPv4(String accessIPv4)
	{
		this.accessIPv4 = accessIPv4;
	}

	public void setAccessIPv6(String accessIPv6)
	{
		this.accessIPv6 = accessIPv6;
	}

	public List<SecurityGroup> getSecurityGroup()
	{
		return securityGroup;
	}

	public String getAccessIPv4()
	{
		return accessIPv4 == null || accessIPv4.trim().length() == 0 ? null : accessIPv4.trim();
	}

	public String getAccessIPv6()
	{
		return accessIPv6 == null || accessIPv6.trim().length() == 0 ? null : accessIPv6.trim();
	}

	public String getAdminPass()
	{
		return adminPass == null || adminPass.trim().length() == 0 ? null : adminPass.trim();
	}

	public String getKeyPair()
	{
		return keyPair == null || keyPair.trim().length() == 0 ? null : keyPair.trim();
	}

	public String getFloatingIp()
	{
		return floatingIp == null || floatingIp.trim().length() == 0 ? null : floatingIp.trim();
	}
}
