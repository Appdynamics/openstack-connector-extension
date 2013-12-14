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
package com.appdynamics.connectors.openstack;

import static com.singularity.ee.controller.KAppServerConstants.CONTROLLER_SERVICES_HOST_NAME_PROPERTY_KEY;
import static com.singularity.ee.controller.KAppServerConstants.CONTROLLER_SERVICES_PORT_PROPERTY_KEY;
import static com.singularity.ee.controller.KAppServerConstants.DEFAULT_CONTROLLER_PORT_VALUE;

import java.net.InetAddress;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.singularity.ee.agent.resolver.AgentResolutionEncoder;
import com.singularity.ee.connectors.api.ConnectorException;
import com.singularity.ee.connectors.api.IConnector;
import com.singularity.ee.connectors.api.IControllerServices;
import com.singularity.ee.connectors.api.InvalidObjectException;
import com.singularity.ee.connectors.entity.api.IAccount;
import com.singularity.ee.connectors.entity.api.IComputeCenter;
import com.singularity.ee.connectors.entity.api.IImage;
import com.singularity.ee.connectors.entity.api.IImageStore;
import com.singularity.ee.connectors.entity.api.IMachine;
import com.singularity.ee.connectors.entity.api.IMachineDescriptor;
import com.singularity.ee.connectors.entity.api.IProperty;
import com.singularity.ee.connectors.entity.api.MachineState;
import com.appdynamics.connectors.openstack.enums.RebootType;
import com.appdynamics.connectors.openstack.enums.ServerStatus;
import com.singularity.ee.controller.KAppServerConstants;
import com.appdynamics.openstack.nova.CreateServerOptions;
import com.appdynamics.openstack.nova.FloatingIp;
import com.appdynamics.openstack.nova.OpenStackClient;
import com.appdynamics.openstack.nova.Server;

/**
 * 
 * @author Jeffrey
 * 
 *         Generic OpenStack Connector. As of now, there are currently two connector implementations; Rackspace and HP Cloud.
 * 
 *         Note: When the controller grabs the corresponding image repository entity for each compute center, it uses the
 *         implementation class name as the identifier. So different connectors need to use different class names, otherwise
 *         the image repository entities will get mixed and the connector breaks. Hence an "implementation" of the
 *         OpenStackConnector class is needed for each connector
 */
public abstract class OpenStackConnector implements IConnector
{
	private IControllerServices controllerServices;
	private static final Object counterLock = new Object();
	private static volatile long counter;
	private final Logger logger = Logger.getLogger(OpenStackConnector.class.getName());

	@Override
	public IMachine createMachine(IComputeCenter computeCenter, IImage image, IMachineDescriptor machineDescriptor)
			throws InvalidObjectException, ConnectorException
	{

		boolean succeeded = false;
		Exception createFailureRootCause = null;
		Server server = null;

		IProperty[] macProps = machineDescriptor.getProperties();
		OpenStackClient client = null;

		try
		{
			client = getOpenStackClient(computeCenter.getProperties(), controllerServices);

			String controllerHost = System.getProperty(CONTROLLER_SERVICES_HOST_NAME_PROPERTY_KEY, InetAddress
					.getLocalHost().getHostAddress());

			int controllerPort = Integer.getInteger(CONTROLLER_SERVICES_PORT_PROPERTY_KEY, DEFAULT_CONTROLLER_PORT_VALUE);

			IAccount account = computeCenter.getAccount();
			String accountName = account.getName();
			String accessKey = account.getAccessKey();

			AgentResolutionEncoder agentResolutionEncoder = null;

			try
			{
				agentResolutionEncoder = new AgentResolutionEncoder(controllerHost, controllerPort, accountName, accessKey);
			}
			catch (Exception e)
			{
				throw new ConnectorException("Failed to initiate AgentResolutionEncoder");
			}

			server = createServer(agentResolutionEncoder, image, macProps, client, computeCenter.getType().getName());

			IMachine machine = controllerServices.createMachineInstance(server.getId(),
					agentResolutionEncoder.getUniqueHostIdentifier(), computeCenter, machineDescriptor, image,
					getAgentPort());

			logger.info(computeCenter.getType().getName() + " instance created; machine id:" + machine.getId()
					+ "; server id:" + server.getId() + "; server name:" + server.getName());

			succeeded = true;
			return machine;
		}
		catch (Exception e)
		{
			createFailureRootCause = e;
			throw new ConnectorException(e.getMessage(), e);
		}
		finally
		{
			if (!succeeded && server != null)
			{
				try
				{
					String region = Utils.getRegion(macProps, controllerServices);
					client.deleteServer(server.getId(), region);
				}
				catch (Exception e)
				{
					throw new ConnectorException("Machine create failed, but terminate failed as well! "
							+ "We have an orphan " + computeCenter.getType().getName() + " instance with id: "
							+ server.getId() + " that must be shut down manually. Root cause for machine "
							+ "create failure is following: ", createFailureRootCause);
				}
			}
		}

	}

	/**
	 * Used to get the Openstack Client instance for each service provider. Also used to store the authentication urls for
	 * different service providers
	 */
	protected abstract OpenStackClient getOpenStackClient(IProperty[] props, IControllerServices controllerServices)
			throws Exception;

	/**
	 * Each service providers have different flavour reference/id numbering and naming system. The controller GUI properties
	 * displays the flavour names, this method translates them into the corresponding flavour ref/id.
	 */
	protected abstract String getFlavorRef(IProperty[] props, IControllerServices controllerServices) throws Exception;

	/**
	 * Assign a floating ip address to server instance
	 */
	protected boolean assignFloatingIp(OpenStackClient client, String floatingIp, String region, String serverId)
			throws Exception
	{
		if (floatingIp == null)
		{
			return true;
		}

		if (client.assignFloatingIP(serverId, floatingIp, region))
		{
			return true;
		}
		else
		{
			throw new Exception("Failed to assign floating Ip address:" + floatingIp + " to server instance: " + serverId);
		}

	}

	protected String verifyFloatingIp(OpenStackClient client, String floatingIp, String region, String serverId)
			throws Exception
	{
		if (floatingIp == null)
		{
			return null;
		}

		List<FloatingIp> ips = client.listFloatingIp(region);

		for (FloatingIp ip : ips)
		{
			if (ip.getIp().equals(floatingIp))
			{
				if (ip.getInstanceId().equalsIgnoreCase("None") || ip.getInstanceId().equalsIgnoreCase("null"))
				{
					return floatingIp;
				}
				else if (serverId != null)
				{
					if (ip.getInstanceId().trim().equals(serverId))
					{
						return null;
					}
				}
				else
				{
					throw new Exception("The floating ip entered is already assigned to a server instance");
				}
			}
		}

		throw new Exception("The floating ip entered does not exist");
	}

	protected Server createServer(AgentResolutionEncoder agentResolutionEncoder, IImage image, IProperty[] macProps,
			OpenStackClient client, String computeCenterType) throws Exception
	{
		String floatingIp = Utils.getFloatingIp(macProps, controllerServices);
		String region = Utils.getRegion(macProps, controllerServices);
		String flavorRef = getFlavorRef(macProps, controllerServices);
		String imageRef = Utils.getImageRef(image.getProperties(), controllerServices);
		String userData = agentResolutionEncoder.encodeAgentResolutionInfo();
		String filePath = KAppServerConstants.LINUX_FULL_PATH_TO_USER_DATA_FILE;

		verifyFloatingIp(client, floatingIp, region, null);

		if (image.getOsType().equals(IImage.WINDOWS_OS_TYPE))
		{
			filePath = KAppServerConstants.WINDOWS_FULL_PATH_TO_USER_DATA_FILE;
		}

		Server server = null;

		// 3 options supported on Rackspace
		String accessIPv4 = Utils.getAccessIPv4Address(macProps, controllerServices);
		String accessIPv6 = Utils.getAccessIPv6Address(macProps, controllerServices);
		String adminPass = Utils.getAdminPass(macProps, controllerServices);

		// 2 options supported on HP Cloud Compute
		String keyPair = Utils.getKeyPair(macProps, controllerServices);
		List<String> securityGroups = Utils.getSecurityGroup(macProps, controllerServices);

		CreateServerOptions options = new CreateServerOptions();
		options.setAccessIPv4(accessIPv4);
		options.setAccessIPv6(accessIPv6);
		options.setAdminPass(adminPass);
		options.setFile(userData.getBytes(), filePath);
		options.setKeyPair(keyPair);

		for (String s : securityGroups)
		{
			options.addSecurityGroup(s);
		}

		logger.info("Starting " + computeCenterType + " Instance from Image:" + imageRef + "; flavor:" + flavorRef
				+ "; region: " + region + " userData :" + userData);

		long count;

		synchronized (counterLock)
		{
			count = counter++;
		}

		server = client.createServer(region, flavorRef, imageRef, "AD_" + System.currentTimeMillis() + count, options);

		return server;
	}

	public String getIpAddress(IMachine machine, OpenStackClient client, IControllerServices controllerService)
			throws Exception
	{

		IProperty[] macProps = machine.getMachineDescriptor().getProperties();

		String region = Utils.getRegion(macProps, controllerService);

		List<String> ipAddress = client.getPublicIPv4Address(machine.getName(), region);

		if (ipAddress.size() != 0)
		{
			return ipAddress.iterator().next();
		}
		else
		{
			ipAddress = client.getPublicIPv6Address(machine.getName(), region);

			if (ipAddress.size() != 0)
			{
				return ipAddress.iterator().next();
			}
		}

		ipAddress = client.getPrivateIpAddress(machine.getName(), region);

		return ipAddress.iterator().next();
	}

	@Override
	public void refreshMachineState(IMachine machine) throws InvalidObjectException, ConnectorException
	{
		try
		{
			IProperty[] macProps = machine.getMachineDescriptor().getProperties();

			String region = Utils.getRegion(macProps, controllerServices);

			String serverId = machine.getName();
			MachineState currentState = machine.getState();

			if (currentState == MachineState.STARTING)
			{
				OpenStackClient client = getOpenStackClient(machine.getComputeCenter().getProperties(), controllerServices);

				ServerStatus serverStatus = client.getServerStatus(serverId, region);

				if (serverStatus == null)
				{
					machine.setState(MachineState.STOPPED);
				}
				else if (serverStatus == ServerStatus.SUSPENDED)
				{
					// machine is created. Power on the machine
					client.rebootServer(machine.getName(), region, RebootType.SOFT);
				}
				else if (serverStatus == ServerStatus.ACTIVE)
				{
					String floatingIp = Utils.getFloatingIp(macProps, controllerServices);
					String ipAddress = null;

					try
					{
						ipAddress = verifyFloatingIp(client, floatingIp, region, serverId);
						assignFloatingIp(client, floatingIp, region, serverId);

						if (ipAddress != null)
						{
							logger.info("Associated " + machine.getComputeCenter().getType().getName()
									+ " server instance with floating ip: " + ipAddress);
						}
					}
					catch (Exception e)
					{
						throw new ConnectorException("Error associating " + machine.getComputeCenter().getType().getName()
								+ " instance id: " + serverId + " with floating ip:" + floatingIp);
					}

					if (ipAddress == null)
					{
						ipAddress = getIpAddress(machine, client, controllerServices);
					}

					String currentIpAddress = machine.getIpAddress();

					if (ipAddress != null && !currentIpAddress.equals(ipAddress))
					{
						machine.setIpAddress(ipAddress);
					}

					machine.setState(MachineState.STARTED);
				}
			}
			else if (currentState == MachineState.STOPPING)
			{
				OpenStackClient client = getOpenStackClient(machine.getComputeCenter().getProperties(), controllerServices);

				ServerStatus serverStatus = null;

				try
				{
					serverStatus = client.getServerStatus(serverId, region);
				}
				catch (Exception e)
				{
					machine.setState(MachineState.STOPPED);
					logger.log(Level.FINE, "Exception occurred while checking machine "
							+ "state on STOPPING instance. Assume instance is STOPPED.", e);
				}

				if (serverStatus == null)
				{
					machine.setState(MachineState.STOPPED);
				}
				else if (serverStatus == ServerStatus.SUSPENDED)
				{
					machine.setState(MachineState.STOPPED);
				}
				else if (serverStatus == ServerStatus.UNKOWN)
				{
					machine.setState(MachineState.STOPPED);
				}
			}
		}
		catch (Exception e)
		{
			throw new ConnectorException("Unable to retrieve Server Status. " + e.getMessage(), e);
		}
	}

	@Override
	public void terminateMachine(IMachine machine) throws InvalidObjectException, ConnectorException
	{
		try
		{
			String region = Utils.getRegion(machine.getMachineDescriptor().getProperties(), controllerServices);

			OpenStackClient client = getOpenStackClient(machine.getComputeCenter().getProperties(), controllerServices);

			logger.info("terminating with " + machine.getName() + " " + region);
			client.deleteServer(machine.getName(), region);
		}
		catch (Exception e)
		{
			throw new ConnectorException("Error Terminating " + machine.getComputeCenter().getType().getName()
					+ " Instance Id:" + machine.getId() + " " + e.getMessage(), e);
		}
	}

	@Override
	public void restartMachine(IMachine machine) throws InvalidObjectException, ConnectorException
	{
		try
		{
			OpenStackClient client = getOpenStackClient(machine.getComputeCenter().getProperties(), controllerServices);

			String region = Utils.getRegion(machine.getMachineDescriptor().getProperties(), controllerServices);

			client.rebootServer(machine.getName(), region, RebootType.HARD);
		}
		catch (Exception e)
		{
			throw new ConnectorException("Error Rebooting " + machine.getComputeCenter().getType().getName()
					+ " Instance Id:" + machine.getName() + " " + e.getMessage(), e);
		}
	}

	@Override
	public void validate(IComputeCenter computeCenter) throws InvalidObjectException, ConnectorException
	{
		try
		{
			getOpenStackClient(computeCenter.getProperties(), controllerServices);

		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, "", e);

			throw new InvalidObjectException("Failed to validate the " + computeCenter.getType().getName()
					+ " connector properties" + " " + e.getMessage(), e);
		}
	}

	@Override
	public void validate(IImageStore imageStore) throws InvalidObjectException, ConnectorException
	{
		try
		{
			getOpenStackClient(imageStore.getProperties(), controllerServices);
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, "", e);

			throw new InvalidObjectException("Failed to validate the " + imageStore.getImageStoreType().getName()
					+ " store properties" + " " + e.getMessage(), e);
		}
	}

	@Override
	public void setControllerServices(IControllerServices controllerServices)
	{
		this.controllerServices = controllerServices;
	}

	@Override
	public int getAgentPort()
	{
		return controllerServices.getDefaultAgentPort();
	}

	@Override
	public void validate(IImage image) throws InvalidObjectException, ConnectorException
	{
		// do nothing
	}

	@Override
	public void unconfigure(IComputeCenter computeCenter) throws InvalidObjectException, ConnectorException
	{
		// do nothing
	}

	@Override
	public void unconfigure(IImageStore imageStore) throws InvalidObjectException, ConnectorException
	{
		// do nothing
	}

	@Override
	public void unconfigure(IImage image) throws InvalidObjectException, ConnectorException
	{
		// do nothing
	}

	@Override
	public void configure(IComputeCenter computeCenter) throws InvalidObjectException, ConnectorException
	{
		// do nothing
	}

	@Override
	public void configure(IImageStore imageStore) throws InvalidObjectException, ConnectorException
	{
		// do nothing
	}

	@Override
	public void configure(IImage image) throws InvalidObjectException, ConnectorException
	{
		// do nothing
	}

	@Override
	public void deleteImage(IImage image) throws InvalidObjectException, ConnectorException
	{
		// do nothing
	}

	@Override
	public void refreshImageState(IImage image) throws InvalidObjectException, ConnectorException
	{
		// do nothing
	}

}
