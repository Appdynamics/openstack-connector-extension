OpenStack Connector Extension
=============================

##Use Case

Elastically grow/shrink instances into cloud/virtualized environments. There are four use cases for the connector. 

First, if the Controller detects that the load on the machine instances hosting an application is too high, the openstack-connector-extension may be used to automate creation of new virtual machines to host that application. The end goal is to reduce the load across the application by horizontally scaling up application machine instances.

Second, if the Controller detects that the load on the machine instances hosting an application is below some minimum threshold, the openstack-connector-extension may be used to terminate virtual machines running that application. The end goal is to save power/usage costs without sacrificing application performance by horizontally scaling down application machine instances.

Third, if the Controller detects that a machine instance has terminated unexpectedly when the connector refreshes an application machine state, the openstack-connector-extension may be used to create a replacement virtual machine to replace the terminated application machine instance. This is known as our failover feature.

Lastly, the openstack-connector-extension may be used to stage migration of an application from a physical to virtual infrastructure. Or the openstack-connector-extension may be used to add additional virtual capacity to an application to augment a preexisting physical infrastructure hosting the application.  

The OpenStack connector is actually a meta-connector and supports the following OpenStack cloud implementations:

1. OpenStack Compute (Nova)
2. Rackspace OpenStack Public Cloud
3. Rackspace OpenStack Private Cloud
4. HP Cloud 

##Directory Structure

<table><tbody>
<tr>
<th align="left"> File/Folder </th>
<th align="left"> Description </th>
</tr>
<tr>
<td class='confluenceTd'> lib </td>
<td class='confluenceTd'> Contains third-party project references </td>
</tr>
<tr>
<td class='confluenceTd'> src </td>
<td class='confluenceTd'> Contains source code to the openstack connector extension </td>
</tr>
<tr>
<td class='confluenceTd'> dist </td>
<td class='confluenceTd'> Only obtained when using ant. Run 'ant build' to get binaries. Run 'ant package' to get the distributable .zip file </td>
</tr>
<tr>
<td class='confluenceTd'> build.xml </td>
<td class='confluenceTd'> Ant build script to package the project (required only if changing Java code) </td>
</tr>
</tbody>
</table>

##Installation

1. Clone the openstack-connector-extension from GitHub
2. Run 'ant package' from the cloned openstack-connector-extension directory
3. Download the file openstack-connector.zip located in the 'dist' directory into \<controller install dir\>/lib/connectors
4. Unzip the downloaded file
5. Restart the Controller
6. Go to the controller dashboard on the browser. Under Setup->My Preferences->Advanced Features enable "Show Cloud Auto-Scaling features" if it is not enabled. 
7. On the controller dashboard click "Cloud Auto-Scaling" and configure the compute cloud and the image.

Click Compute Cloud->Register Compute Cloud. Refer to the images below

![alt tag](https://raw.github.com/Appdynamics/openstack-connector-extension/master/OpenStackCompute.png)
![alt tag](https://raw.github.com/Appdynamics/openstack-connector-extension/master/RackspacePublicCloud.png)
![alt tag](https://raw.github.com/Appdynamics/openstack-connector-extension/master/RackspacePrivateCloud.png)
![alt tag](https://raw.github.com/Appdynamics/openstack-connector-extension/master/HPCloud.png)

Click Image->Register Image. Refer to the images below

![alt tag](https://raw.github.com/Appdynamics/openstack-connector-extension/master/OpenStackComputeImage.png)
![alt tag](https://raw.github.com/Appdynamics/openstack-connector-extension/master/RackspaceOpenStackImage.png)
![alt tag](https://raw.github.com/Appdynamics/openstack-connector-extension/master/HPCloudImage.png)

##Contributing

Always feel free to fork and contribute any changes directly here on GitHub.

##Community

Find out more in the [AppSphere](http://appsphere.appdynamics.com/t5/eXchange/OpenStack-Cloud-Connector-Extension/idi-p/5505) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).

