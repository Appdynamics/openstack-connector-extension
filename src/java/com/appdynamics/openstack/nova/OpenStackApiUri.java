package com.appdynamics.openstack.nova;

//URI for the REST Api calls
public class OpenStackApiUri
{
	public static final String CREATE_SERVER_URI = "/servers";
	
	//replace %s with server id
	public static final String GET_SERVER_URI = "/servers/%s";
	
	public static final String LIST_SERVER ="/servers";
	
	//replace %s with the server id.
	public static final String REBOOT_SERVER_URI = "/servers/%s/action";
	
	//replace %s with server id
	public static final String GET_SERVER_IP_ADDRESSES_URI = "/servers/%s/ips";
	
	//replace %s with server id
	public static final String DELETE_SERVER_URI = "/servers/%s";
	
	public static final String LIST_FLOATING_IPS = "/os-floating-ips";
	
	//replace %s with server id
	public static final String ADD_FLOATING_IP = "/servers/%s/action";
}
