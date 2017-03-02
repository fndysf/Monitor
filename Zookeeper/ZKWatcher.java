package coriant.cats.zookeeper;

import org.I0Itec.zkclient.IZkChildListener;
import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coriant.cats.inf.*;
import coriant.cats.tem.StlMgr;

/**
 * Created by dom on 16-9-13.
 */
public class ZKWatcher implements IZkChildListener {
	private static Logger logger = ZKLookUp.getLogger();
	// private Map<String,List<String>> map;
	// private Map<String,Integer>count;
	private int cmIndex = 0;
	private int tpIndex = 0;
	private int stlIndex = 0;
	private List<CmInf> cmPool;
	private List<StlMgrInf> stlPool;
	private List<TpInf> tpPool;
	private List<String> cmUrl=null;
	private static String cmString = "cmserver";
	private static String tpString = "tpserver";
	private static String stlString = "stlserver";
	private static String cmSuffix = "/cats/hessian/CmService";
	private static String stlSuffix = "/stlmgr/hessian/StlService";
	private static String tpSuffix = "/tp/hessian/TpService";
	private static String cmCfgFileSuffix="/cats/hessian/CfgFileService";
	private static HessianProxyFactory hpFac = new HessianProxyFactory();
	private static String prefix = "http://";
	private static Map<String, List<String>> map;
	public ZKWatcher(Map<String, List<String>> m) {
		// this.map=map;
		//logger.info("in zkwatcher constructor");
		map=new HashMap<String,List<String>>(m);
		cmPool = new ArrayList<CmInf>();
		stlPool = new ArrayList<StlMgrInf>();
		tpPool = new ArrayList<TpInf>();
		// count=new HashMap<String,Integer>();
		for (String str : map.keySet()) {
			// count.put(str,0);
			createServiceList(str, map.get(str));
		}
		 print(map);
	}
	
	void print(Map<String, List<String>> map)
	{
		for(String str:map.keySet())
		{
			logger.info(str);
			List<String> list=map.get(str);
			for(int i=0;i<list.size();i++)
				logger.info(list.get(i));
		}
	}
	
	synchronized void  createServiceList(String str, List<String> list) {
		//logger.info("in createservicelist ");
		if (str.equals(cmString)) {
			//logger.info("cmserver : "+str+" list.size = "+list.size()+"  "+list.get(0));
			cmPool.clear();
			for (int i = 0; i < list.size(); i++)
				cmPool.add(createCmService(list.get(i)));
		} else if (str.equals(tpString)) {
			tpPool.clear();
			for (int i = 0; i < list.size(); i++)
				tpPool.add(createTpService(list.get(i)));
		} else if (str.equals(stlString)) {
			stlPool.clear();
			for (int i = 0; i < list.size(); i++)
				stlPool.add(createStlService(list.get(i)));
		}
	}

	synchronized private CmInf createCmService(String ip) {
		CmInf cmService = null;
		try {
			cmService = (CmInf) hpFac.create(CmInf.class,
					prefix + ip+ cmSuffix);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmService;
	}

	synchronized private TpInf createTpService(String ip) {
		TpInf tpService = null;
		try {
			tpService = (TpInf) hpFac.create(TpInf.class,
					prefix + ip + tpSuffix);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tpService;
	}

	synchronized private StlMgrInf createStlService(String ip) {
		StlMgrInf stlService = null;
		try {
			stlService = (StlMgrInf) hpFac.create(StlMgrInf.class, prefix
					+ ip + stlSuffix);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stlService;
	}

	@Override
	synchronized public void handleChildChange(String s, List<String> list) throws Exception {
		// System.out.println(s+": "+list);
		String str=s.split("/")[2];
		map.put(str,list);
		createServiceList(str, list);
		logger.info("Children of " + s + " changed to " + list);
		//print();
	}
	
	synchronized public String getCmUrl(){
		cmUrl=map.get("cmserver");
		if(cmUrl==null||cmUrl.size()==0)
			return null;
		cmIndex%=cmUrl.size();
		return prefix+cmUrl.get(cmIndex++)+cmCfgFileSuffix;
	}
	
	synchronized public CmInf getCmService() {
		logger.info("into ZKWatchdr.getCmService()");
		if (cmPool.size() == 0)
			return null;
		cmIndex %= cmPool.size();
		logger.info("out of ZKWatchdr.getCmService()");
		return cmPool.get(cmIndex++);
	}

	synchronized public StlMgrInf getStlService() {
		if (stlPool.size() == 0)
			return null;
		stlIndex %= stlPool.size();
		return stlPool.get(stlIndex++);
	}

	synchronized public TpInf getTpService() {
		if (tpPool.size() == 0)
			return null;
		tpIndex %= tpPool.size();
		return tpPool.get(tpIndex++);
	}
}
