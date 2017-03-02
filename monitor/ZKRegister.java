package cats.zookeeper.monitor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.*;

public class ZKRegister {
	private static ZkClient client=ZKConnection.getConnection();
	public ZKRegister() {
	}

	public static List<String> getChildren(String path) {
		if (!client.exists(path))
			client.createPersistent(path, true);
		return client.getChildren(path);
	}

	public static void create(String path) {
		client.createPersistent(path, true);
	}

	public static void delete(String path) {
		client.deleteRecursive(path);
	}
}
