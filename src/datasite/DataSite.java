package datasite;
import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;

import centralsite.Interface_CentralRemote;
import publicClasses.ClassOperations;
import publicClasses.ClassTransaction;
import dataSite2.DataManager2;
import dataSite3.DataManager3;
import dataSite4.DataManager4;

public class DataSite implements Runnable, Interface_DataRemote {
	public TransactionManager tm;
	public int id;
	
	public static void main(String[] args) {
		DataManager.create();
		
		
		(new DataSite(args[0])).run();	
	}
	
	public Interface_CentralRemote csi;
	public DataSite(String transactionsFile) {
		blocked = false;
		abort = false;
		try {
			String line;
			Registry registry = LocateRegistry.getRegistry("localhost", 45);
			csi = (Interface_CentralRemote) registry.lookup("c2pl");
			id = csi.get_next_Site();
			tm = new TransactionManager(id);
			//load the transaction file
			ArrayList<String> fileData = new ArrayList<String>();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(transactionsFile));		
				while((line = reader.readLine()) != null && !line.trim().isEmpty()) { fileData.add(line);	}
				reader.close();
			}
			catch(IOException e) { System.out.println("IO Exception: " + e.getMessage()); }
			tm.transaction_history = tm.generateTransactions(fileData);			
			registry.bind("c2pl" + id,  (Interface_DataRemote) UnicastRemoteObject.exportObject(this, 0));
		}
		catch(RemoteException e) { System.out.println(" Remote Exception: " + e.getMessage()); }
		catch(Exception e) { System.out.println( " Exception: " + e.getMessage()); }	
	}
	
	public boolean blocked,abort;
	@Override
	public void abort() throws RemoteException { abort = true; unblock_site(); }

	public synchronized void blocked() {
		try {
			blocked = true;
			while(blocked) { Thread.sleep(100); }
		}
		catch(Exception e) { System.out.println( " Exception: " + e.getMessage());}
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				synchronized(this) {
					ClassTransaction transaction;
					if(! (tm.transaction_history.size() > 0)) {transaction = null;  }
					else { transaction = tm.transaction_history.remove(0); }
					if(! (transaction == null)) 
						{
							System.out.println( "Starting transaction " + transaction.id);
							for(ClassOperations operation : transaction.list_of_operations) {
								switch(operation.ltype) {
									case 3: { transaction.calculateOperation(operation); break; }
									case 1: {
										boolean result = csi.requests_for_locks(operation);
										if(!result) { blocked(); }
										if(abort) { break; }
										transaction.calculateOperation(operation); break;
									}
									case 2: {
										boolean result = csi.requests_for_locks(operation);
										if(!result) { blocked(); }
										if(abort) { break; }
										transaction.calculateOperation(operation); break;
									}	
								}
								if(abort) { break; }		
							}
							if(!abort) {
								//Commits the transaction;
								for(String key: transaction.hash1.keySet()) {
									DataManager.write(key,  transaction.hash1.get(key));
									DataManager2.write(key,  transaction.hash1.get(key));
									DataManager3.write(key,  transaction.hash1.get(key));
									DataManager4.write(key,  transaction.hash1.get(key));
								}
								System.out.println("Transaction " + transaction.id + " is committed");
								csi.lock_release(transaction);
								
							}
							else {
								System.out.println("Transaction " + transaction.id + " is aborted");
								abort = false;
							}
						}
		
					else { System.out.println( "Site " + id + " waiting for next transaction and is blocked");
						blocked();
					}	
				}
				
			}
			catch(RemoteException e) { System.out.println(" Remote Exception: " + e.getMessage());	}
			catch(Exception e) { System.out.println( " Exception: " + e.getMessage()); }
		}
	}
	
	@Override
	public void unblock_site() throws RemoteException { blocked = false; }

	@Override
	public void write_to_dataSite() throws RemoteException { System.out.println( " Writing the info to the datasite " + id); }
	
}

