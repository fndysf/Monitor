package cats.zookeeper.monitor;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Termination implements SignalHandler {
	private ExecutorService pool = null;

	public Termination(ExecutorService p) {
		pool = p;
	}

	@Override
	public void handle(Signal signal) {
		// TODO Auto-generated method stub
		// System.out.println(sn.getName() + " is recevied.");
		Monitor.getLogger().info(
				signal.getName() + " is recevied ; monitor is shutting down");
		ZKConnection.interrupt();
		pool.shutdownNow();
		ZKPid.delete();
		ZKConnection.close();
	}

}
