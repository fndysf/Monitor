package coriant.cats.zookeeper;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import coriant.cats.comm.CfgUtil;

public class ZKConfig {
	private static String zkServers = "172.29.22.182:2181";
	private static int TimeUnit = 2000;
	private static String mode="develop";
	static {
		String str=CfgUtil.getProperty("zookeeper.server");
		if(str!=null)
			zkServers=str.replace(';', ',').trim();
		str=CfgUtil.getProperty("timeunit");
		if(str!=null)
			TimeUnit=Integer.parseInt(str);
		str=CfgUtil.getProperty("cats.mode");
		if(str!=null)
			mode=str.trim();
	}

	public ZKConfig() {
	}


	public static String getZkServers() {
		return zkServers;
	}

	public static int getTimeUnit() {
		return TimeUnit;
	}

	public static String getMode(){
		return mode;
	}
	public static void main(String[] s) {
	}
}

