package centralsite;
import java.util.*;

import publicClasses.ClassLock;
import publicClasses.ClassOperations;
import publicClasses.ClassTransaction;
import publicClasses.ClassTransactionsToCheck;
import waitforgraph.WaitForGraph;


public class LockManager {
	private Hashtable<String, ArrayList<ClassLock>> LocksTable;
	private Hashtable<String, ArrayList<ClassOperations>> Queue;

	public LockManager() {
		LocksTable = new Hashtable<String, ArrayList<ClassLock>>();
		Queue = new Hashtable<String, ArrayList<ClassOperations>>();
	}
	
	public ArrayList<Integer> abort_transaction_during_deadlock(int tid) 
	{
		ClassTransaction t = new ClassTransaction(tid);
		try {
			print_lockTable_QueueTable();
			//Pop blocked Transactions
			for(String key :  Queue.keySet())
			{
				ArrayList<Integer> re = new ArrayList<Integer>();
				for(int k = Queue.get(key).size() - 1; k >= 0; k -= 1) { if( Queue.get(key).get(k).tid == t.id) { re.add(k); } }
				for(int j : re) { Queue.get(key).remove(j);}
			}
			System.out.println("After removing the blocked operations, the lock table and the queue table consists of:");
			print_lockTable_QueueTable();
			ArrayList<Integer> released = releases_all_locks_held(t);
			System.out.println("After releasing the locks, the lock table and the queue table consists of:");
			print_lockTable_QueueTable();
			return released;
		}
		catch(Exception e) {
			System.out.println( "Exception while aborting transaction:" + e.getMessage());
			return new ArrayList<Integer>();
		}	
	}
	
	public ClassTransactionsToCheck<Integer, ArrayList<Integer>> check_for_deadlocks() {
		WaitForGraph g = new WaitForGraph();
		for(String item : LocksTable.keySet()) 
		{
			ArrayList<ClassOperations> aborted_trans = Queue.get(item);
			ArrayList<ClassLock> l = LocksTable.get(item);		
			if(aborted_trans != null && aborted_trans.size() != 0) 
			{
				for(int i = 0; i < l.size(); i += 1)
				{ Integer first = l.get(i).tid;
					for(int j = 0; j < aborted_trans.size(); j += 1) {
						if(!first.equals(aborted_trans.get(j).tid)) {g.addDependency(first, aborted_trans.get(j).tid);}
				}}}}
		List<Integer> d_edge = g.checkCycles();
		if(d_edge == null) { return null; }
		else
		{
		Integer abortingTransaction = d_edge.get(1);
		System.out.println("Deadlock detected in transactions " + d_edge.get(0) + " and " + d_edge.get(1) + " the transaction to be aborted: " + abortingTransaction.intValue());
		ArrayList<Integer> unblocked = abort_transaction_during_deadlock(abortingTransaction.intValue());
		return new ClassTransactionsToCheck<Integer, ArrayList<Integer>>(abortingTransaction, unblocked);} }

	public ClassOperations pop_from_queue(String var) {
		if(!Queue.containsKey(var)) { return null;}
		if(Queue.get(var).size() <= 0) { Queue.remove(var); return null; }
		else {
			if(check_compatibility(Queue.get(var).get(0).getLock())) {
				if(Queue.get(var).size() == 0) { Queue.remove(var); }
				return  Queue.get(var).remove(0);	
			}
			else { return null; }	
		}	
	}
	
	public ClassLock search_lock(int transactionId, String item) { 
		if(LocksTable.get(item) == null) { return null; }
		for(int j = 0; j < LocksTable.get(item).size(); j += 1) { 
			if(LocksTable.get(item).get(j).tid == transactionId) { return LocksTable.get(item).get(j); }}
		return null;
	}
	
		public boolean check_compatibility(ClassLock lock) {
		ArrayList<ClassLock> l = LocksTable.get(lock.Dataitem);
		if(l == null) { return true; }
		switch(lock.Ltype)
		{	case 2: {	
				for(int i = 0; i <  l.size(); i += 1) { if(l.get(i).tid != lock.tid) { return false; } }		
				break; }
			case 1: {
				for(int i = 0; i <  l.size(); i += 1) { if(l.get(i).tid != lock.tid && l.get(i).Ltype >= 2) { return false; } }
				break; }
			default: return false; }
		return true; }

	public void print_lockTable_QueueTable() {	
		if (!Queue.isEmpty()){ System.out.println("Lock Table");
			for(String item : LocksTable.keySet()) {
				ArrayList<ClassLock> L = LocksTable.get(item);
				for(int i = 0; i <  L.size(); i += 1) { System.out.println(L.get(i).toString());}	} }

		if (!Queue.isEmpty()){ System.out.println("Queue Table");
			for(String item : Queue.keySet()) {
				for(int i = 0; i <  Queue.get(item).size(); i += 1) { System.out.println(Queue.get(item).get(i).toString()); } } }
		}
	
	public ArrayList<Integer> releases_all_locks_held(ClassTransaction transaction) throws Exception {
		ArrayList<Integer> unlockedSites = new ArrayList<Integer>();
		
		ArrayList<ClassLock> l = new ArrayList<ClassLock>();
		for(String x : LocksTable.keySet()) {
			ArrayList<ClassLock> lockedLocks = LocksTable.get(x);
			for(int i = 0; i < lockedLocks.size(); i += 1) {
				if(lockedLocks.get(i).tid == transaction.id) {
					l.add(lockedLocks.get(i));
				} }	}
		
		for(int i = 0; i < l.size(); i += 1) {
			//Remove from locktable
			ClassLock lo = l.get(i);
			ArrayList<ClassLock> L = LocksTable.get(lo.Dataitem);
			for(int j = 0; j < L.size(); j += 1) { if(L.get(j).tid == lo.tid) { LocksTable.get(lo.Dataitem).remove(L.get(j));
					if(LocksTable.get(lo.Dataitem).size() == 0) { LocksTable.remove(lo.Dataitem); }
					 }}
			
			ClassOperations op = pop_from_queue(l.get(i).Dataitem);
			if(op != null) {
				ClassLock nextLock = op.getLock();
				if(check_compatibility(nextLock)) {
					ClassLock oldLock = search_lock(nextLock.tid, nextLock.Dataitem);
					if(oldLock != null) { oldLock.upgrade_LockType(nextLock.Ltype); }
					else { 
						//Insert into locktable
					if(!LocksTable.containsKey(nextLock.Dataitem)) { LocksTable.put(nextLock.Dataitem, new ArrayList<ClassLock>()); }
					LocksTable.get(nextLock.Dataitem).add(nextLock);
					}
					unlockedSites.add(op.tid/ 10000);
				} } }
		System.out.println("Transaction " + transaction.id + " releases all of its locks");
		return unlockedSites;	
	}
	
	
	public boolean requests_for_lock(ClassOperations op) throws Exception {
		if(!check_compatibility( op.getLock())) { 
			//Add_into Queue 
			if(!Queue.containsKey(op.Dataitem)) { Queue.put(op.Dataitem, new ArrayList<ClassOperations>()); }
			Queue.get(op.Dataitem).add(op);
			return false; 
			}
		else { ClassLock currL = search_lock(op.tid, op.Dataitem);
		if(currL != null) {currL.upgrade_LockType( op.getLock().Ltype);  }
		else { 
			//Inserts into Lock Table
			ClassLock nextLock = op.getLock();
			if(!LocksTable.containsKey(nextLock.Dataitem)) { LocksTable.put(nextLock.Dataitem, new ArrayList<ClassLock>()); }
			LocksTable.get(nextLock.Dataitem).add(nextLock);
		}
		return true;}
		}
}
