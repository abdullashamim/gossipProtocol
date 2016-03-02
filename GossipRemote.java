package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
public class GossipRemote  extends UnicastRemoteObject implements  GossipInterface{
	
	GossipRemote ()throws RemoteException{  
		//processClocks.put(key, value)
		super();  
	}  
	public void hearGossip(int rpid,int epid,int noprocesses,int clock){
	
		if(GossipServer.debug){
		System.out.println("hear gossip invoked ");
		System.out.println("rpid :"+rpid+"----"+"epid :"+epid+"----"+"clock :"+clock+"---mypid"+GossipServer.mypid);
		}
		HashMap<Integer,Integer> hm=deserializeMap(GossipServer.mypid);
		Integer existingClock=hm.get(epid);
		
		if(GossipServer.debug){
			System.out.println("The values in the map heargossip");
			for(Map.Entry<Integer,Integer> m : hm.entrySet()){
				System.out.println(""+m.getKey()+">>"+m.getValue());
			}
		}
	   if(GossipServer.debug){
	 	System.out.println("received clock is "+clock);
	 	System.out.println("exising clock is "+existingClock);
	    }
		
		if(clock>existingClock){
			System.out.println("Accept "+epid+":"+clock);
			//received clock is greater than existing
			//hence update it
			//System.out.println("Updating the clock of "+epid+" to "+clock);
			hm.put(epid, clock);
			//System.out.println("clock is now updated to "+hm.get(epid));
			GossipServer gs= new GossipServer();
			gs.serializeMap(hm, GossipServer.mypid);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gs.processGossip(GossipServer.mypid, epid, clock,noprocesses ,false);
		}else{
			System.out.println("Reject "+epid+":"+clock);
		}
	}
	
	HashMap<Integer,Integer> deserializeMap(int epid){
		HashMap<Integer,Integer> hm=null;
		String inputFileName="/home/pradeep/workspace/ds-asg2/bin/"+epid+".txt";
		File toRead= new File(inputFileName);
		try {
			FileInputStream fs=new FileInputStream(toRead);
			ObjectInputStream ois=new ObjectInputStream(fs);
			hm= (HashMap<Integer,Integer>)ois.readObject();
			ois.close();
			fs.close();
           
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hm;
		
	}
} // 
