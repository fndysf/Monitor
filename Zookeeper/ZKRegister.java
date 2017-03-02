package coriant.cats.zookeeper;


import org.I0Itec.zkclient.*;
import org.apache.log4j.*;

import coriant.cats.comm.CfgUtil;

import java.net.*;
import java.util.Enumeration;

public class ZKRegister {
	private static ZkClient zkClient=new ZkClient(CfgUtil.getProperty("zookeeper.server"),3000);
	private static ZKType[] types={ZKType.CM,ZKType.STL,ZKType.TP};
    public static void init(ZKType type){
    	for(int i=0;i<types.length;i++)
    	{
    		String str="/cats/"+ZKType.typeToString(types[i]);
    		 if(!zkClient.exists(str))
    	            zkClient.createPersistent(str,true);
    	}
        String address=null,base="/cats/"+ZKType.typeToString(type),port=System.getenv("Tomcat_Port");
        Logger logger= Logger.getLogger(ZKRegister.class);
        if(port==null)
        	port="80";
        address=getLocalIP()+":"+port;
        logger.info(ZKType.typeToString(type)+" : get LocalHost address = "+address+"; it will be registered in zookeeper!");
        //address="172.29.55.196:9010";
        //logger.info(base+"/"+address+"");
        try{
        	zkClient.createEphemeral(base+"/"+address);
        }catch(Exception e)
        {
        	//e.printStackTrace();
        	logger.info(base+"/"+address+"already exists!");
        }
    }
    public static String getLocalIP() {
        Enumeration allNetInterfaces=null;
        try {
            allNetInterfaces= NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
           // System.out.println(netInterface.getName());
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address && !ip.isLoopbackAddress())
                {
                    return ip.getHostAddress();
                }
            }
        }
        return null;
    }
    public static void main(String[] s){
        //init();
        //printAllIPs();
        System.out.println(getLocalIP());
        while(true) {

        }
    }
}
