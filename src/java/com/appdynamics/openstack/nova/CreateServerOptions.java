package com.appdynamics.openstack.nova;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.appdynamics.openstack.requestbody.json.SecurityGroup;

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
