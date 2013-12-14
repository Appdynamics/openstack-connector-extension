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
