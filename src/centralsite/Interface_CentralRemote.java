package centralsite;

import java.rmi.Remote;
import java.rmi.RemoteException;

import publicClasses.ClassOperations;
import publicClasses.ClassTransaction;

public interface Interface_CentralRemote extends Remote 
{
	public boolean requests_for_locks(ClassOperations operation) throws RemoteException;
	public void lock_release(ClassTransaction transaction) throws RemoteException;
	public int get_next_Site() throws RemoteException;
}
