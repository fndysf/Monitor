package cats.zookeeper.monitor;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class LocalInfo {
	private static String ip = null;
	private static String pid = null;

	public static String getLocalIp() {
		if (ip != null)
			return ip;
		return ip = getIP();
	}

	public static String getLocalPid() {
		if (pid != null)
			return pid;
		return pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	}

	private static String getIP() {
		Enumeration allNetInterfaces = null;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		InetAddress ip = null;
		while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
					.nextElement();
			// System.out.println(netInterface.getName());
			Enumeration addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				ip = (InetAddress) addresses.nextElement();
				if (ip != null && ip instanceof Inet4Address
						&& !ip.isLoopbackAddress()) {
					return ip.getHostAddress();
				}
			}
		}
		return null;
	}

	public static void main(String[] str) {
		System.out.println(getLocalIp() + ":" + getLocalPid());
	}
}
