package datasite;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import publicClasses.ClassOperations;
import publicClasses.ClassTransaction;

public class TransactionManager
{
	public int SID;
	public int trans_no = 1;
	public ArrayList<ClassTransaction> transaction_history;
	
	public TransactionManager(int SID) { transaction_history = new ArrayList<ClassTransaction>(); this.SID = SID;	}

	public ArrayList<ClassTransaction> generateTransactions(ArrayList<String> opr_set) throws Exception {
		ArrayList<ClassTransaction> transaction_set = new ArrayList<ClassTransaction>();
		ClassTransaction curr_tr = null;
	
		for(int i = 0; i < opr_set.size(); i += 1) 
		{
			if(!(opr_set.get(i).contains("transaction")))
			{
				char type = opr_set.get(i).trim().charAt(0);
				switch(type)
				{
					case 'r': {
						Pattern pattern = Pattern.compile(".*\\((.*)\\).*"); Matcher matcher = pattern.matcher(opr_set.get(i));
						if(matcher.matches()) {
							ClassOperations operation = new ClassOperations(curr_tr.id, 1,  matcher.group(1));
							operation.tid = curr_tr.id;
							curr_tr.list_of_operations.add(operation);
						}
						break;
					}
					case 'm': {
						Pattern pattern = Pattern.compile("m(.*)\\=(.*);"); Matcher matcher = pattern.matcher(opr_set.get(i));
						if(matcher.matches()) {
							ClassOperations operation = new ClassOperations(curr_tr.id, 3,  matcher.group(1), matcher.group(2));
							operation.tid = curr_tr.id;
							curr_tr.list_of_operations.add(operation);
						}
					break; }
					case 'w': {
						Pattern pattern = Pattern.compile(".*\\((.*)\\).*"); Matcher matcher = pattern.matcher(opr_set.get(i));
						if(matcher.matches()) {
							ClassOperations operation = new ClassOperations(curr_tr.id, 2,  matcher.group(1));
							operation.tid = curr_tr.id;
							curr_tr.list_of_operations.add(operation);
						}
						break; } } } 
			else
			{
				if(curr_tr != null) { transaction_set.add(curr_tr);	}
				int transactionId = SID *  10000 + trans_no;
				trans_no += 1;
				curr_tr = new ClassTransaction(transactionId); 
				}
			
			}
		if(curr_tr != null) { transaction_set.add(curr_tr); }
		return transaction_set;
	}
}
