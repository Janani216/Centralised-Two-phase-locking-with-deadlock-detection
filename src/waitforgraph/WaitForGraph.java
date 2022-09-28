package waitforgraph;

import java.util.ArrayList;
import java.util.List;

public class WaitForGraph {
	
	private Graph<Integer> wfg;
	
	public WaitForGraph() {
		wfg = new Graph<Integer>();
	}

	public Boolean addDependency(Integer trans1, Integer trans2) {
		Vertex<Integer> t1 = wfg.findVertexByName(trans1.toString());
		Vertex<Integer> t2 = wfg.findVertexByName(trans2.toString());
		if(t1 == null)
		{
			t1 = new Vertex<Integer>(trans1.toString(), trans1);
			wfg.addVertex(t1);
		}
		if(t2 == null)
		{
			t2 = new Vertex<Integer>(trans2.toString(), trans2);
			wfg.addVertex(t2);
		}
		return wfg.addEdge(t1, t2, 1);
	}

	public List<Integer> checkCycles() {
		Edge<Integer>[] edgesToRemove = wfg.findCycles();
		
		if(edgesToRemove.length == 0)
			return null;
		
		List<Integer> edge = new ArrayList<Integer>();
		edge.add(edgesToRemove[0].getFrom().getData());
		edge.add(edgesToRemove[0].getTo().getData());
		
		return edge;
	}
	
}
