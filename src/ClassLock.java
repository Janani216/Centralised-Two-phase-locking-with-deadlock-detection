package publicClasses;

public class ClassLock {
	public String Dataitem;
	public int Ltype;
	public int tid;

	public ClassLock(int ltype, String ditem, int tid) {
		this.Ltype = ltype;
		this.Dataitem = ditem;
		this.tid = tid;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		switch(Ltype) {
			case 1: builder.append("read lock "+ Dataitem + "-" + tid); break;
			case 2: builder.append("write lock"+ Dataitem + "-" + tid); break;
			case 3: builder.append("read&write lock "+ Dataitem + "-" + tid); break;
		}
		return builder.toString();
	}

	public void upgrade_LockType(int newType) {	
		if (Ltype == 2 && newType == 1) { Ltype = 3; }
		else if (Ltype == 1 && newType == 2) {Ltype = 3;}
	}
}
