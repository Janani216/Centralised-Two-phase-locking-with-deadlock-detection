package publicClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import datasite.DataManager;

public class ClassTransaction implements Serializable {

	private static final long serialVersionUID = 1L;
	public int id;
	public ArrayList<ClassOperations> list_of_operations;
	public Hashtable<String, Integer> hash1;
	public Hashtable<String, Integer> hash2;
	
	public ClassTransaction(int id) {
		hash1 = new Hashtable<String, Integer>();
		hash2 = new Hashtable<String, Integer>();
		this.id = id;
		list_of_operations = new ArrayList<ClassOperations>();
		
	}
	
	public void calculateOperation(ClassOperations operation) throws Exception {
		try {
			Thread.sleep(100);
			switch(operation.ltype) {
				case 3:				
					int operand1Value;
					if(!operation.op1.matches("\\d+")) { operand1Value = hash1.get(operation.op1); }
					else { operand1Value = Integer.parseInt(operation.op1); }
					hash2.put( operation.Dataitem, operand1Value);
				break;
				case 1:
					hash1.put( operation.Dataitem, DataManager.read( operation.Dataitem));
				break;
				case 2:
					if(hash2.containsKey( operation.Dataitem)) {
						hash1.put( operation.Dataitem, hash2.get( operation.Dataitem));
					}
				break;
				
			}	}
		catch(InterruptedException e) { System.out.println("Interrupted Exception: " + e.getMessage()); }
		  }
}
