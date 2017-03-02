package coriant.cats.zookeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.*;

public class ZKConnection {
	private static ZkClient client = null;

	public static ZkClient getConnection() {
		if (client!=null)
			return client;
		//boolean ok = true;
		ZKLookUp.getLogger().info("Trying to connect to ZooKeeper");
		while(client==null)
		{
			try {
				client = new ZkClient(ZKConfig.getZkServers(),
						ZKConfig.getTimeUnit() * 5, ZKConfig.getTimeUnit());
			} catch (Exception e) {
				client=null;
			}
		}
		ZKLookUp.getLogger().info("Connect to ZooKeeper successfully!");
		return client;
	}

	public static void close() {
		if (client!=null) {
			client.close();
			client=null;
		}
		return;
	}

	public static void main(String[] str) {
		// System.out.println("out");
	}
}

