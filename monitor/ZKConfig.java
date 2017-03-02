package cats.zookeeper.monitor;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ZKConfig {
	public static final String base = System.getenv("MONITOR_HOME");
	private static String zkServers = "172.29.22.182:2181,172.29.22.185:2181,172.29.55.196:2181";
	private static HashMap<String, String> devMap = new HashMap<String, String>();
	private static HashMap<String, String> relMap = new HashMap<String, String>();
	private static BufferedReader in;
	//private static String debugFile = base + "/conf/debug.cfg",developConfig= base + "/conf/release.cfg";
	private static String[] item;
	private static int TimeUnit = 3000;
	private static File devCfgFile = new File(base + "/conf/develop.cfg"),relCfgFile=new File(base + "/conf/release.cfg");
	//private static long cfgFileSize=0;
	private static String[] checkList={"cmserver","tpserver","stlserver"};
	static {
		config();
	}

	public ZKConfig() {
	}
	
	private static void readCfg(File file,Map<String,String> map){
		map.clear();
		String line;
		int idx;
		try {
			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("#"))
					continue;
				idx=line.indexOf('#');
				if(idx>=0)
					line=line.substring(0, idx);
				item = line.split("=");
				if (item.length == 2)
					map.put(item[0].trim(), item[1].trim());
			}
		} catch (FileNotFoundException e) {
			Monitor.getLogger().error(e.getMessage());
		} catch (IOException e) {
			Monitor.getLogger().error(e.getMessage());
		}
		if (map.containsKey("zkserver")) {
			zkServers = map.get("zkserver");
			map.remove("zkserver");
		}
		if (map.containsKey("timeunit")) {
			TimeUnit = new Integer(map.get("timeunit"));
			map.remove("timeunit");
		}
		for(int i=0;i<checkList.length;i++){
			if(!map.containsKey(checkList[i]))
				map.put(checkList[i], "");
		}
	}
	public static void config() {
			readCfg(devCfgFile,devMap);
			readCfg(relCfgFile,relMap);
			//cfgFileSize=cfgFile.length();
	}

	public static void checkConfig() {
		//if (cfgFile.length()!=cfgFileSize||cfgFile.lastModified() + TimeUnit * 2 >= System.currentTimeMillis()) {
			config();
		//}
	}

	public static String getZkServers() {
		return zkServers;
	}

	public static int getTimeUnit() {
		return TimeUnit;
	}

	public static HashMap<String, String> getDevelopMap() {
			return devMap;
	}
	
	public static HashMap<String, String> getRealeseMap() {
		return relMap;
		
	}

	public static void main(String[] s) {
	}
}
