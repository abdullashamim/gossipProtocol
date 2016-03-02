package server;
import java.rmi.*;
import java.util.HashMap;  

public interface GossipInterface extends Remote {
	public void hearGossip(int rpid,int pid,int noprocesses,int clock) throws RemoteException; 
	//HashMap<Integer,Integer> hm
}
