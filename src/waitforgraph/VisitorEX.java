package waitforgraph;


public interface VisitorEX<T, E extends Exception> {
	
	public void visit(Graph<T> g, Vertex<T> v) throws E;
	
}
