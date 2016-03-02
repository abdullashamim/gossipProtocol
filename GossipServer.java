package server;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;
import java.rmi.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GossipServer{  
	static int mypid;
	static boolean debug =false;

public static void main(String args[]){  
	
	String bindStr;
	int noprocesses=Integer.parseInt(args[1]);
	if(debug)
	System.out.println("no of processes "+noprocesses);
	int pWaitTime=10000;//wait for 5 min actual
	int waitTime=10000; // wait time in milli seconds
	String inputFile="";
	int argslen=args.length;
	int rpid=Integer.parseInt(args[0]);
	//System.out.println("rpid is "+rpid);
	mypid=rpid;
	boolean isActive = false;
	
	HashMap<Integer,Integer> processClocks = new HashMap<Integer,Integer>();
    for(int i=0;i<noprocesses;i++){
    	processClocks.put(i+1, 0);
    }
   
	if(argslen>=3){
		isActive=true;
	}

try{  
	if (argslen==0) 
		bindStr="Gossip1"; 
	else {
	    bindStr="Gossip"+rpid;
	    noprocesses=Integer.parseInt(args[1]);
	    if(argslen>=3)
	    	inputFile="/home/pradeep/workspace/ds-asg2/bin/"+args[3];
	     
	}
	GossipInterface stub=new GossipRemote();  
	if(debug)
	System.out.println("bind str is "+bindStr);
	Naming.rebind("rmi://localhost:1099/"+bindStr, stub); 
	//System.out.println("after bind");
	
	// wait sleep for specified time
	Thread.sleep(pWaitTime);
	// read msg from input.txt and invoke processGossip for each msg/line
	GossipServer gs=new GossipServer();
	gs.serializeMap(processClocks,mypid);
	
	if(isActive){
		if(debug)
			System.out.println(mypid+" is an active process");
			try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
			    String line;			    
			    while ((line = br.readLine()) != null) {
			    	String[] id_clock=line.split("\\.");
			    	 
			    	//System.out.println("parsing "+id_clock[0]);
			    //	System.out.println("parsing1 "+id_clock[1]);
			    	int epid=Integer.parseInt(id_clock[0]);
			    	int clockCounter=Integer.parseInt(id_clock[1]);
			    	gs.processGossip(mypid,epid,clockCounter,noprocesses,true);
			    	Thread.sleep(waitTime);
			    }
			}
	} 
				
	}catch(Exception e){System.out.println(e);}  
	}  

//code for processGossip goes here
	void processGossip(int rpid,int epid,int clockCounter,int noprocesses,boolean isActive){
		
	
		Random rn = new Random();
		int peer1 ;
		while(true){
			peer1=rn.nextInt(noprocesses) + 1;
			if(peer1==mypid) continue;
			else break;
		}
		
		int peer2;
		while(true){ 
			peer2=rn.nextInt(noprocesses) + 1;
			if(peer2==mypid||peer2==peer1) continue;
			else break;
		}
		if(debug)
		System.out.println("Peer1 and Peer2 are calculated as "+peer1+","+peer2+"- by "+rpid);
		
		String bindstr1,bindstr2;
		bindstr1="Gossip"+peer1;
		bindstr2="Gossip"+peer2;

		HashMap<Integer,Integer> hm=deserializeMap(mypid);
		if(isActive){
		//	System.out.println("The existing clock in hashmap for "+rpid+" is "+hm.get(rpid));
		  hm.put(rpid,hm.get(rpid)+1);
		  serializeMap(hm,rpid);
		}
		//invoke hearGossip() on peer1 and peer2
		try {
			//Here this process is generating events , hence it's clock has to be updated
			 // hm.put(rpid,hm.get(rpid)+1);
			//  System.out.println("before binding ");
			GossipInterface stub1=(GossipInterface)Naming.lookup("rmi://localhost/"+bindstr1);
			// System.out.println("passign rpid..epid..clock"+rpid+".."+epid+".."+clockCounter);
			stub1.hearGossip(rpid,epid,noprocesses,clockCounter);
			
			//hm.put(rpid,hm.get(rpid)+1);
			GossipInterface stub2=(GossipInterface)Naming.lookup("rmi://localhost/"+bindstr2);
			stub2.hearGossip(rpid,epid,noprocesses,clockCounter);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

	}
	public void serializeMap(HashMap<Integer,Integer> hm,int rpid){
		if(debug){
			System.out.println("updating "+rpid+".txt with :");
			for(Map.Entry<Integer,Integer> m : hm.entrySet()){
				System.out.println(""+m.getKey()+">>"+m.getValue());
			}
		}
		String inputFileName="/home/pradeep/workspace/ds-asg2/bin/"+rpid+".txt";
	//	FileChannel outchan= new FileOutputStream()
		File inputFile=new File(inputFileName);
		try {
			FileOutputStream fs = new FileOutputStream(inputFile);
			ObjectOutputStream os= new ObjectOutputStream(fs);
			os.writeObject(hm);
			os.flush();
			os.close();
			fs.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}  