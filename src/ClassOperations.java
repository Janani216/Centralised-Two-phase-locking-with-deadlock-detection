package publicClasses;

import java.io.Serializable;

public class ClassOperations implements Serializable {
	private static final long serialVersionUID = 1L;
	public int tid,ltype;
	public String Dataitem,op1;
	public ClassOperations(int tid, int ltype, String ditem) { this.tid = tid; this.ltype = ltype; this.Dataitem = ditem;}
	public ClassOperations(int tid, int ltype, String ditem, String op1) { this.op1 = op1; this.tid = tid; this.ltype = ltype; this.Dataitem = ditem; }

	public ClassLock getLock() {
		int Ltype;
		switch(ltype) {
			case 1: Ltype = 1; break;
			case 2: Ltype = 2; break;
			default: return null;
		}
		ClassLock L = new ClassLock(Ltype, Dataitem, tid);
		return L;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		switch(ltype) {
			case 1: builder.append("read " + Dataitem + " - " + tid); break;
			case 2: builder.append("write " + Dataitem + "-" + tid); break;
			case 3: builder.append("math " + Dataitem + " - " + op1 + "), "); break;
		}
		return builder.toString();
	}
}
