package coriant.cats.zookeeper;

import org.I0Itec.zkclient.ZkClient;


import org.apache.log4j.Logger;

import coriant.cats.comm.CfgUtil;
import coriant.cats.inf.CmInf;
import coriant.cats.inf.StlMgrInf;
import coriant.cats.inf.TpInf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZKLookUp {
	private static Logger logger = Logger.getLogger(ZKLookUp.class);
	private static Map<String, List<String>> map = new HashMap<String, List<String>>();
	private static ZKWatcher zkWatcher;
	private static ZkClient zkClient;
	private static String[] types = { "cmserver", "stlserver", "tpserver" };
	private static String base="/"+ZKConfig.getMode();
	static {
		zkClient = ZKConnection.getConnection();
		for (int i = 0; i < types.length; i++) {
			String str = base +"/"+ types[i];
			if (!zkClient.exists(str))
				zkClient.createPersistent(str, true);
		}
		List<String> list = zkClient.getChildren(base);
		for (String str : list) {
			List<String> l = zkClient.getChildren(base+"/" + str);
			map.put(str, l);
		}
		zkWatcher = new ZKWatcher(map);
		for (String str : list) {
			zkClient.subscribeChildChanges(base+"/" + str, zkWatcher);
		}
		logger.info("out of ZKLookUp static");
	}
	
	public static Logger getLogger(){
		return logger;
	}
	
	public static void main(String[] str){
		while(true){
			//System.out.println(zkWatcher.getCmUrl());
			System.out.println("-------------------------");
			System.out.println("cm Service : "+zkWatcher.getCmService());
			System.out.println("stl Service : "+zkWatcher.getStlService());
			System.out.println("tp Service : "+zkWatcher.getTpService());
			System.out.println("-------------------------");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static String getCmUrl(){
		logger.info("Trying to get cm url. If succeed, soon you will see it later in the log. If not, it will wait here until CmServer is available.");
		String cmUrl=zkWatcher.getCmUrl();
		while(cmUrl==null){
			cmUrl=zkWatcher.getCmUrl();
		}
		logger.info("Successflly get cm url: "+cmUrl);
		return cmUrl;
	}
	
	public static CmInf getCmService() {
		// List<String>list=zkClient.getChildren("/cats/cmserver");
		// return list.get(0);
		logger.info("into ZKLookUp getCmServicde()");
		CmInf cmService=zkWatcher.getCmService();
		logger.info("Trying to get cm service. If succeed, soon you will see it later in the log. If not, it will wait here until CmServer is available.");
		while(cmService==null){
			cmService=zkWatcher.getCmService();
		}
		logger.info("Successflly get cm service: "+cmService);
		return cmService;
	}

	public static StlMgrInf getStlService() {
		StlMgrInf stlService=zkWatcher.getStlService();
		logger.info("Trying to get stl service. If succeed, soon you will see it later in the log. If not, it will wait here until StlMgrServer is available.");
		while(stlService==null){
			stlService=zkWatcher.getStlService();
		}
		logger.info("Successflly get stl service: "+stlService);
		return stlService;
	}

	public static TpInf getTpService() {
		TpInf tpService=zkWatcher.getTpService();
		logger.info("Trying to get tp service. If succeed, soon you will see it later in the log. If not, it will wait here until TpServer is available.");
		while(tpService==null){
			tpService=zkWatcher.getTpService();
		}
		logger.info("Successflly get tp service: "+tpService);
		return tpService;
	}
}
