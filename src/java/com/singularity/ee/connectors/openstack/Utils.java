package com.singularity.ee.connectors.openstack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.entity.api.IProperty;


//Used to grab IProperty values. If the named property field doesn't exist, it will/should return null.
//Different service provider's connectors will have different property fields.
public class Utils
{
	//universal property fields
	public static final String IMAGE_REF = "Image Ref";
	public static final String FLAVOR_REF = "Flavor Ref";
	public static final String REGION = "Region";
	
	//property fields used by Rackspace Private Cloud
	public static final String PRIVATE_CLOUD_ENDPOINT_URL = "Nova API Endpoint Url";

	
	//property fields used by Rackspace Connector
	public static final String USER_NAME = "User Name"; //Also used by Rackspace Private Cloud
	public static final String API_KEY = "Api Key";
	public static final String LOCATION = "Location";
	public static final String ACCESS_IPV4_ADDRESS = "Access IPv4 Address";
	public static final String ACCESS_IPV6_ADDRESS = "Access IPv6 Address";
	public static final String ADMIN_PASSWORD = "Admin Password";
	
	//property fields used by HP Cloud Compute Connector
	public static final String ACCESS_KEY = "Access Key";
	public static final String SECRET_KEY = "Secret Key";
	public static final String TENANT_ID = "Tenant Id";
	public static final String SECURITY_GROUP = "Security Group";
	public static final String KEY_PAIR = "Key Pair";
	public static final String FLOATING_IP = "Floating Ip";
	
	public static final String COMPUTE_SERVICE_NAME = "Compute Service Name";
	public static final String AUTHENTICATION_URL = "Token Authentication Url";
	public static final String PASSWORD = "Password"; //Also used by Rackspace Private Cloud
	
	public static List<String> getSecurityGroup(IProperty[] properties, IControllerServices controllerServices)
	{
		String securityGroups = getValue(controllerServices.getStringPropertyValueByName(properties,
				SECURITY_GROUP));

		if (securityGroups == null)
		{
			return new ArrayList<String>();
		}

		String[] groups = securityGroups.split(";");

		return Arrays.asList(groups);
	}
	
	public static String getPassword(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, PASSWORD));
	}
	
	public static String getAuthenticationUrl(IProperty[] properties, IControllerServices controllerServices)
	{
		String url = getValue(controllerServices.getStringPropertyValueByName(properties, AUTHENTICATION_URL));
		
		if(url.endsWith("/"))
		{
			url = url.substring(0, url.length() - 1);
		}
		
		if(!url.endsWith("/tokens"))
		{
			url = url.concat("/tokens");
		}
		return url;
	}
	
	public static String getComputeServiceName(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, COMPUTE_SERVICE_NAME));
	}
	
	public static String getKeyPair(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, KEY_PAIR));
	}
	
	public static String getFloatingIp(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, FLOATING_IP));
	}

	protected static String getApiKey(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, API_KEY));
	}
	
	protected static String getAccessKey(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, ACCESS_KEY));
	}
	
	protected static String getSecretKey(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, SECRET_KEY));
	}

	protected static String getTenantId(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, TENANT_ID));
	}

	public static String getAccessIPv4Address(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, ACCESS_IPV4_ADDRESS));
	}

	public static String getAccessIPv6Address(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, ACCESS_IPV6_ADDRESS));
	}
	
	public static String getLocation(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, LOCATION));
	}

	protected static String getAdminPass(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, ADMIN_PASSWORD));
	}

	protected static String getUserName(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, USER_NAME));
	}

	public static String getRegion(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, REGION));
	}

	public static String getImageRef(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, IMAGE_REF));
	}

	public static String getFlavorRef(IProperty[] properties, IControllerServices controllerServices)
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, FLAVOR_REF));
	}

	private static String getValue(String value)
	{
		return (value == null || value.trim().length() == 0) ? null : value.trim();
	}

	public static String getPrivateCloudEndpointUrl(IProperty[] properties, IControllerServices controllerServices) 
	{
		return getValue(controllerServices.getStringPropertyValueByName(properties, PRIVATE_CLOUD_ENDPOINT_URL));
	}

}
