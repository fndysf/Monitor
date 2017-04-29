package cats.zookeeper.monitor;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Monitor {
	private static Socket socket;
	private static Map<String, String> map = null;
	private static Set<String> set = new HashSet<String>();
	private static List<String> list;
	private final static String logCfg = ZKConfig.base
			+ "/conf/log4j.properties";
	private static ExecutorService pool = Executors.newCachedThreadPool();
	private static Logger log = null;
	private static String[] checkList={"cmserver","tpserver","stlserver"};
	static {
		System.setProperty("log4j.configuration", "file:" + logCfg);
		log = LogManager.getLogger(Monitor.class);
	}

	public Monitor() {
	}

	public static void main(String[] s) {
		Signal.handle(new Signal("TERM"), new Termination(pool));
		ZKPid.create(LocalInfo.getLocalPid());
		pool.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					ZKConfig.config();
					if(MasterSelection.isMaster())
						update();
					try {
						Thread.sleep(ZKConfig.getTimeUnit()*3);
					} catch (InterruptedException e) {
						// e.printStackTrace();
						break;
					}
				}
			}
		});
	}
	
	private static void update(){
		update(ZKConfig.getDevelopMap(),"/develop");
		update(ZKConfig.getRealeseMap(),"/release");
	}

	private static void update(Map<String,String> map,String root) {
		String str,base;
		String[] ips;
		try{
		for (String key : checkList) {
			base = root +"/"+ key;
			str = map.get(key);
			ips = str.split(",");
			set.clear();
			for (String ip : ips) {
				ip = ip.trim();
				if (isActive(ip))
					set.add(ip);
			}
			list = ZKRegister.getChildren(base);
			for (String ip : list) {
				if (!set.contains(ip)) {
					ZKRegister.delete(base + "/" + ip);
					log.info(base
							+ "/"
							+ ip
							+ " is deleted from zookeeper, because it is not in the configuration file or not active.");
				}
				set.remove(ip);
			}
			for (String ip : set) {
				if(!ip.equals("")){
				log.info(base + "/" + ip + " is added to zookeeper.");
				ZKRegister.create(base + "/" + ip);
				}
			}
		}
		}catch(ZkInterruptedException e){
			
		}
	}

	private static boolean isActive(String addr) {
		String[] str = addr.split(":");
		addr = str[0];
		int port = 80;
		if (str.length == 2)
			port = Integer.parseInt(str[1]);
		try {
			socket = new Socket(addr, port);
			
		} catch (IOException e) {
			return false;
		}finally{
			socket.close();
		}
		return true;
	}

	public static Logger getLogger() {
		return log;
	}

}
