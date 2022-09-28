package centralsite;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

import datasite.Interface_DataRemote;
import publicClasses.ClassOperations;
import publicClasses.ClassTransaction;
import publicClasses.ClassTransactionsToCheck;

public class CentralManager implements Interface_CentralRemote {
	
	public int sitenumber = 0;
	public int port_number;
	public int number_of_deadlocks;
	
	public CentralManager() 
	{
		this.port_number = 45;
		long graph_time = 10000;
		number_of_deadlocks = 0;
		lm = new LockManager();
		try {
			Registry registry = LocateRegistry.createRegistry(45);
			Interface_CentralRemote cs = (Interface_CentralRemote) UnicastRemoteObject.exportObject(this, 0);
			registry.bind("c2pl", cs);
			registry.bind("c2pl_site2", cs);
			registry.bind("c2pl_site3", cs);
			registry.bind("c2pl_site4", cs);
			new Timer().schedule(new TimerTask() {
				@Override
	            public void run() {
					check_for_deadlocks();}}, graph_time, graph_time);
		}
		catch(RemoteException e) {
			System.out.println( " Remote Exception: " + e.getMessage());
		}
		catch(AlreadyBoundException e) {
			System.out.println( " Already Bound Exception: " + e.getMessage());
		}
	}
	
	public LockManager lm;
	public synchronized void check_for_deadlocks() {
		ClassTransactionsToCheck<Integer, ArrayList<Integer>> pair = lm.check_for_deadlocks();
		if(pair != null) {
			number_of_deadlocks += 1;
			int siteId = pair.first.intValue()/10000;
			System.out.println( "Deadlock occuring transactions are obtained and the transaction to be aborted: " + pair.first.intValue() + " from site " + siteId);
			try {
				Interface_DataRemote ds = getDataSiteStub(siteId);
				if(ds != null) { 
					ds.abort();
					System.out.println( "Data site " + siteId + " has successfully aborted the transaction");
				}	
			}
			catch(Exception e) {System.out.println( " Exception in checking deadlocks: " + e.getMessage()); }		
			for(Integer site: pair.second) {
				try {
					Interface_DataRemote dataSiteStub = getDataSiteStub(site);
					if(dataSiteStub != null) {
						dataSiteStub.unblock_site();
						System.out.println( "Data Site " + site + " unblocked!");
					}		
				}
				
				catch(Exception e) { System.out.println( " Exception in checkDeadlocks: " + e.getMessage()); }
				} }
		System.out.println( "Checked for Deadlocks in the waitforgraph and the number of Deadlocks found so far: " + number_of_deadlocks);
	}
	
	private Interface_DataRemote getDataSiteStub(int id) {
		String db = null;
		try {
			if (id == 1) {db = "c2pl";}
			if (id == 2) {db = "c2pl_site2";}
			if (id == 3) {db = "c2pl_site3";}
			if (id == 4) {db = "c2pl_site4";}

			Registry registry = LocateRegistry.getRegistry(port_number);
			Interface_DataRemote ds = (Interface_DataRemote) registry.lookup(db + id);	
			return ds;
		}
		catch(RemoteException e) {
			System.out.println( " Remote Exception: " + e.getMessage());
		}
		catch(NotBoundException e) {
			System.out.println( " Not Bound Exception: " + e.getMessage());
		}
		return null;
	}

	public synchronized int get_next_Site() throws RemoteException {
		sitenumber += 1;
		return sitenumber;
	}

	public synchronized void lock_release(ClassTransaction transaction) throws RemoteException {
		try {
			List<Integer> unblockedSites = lm.releases_all_locks_held(transaction);
			for(int i = 0; i < unblockedSites.size(); i += 1) {
				Interface_DataRemote ds = getDataSiteStub(unblockedSites.get(i).intValue());
				ds.write_to_dataSite();
				ds.unblock_site();
				System.out.println( "Data site " + unblockedSites.get(i).intValue() + " unblocked");
			}
			lm.print_lockTable_QueueTable();
		}
		catch(Exception e) {
			System.out.println( " Exception in releaseLock: " + e.getMessage());
		}
	}

	public synchronized boolean requests_for_locks(ClassOperations operation) throws RemoteException {
		
		try {
			boolean lock_access = lm.requests_for_lock(operation);
			if(lock_access == true) {System.out.println( "Transaction " + operation.tid + " requests for a lock type "+ operation.ltype + " on data item " + operation.Dataitem +" and the lock request is granted");}
			else {System.out.println( "Transaction" + operation.tid + " requests for a lock type "+ operation.ltype + " on data item " + operation.Dataitem +" and the lock request is rejected");}
			lm.print_lockTable_QueueTable();
			return lock_access;			
		}
		catch(Exception e) { System.out.println( "Raised Exception while requesting Lock: " + e.getMessage()); }
		return false;
	}
	
	public static void main (String[] args) 
	{	
		new CentralManager();
		System.out.println( "Initiated Central Site at port " + 45);
	}
}
