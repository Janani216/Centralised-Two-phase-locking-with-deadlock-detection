package datasite;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Interface_DataRemote extends Remote {
	public void write_to_dataSite() throws RemoteException;
	public void unblock_site() throws RemoteException;
	public void abort() throws RemoteException;
}
