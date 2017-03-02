package cats.zookeeper.monitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ZKPid {
	
	private static String masterString = ZKConfig.base + "/tmp/master.pid";
	private static String slaveString = ZKConfig.base + "/tmp/slave.pid";
	private static File master = new File(masterString),slave=new File(slaveString),file=slave;

	public static void create(String pid){
		file=slave;
		try {
			FileWriter fw = new FileWriter(file, false);
			fw.write(pid);
			fw.close();
		} catch (IOException e) {
			Monitor.getLogger().error(e.getMessage());
		}
		file.setWritable(false);
	}
	public static void renameToMaster() {
		if (file == slave)
		file.renameTo(master);
	}

	public static void delete() {
		if (master.exists())
			master.delete();
		if(slave.exists())
			slave.delete();
	}
}
