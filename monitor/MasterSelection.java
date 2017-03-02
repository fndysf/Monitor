package cats.zookeeper.monitor;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.log4j.Logger;

public class MasterSelection {
	private static boolean master = false;
	private static Logger logger=Monitor.getLogger();
	private static ZkClient client=ZKConnection.getConnection();
	static {
		if(client!=null){
		if (!client.exists("/monitor"))
			client.createPersistent("/monitor", true);
		register();
		client.subscribeDataChanges("/monitor/lock",
				new IZkDataListener() {
					@Override
					public void handleDataChange(String arg0, Object arg1)
							throws Exception {
						// TODO Auto-generated method stub
						register();
					}

					@Override
					public void handleDataDeleted(String arg0) throws Exception {
						// TODO Auto-generated method stub
						// System.out.println(arg0+" is deleted");
						register();
						logger.info(LocalInfo.getLocalIp()+ " is not working any more");
						logger.info("A new master will be selected");
					}
				});
		}
	}

	public static boolean isMaster() {
		return master;
	}

	public static void register() {
		// System.out.println("in register");
		if (client.exists("/monitor/lock"))
			return;
		boolean mark = true;
		try {
			client.createEphemeral("/monitor/lock");
		} catch (ZkNodeExistsException e) {
			mark = false;
			// e.printStackTrace();
		}
		master = mark;
		if (master) {
			//System.out.println("is master");
			ZKPid.renameToMaster();
			logger.info(LocalInfo.getLocalIp() + " is selected as master");
		}
	}
}
