import java.util.LinkedList;

/*
	Node is part of SlicerDroid.
	
	SlicerDroid is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	SlicerDroid is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with SlicerDroid.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * Class Node
 *
 * Contains all the information concerning to a node
 *
 * @author Agustin & Facundo
 * @version 1.0
 */
public class Node {
	private String id;
	private String action;
	private LinkedList <String> parameter;
	private LinkedList <Node> successor; 
	private LinkedList <Node> predecessor; 
	private LinkedList <Pair<Node,String>> gen; 
	private LinkedList <Pair<Node,String>> kill;
	private LinkedList <Pair<Node,String>> in;
	private LinkedList <Pair<Node,String>> out;
	private LinkedList <Pair<Pair<Node,String>,Pair<Node,String>>> use; // node's uses
	private LinkedList <String> varUse;  // list of variables used by this node 
	private LinkedList <Node> dominate;
	private Node lca;
	private boolean pointsItself; // variable used in case that this node points itself in CDG tree
	private boolean visited; // variable used to set node as visited in Slicing algorithm 
	private boolean isBeginLoop;
	private boolean isEndLoop; 
	private Node beginLoop;
	
	/**
	 * Constructor with 1 parameter
	 * @param id a string which indicates the name of the node that is being constructed
	 */
	public Node(String id){
		this.id = id;
		action = "";
		parameter = new LinkedList <String>();
		successor = new LinkedList <Node>();
		predecessor = new LinkedList <Node>();
		gen = new LinkedList <Pair<Node,String>>();
		kill = new LinkedList <Pair<Node,String>>();
		in = new LinkedList <Pair<Node,String>>();
		out = new LinkedList <Pair<Node,String>>();
		use = new LinkedList <Pair<Pair<Node,String>,Pair<Node,String>>>();
		varUse = new LinkedList <String>(); 
		dominate = new LinkedList <Node>();
		lca = null;
		pointsItself = false;
		visited = false;
		isBeginLoop = false;
		isEndLoop = false;
		beginLoop = null;
	}
	
	/**
	 * Checks if current node is the beginning of a loop
	 * @return <ul>
	 *          <li>true: current node is the beginning of a loop</li>
	 *          <li>false: current node is not the beginning of a loop</li>
	 *          </ul>
	 */
	public boolean getIsBeginLoop(){
		return this.isBeginLoop;
	}
	
	/**
	 * Sets current node as the beginning of a loop
	 * @param isBeginLoop if true, current node is seted as the beginning of a loop  
	 */
	public void setIsBeginLoop(boolean isBeginLoop){
		this.isBeginLoop = isBeginLoop;
	}
	
	/**
	 * Outputs current node's flag which indicates if this is the end of a node
	 * @return <ul>
	 *          <li>true: current node is the end of a loop</li>
	 *          <li>false: current node is not the end of a loop</li>
	 *          </ul>
	 */
	public boolean getIsEndLoop(){
		return this.isEndLoop;
	}
	
	/**
	 * Sets current node as the end of a loop
	 * @param isEndLoop if true, current node is seted as the end of a loop  
	 */
	public void setIsEndLoop(boolean isEndLoop){
		this.isEndLoop = isEndLoop;
	}
	
	/**
	 * Outputs the initial node of the loop which current node is a part of 
	 * @return initial node of the loop which current node is a part of 
	 */
	public Node getBeginLoop(){
		return this.beginLoop;
	}
	
	/**
	 * Sets initial node of the loop which current node is a part of
	 * @param beginLoop a node to be seted as the initial node of the loop which current node is a part of 
	 */
	public void setBeginLoop(Node beginLoop){
		this.beginLoop = beginLoop;
	}
	
	/**
	 * Outputs current node's least common ancestor
	 * @return a node that is current node's least common ancestor 
	 */
	public Node getLCA(){
		return this.lca;
	}
	
	/**
	 * Sets current node's least common ancestor
	 * @param lca a node to be seted as current node's least common ancestor 
	 */
	public void setLCA (Node lca){
		this.lca = lca;
	}
	
	/**
	 * Sets current node pointing itself within the graph
	 * @param value if true, currents node points itself within the graph
	 */
	public void setPointsItself(boolean value){
		this.pointsItself = value;
	}
	
	/**
	 * Outputs current node's flag which indicates if this node points itself within the graph
	 * @return <ul>
	 *          <li>true: current node points itself within the graph</li>
	 *          <li>false: current node does not point itself within the graph</li>
	 *          </ul>
	 */
	public boolean getPointsItself(){
		return this.pointsItself;
	}
	
	/**
	 * Sets current node's flag which indicates if this node has already been visited
	 * @param visited boolean value to be seted as the new value of flag visited
	 */
	public void setVisited(boolean visited){
		this.visited = visited;
	}
	
	/**
	 * Outputs list of variables used by this node and others
	 * @return list of variables used by this node and others as a linked list made of pair of pairs that contains strings and nodes 
	 */
	public LinkedList <Pair<Pair<Node,String>,Pair<Node,String>>> getUse(){
		return this.use;
	}
	
	/**
	 * Outputs current node's flag which indicates if this node has already been visited
	 * @return <ul>
	 *          <li>true: current node has already been visited</li>
	 *          <li>false: current node has not been visited yet</li>
	 *          </ul>
	 */
	public boolean getVisited(){
		return this.visited;
	}
	
	/**
	 * Outputs this node's action
	 * @return a string representing this node's action 
	 */
	public String getAction(){
		return this.action;
	}
	
	/**
	 * Outputs list of variables used by this node 
	 * @return list of variables used by this node as a linked list made of strings 
	 */
	public LinkedList<String> getVarUse(){
		return this.varUse;
	}
	
	/**
	 * Adds a new pair to this node's use list
	 * @param p pair made of a string and a node to be added as the first component of a new pair within current node's list named use 
	 * @param q pair made of a string and a node to be added as the second component of a new pair within current node's list named use 
	 */
	public void addUse(Pair<Node,String> p,Pair<Node,String> q){
		Pair<Pair<Node,String>,Pair<Node,String>> newUse = new Pair<Pair<Node,String>, Pair<Node,String>>(p, q);
		this.use.add(newUse);
	}
	
	/**
	 * Adds a new parameter to this node's parameters' list
	 * @param newParameter new parameter to be added
	 */
	public void addParameter(String newParameter){
		this.parameter.add(newParameter);
	}
	
	/**
	 * Adds a new node to this node's successors' list
	 * @param newSuccesor new successor to be added
	 */
	public void addSuccesor(Node newSuccesor){
		this.successor.add(newSuccesor);
	}
	
	/**
	 * Outputs this node's successors' list
	 * @return this node's successors' list as a linked list made of nodes
	 */
	public LinkedList<Node> getSuccessors(){
		return this.successor;
	}
	
	/**
	 * Outputs this node's parameters' list
	 * @return this node's parameters' list as a linked list made of strings
	 */
	public LinkedList<String> getParameters(){
		return this.parameter;
	}	
	
	/**
	 * Adds a new node to this node's predecessors' list
	 * @param newPredecessor new predecessor to be added
	 */
	public void addPredecessor(Node newPredecessor){
		this.predecessor.add(newPredecessor);
	}	
	
	/**
	 * Outputs this node's predecessors' list
	 * @return this node's predecessors' list as a linked list made of nodes
	 */
	public LinkedList<Node> getPredecessors(){
		return this.predecessor;
	}

	/**
	 * Sets this node's action
	 * @param action string representing this node's new action to be seted
	 */
	public void setAction (String action){
		this.action = action;
	}
	
	/**
	 * Outputs this node's ID
	 * @return string representing this node's ID
	 */
	public String getID(){
		return this.id;
	}
	
	/**
	 * Checks if string passed as a parameter is equal to a node's ID within this node's list of successors
	 * @param successor string to be checked as a part of this node's list of successors
	 * @return <ul>
	 *          <li>true: string passed as a parameter is equal to a node's ID within this node's list of successors</li>
	 *          <li>false: string passed as a parameter is not equal to a node's ID within this node's list of successors</li>
	 *          </ul>
	 */
	public boolean isSuccessor(String successor){
		for(int i=0; i<this.successor.size(); i++){
			if(this.successor.get(i).getID().equalsIgnoreCase(successor))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if string passed as a parameter is equal to a node's ID within this node's list of predecessors
	 * @param predecessor string to be checked as a part of this node's list of predecessors
	 * @return <ul>
	 *          <li>true: string passed as a parameter is equal to a node's ID within this node's list of predecessors</li>
	 *          <li>false: string passed as a parameter is not equal to a node's ID within this node's list of predecessors</li>
	 *          </ul>
	 */
	public boolean isPredecessor(String predecessor){
		for(int i=0; i<this.predecessor.size(); i++){
			if(this.predecessor.get(i).getID().equalsIgnoreCase(predecessor))
				return true;
		}
		return false;
	}
	
	/**
	 * Adds a new pair to this node's 'kill' list
	 * @param n a node to be added as the first component of a new pair within this node's list named kill
	 * @param field a sting to be added as the second component of a new pair within this node's list named kill
	 */
	public void addKill(Node n, String field){
		Pair<Node, String> p = new Pair<Node,String>(n,field);
		this.kill.add(p);
	}
	
	/**
	 * Adds a new pair to this node's 'gen' list
	 * @param n a node to be added as the first component of a new pair within this node's list named gen
	 * @param field a sting to be added as the second component of a new pair within this node's list named gen
	 */
	public void addGen(Node n, String field){
		Pair<Node, String> p = new Pair<Node,String>(n,field);
		this.gen.add(p);
	}
	
	/**
	 * Checks if pair passed as a parameter is equal to a pair within this node's 'kill' list
	 * @param p a pair meant to be checked as equal to a member of this node's 'kill' list
	 * @return <ul>
	 *          <li>true: pair 'p' passed as a parameter is equal to a pair within this node's 'kill' list</li>
	 *          <li>false: pair 'p' passed as a parameter is not equal to a pair within this node's 'kill' list</li>
	 *          </ul>
	 */
	public boolean isAlreadyInKill(Pair<Node,String> p){
		for(int i=0; i<this.kill.size(); i++){
			if((p.getFirst().getID().equalsIgnoreCase(this.kill.get(i).getFirst().getID()))&&(p.getSecond().equalsIgnoreCase(this.kill.get(i).getSecond())))
				return true;
		}
		return false;
	}

	/**
	 * Outputs the variable generated by this node
	 * @return a string that represents the variable generated by this node   
	 */
	public String getGenVariable() {
		if (!(this.gen.isEmpty()))
			return this.gen.get(0).getSecond();
		else
			return null;
	}
	
	/**
	 * Outputs all pairs made of nodes and strings contained in this node's 'kill' list
	 * @return a linked list of pairs made of nodes and strings
	 */
	public LinkedList<Pair<Node,String>> getKills(){
		return this.kill;
	}

	/**
	 * Outputs all pairs made of nodes and strings contained in this node's 'gen' list
	 * @return a linked list of pairs made of nodes and strings
	 */
	public LinkedList<Pair<Node,String>> getGen(){
		return this.gen;
	}
	
	/**
	 * Adds pair 'p' passed as a parameter into this node's 'out' list 
	 * @param p a pair made of a node and a string
	 */
	public void addOut(Pair<Node,String> p){
		this.out.add(p);
	}
	
	/**
	 * Adds pair 'p' passed as a parameter into this node's 'in' list 
	 * @param p a pair made of a node and a string
	 */
	public void addIn(Pair<Node,String> p){
		this.in.add(p);
	}
	
	/**
	 * Outputs this node's 'out' list
	 * @return a linked list of pairs made of a node and a string
	 */
	public LinkedList<Pair<Node,String>> getOut(){
		return this.out;
	}
	
	/**
	 * Outputs this node's 'in' list
	 * @return a linked list of pairs made of a node and a string
	 */
	public LinkedList<Pair<Node,String>> getIn(){
		return this.in;
	}
	
	/**
	 * Builds a list of pairs made of a node and a string. Those who are found within this node's 'in' list but not within
	 * this node's 'kill' list
	 * @return a linked list of pairs made of a node and a string
	 */
	public LinkedList<Pair<Node,String>> inLessKill(){
		LinkedList<Pair<Node,String>> in_aux = this.in;
		LinkedList<Pair<Node,String>> kill_aux = this.kill;
		in_aux.removeAll(kill_aux);
		return in_aux; // check outside if this list is empty or not
	}
	
	/**
	 * Checks if this node's 'out' list and 'other' list passed as a parameter are equal between each other
	 * @param other a list of pairs made of a node and a string
	 * @return <ul>
	 *          <li>true: both lists are equal between each other</li>
	 *          <li>false: both lists are not equal between each other</li>
	 *          </ul>
	 */
	public boolean equalsOut(LinkedList<Pair<Node, String>> other){
		int count = 0;
		boolean res = false;
		if(this.out.size()==other.size()){
			for(int i=0; i<this.out.size(); i++){
				for(int j=0; j<other.size(); j++){
					if((this.out.get(i).getFirst().getID().equalsIgnoreCase(other.get(j).getFirst().getID()))&&(this.out.get(i).getSecond().equalsIgnoreCase(other.get(j).getSecond())))
						count++;
				}
			}
			if(count==other.size())
				res = true;
		}
		return res;
	}
	
	/**
	 * Checks if both pairs passed as parameter are equal between each other
	 * @param p1 a pair made of a node and a string
	 * @param p2 a pair made of a node and a string
	 * @return <ul>
	 *          <li>true: both pairs passed as parameter are equal between each other</li>
	 *          <li>false: both pairs passed as parameter are not equal between each other</li>
	 *          </ul>
	 */
	public boolean equalPairs(Pair<Node,String> p1, Pair<Node,String> p2){
		if((p1.getFirst().getID().equalsIgnoreCase(p2.getFirst().getID()))&&(p1.getSecond().equalsIgnoreCase(p2.getSecond())))
			return true;
		return false;
	}
	
	/**
	 * Builds a list containing all pairs which are found within this node's 'in' list, but not on this node's 'kill' list 
	 * @return a linked list of pairs made of nodes and strings
	 */
	public LinkedList<Pair<Node,String>> difference(){ 
		LinkedList<Pair<Node,String>> list = new LinkedList<Pair<Node,String>>();
		boolean isEqual;
		for(int i=0; i<this.in.size(); i++){
			isEqual = false;
			for(int j=0; j<this.kill.size(); j++){
				if(equalPairs(this.in.get(i), this.kill.get(j)))
					isEqual = true;
			}
			if(!(isEqual)) // if it never matched, it is on the difference
				list.add(this.in.get(i));
		}
		return list;
	}
	
	/**
	 * Adds to this node's 'out' list those pairs appearing in the list passed as a parameter different from all pairs which are already
	 * within this node's 'out' list
	 * @param list a linked list of pairs made of nodes and strings
	 */
	public void union(LinkedList<Pair<Node,String>> list){ 
		boolean isEqual;
		for(int i=0; i<list.size(); i++){
			isEqual = false;
			for(int j=0; j<this.out.size(); j++){
				if(equalPairs(list.get(i),this.out.get(j)))
					isEqual = true;
			}
			if(!(isEqual)) 
				this.out.add(list.get(i));
		}
	}
	
	/**
	 * Adds to this node's 'in' list those pairs appearing in the list passed as a parameter different from all pairs which are already
	 * within this node's 'in' list
	 * @param list a linked list of pairs made of nodes and strings
	 */
	public void unionIN(LinkedList<Pair<Node,String>> list){ 
		boolean isEqual;
		for(int i=0; i<list.size(); i++){
			isEqual = false;
			for(int j=0; j<this.in.size(); j++){
				if(equalPairs(list.get(i),this.in.get(j)))
					isEqual = true;
			}
			if(!(isEqual)) 
				this.in.add(list.get(i));
		}
	}
	
	/**
	 * Adds a string passed as a parameter into the list of variables used by this node
	 * @param varUse string to be added into the list of variables used by this node
	 */
	void addVarUse (String varUse){
		this.varUse.add(varUse);
	}
	
	/**
	 * Builds a list of nodes which represents the union of current node and nodes contained on list 'dom' passed as a parameter
	 * @param dom a list of nodes
	 * @return a list of nodes which represents the union of current node and nodes contained on list 'dom' passed as a parameter
	 */
	public LinkedList<Node> unionDom(LinkedList<Node> dom){
		boolean isEqual;
		LinkedList<Node> res = new LinkedList<Node>();
		res.add(this);
		for(int i=0; i<dom.size(); i++){
			isEqual = false;
			if(dom.get(i).getID().equalsIgnoreCase(this.getID())){
				isEqual = true;
			}
			if(!(isEqual))
				res.add(dom.get(i));
		}
		return res;
	}
	
	/**
	 * Outputs this node's 'dominate' list
	 * @return a list of nodes which is this node's 'dominate' list
	 */
	public LinkedList<Node> getDom (){
		return this.dominate;
	}
	
	/**
	 * Sets list of nodes passed as a parameter to be this node's new 'dominate' list 
	 * @param dom list of nodes meant to be this node's new 'dominate' list
	 */
	public void setDom (LinkedList<Node> dom){
		this.dominate.clear();
		for(int i = 0; i<dom.size(); i++){
			this.dominate.add(dom.get(i));
		}
	}
	
	/**
	 * Builds a list of nodes that represents the intersection of both lists passed as parameters 
	 * @param l1 first list of nodes passed as a parameter
	 * @param l2 first list of nodes passed as a parameter
	 * @return a list of nodes that represents the intersection of both lists passed as parameters 
	 */
	public LinkedList<Node> intersection (LinkedList<Node> l1,LinkedList<Node> l2){
		LinkedList<Node> res = new LinkedList<Node>();
		boolean isEqual;
		for(int i=0; i<l1.size(); i++){
			isEqual = false;
			for(int j=0; j<l2.size(); j++){
				if(l1.get(i).getID().equalsIgnoreCase(l2.get(j).getID()))
					isEqual = true;
			}
			if(isEqual)
				res.add(l1.get(i));
		}
		return res;
	}
	
	/**
	 * Builds a list of nodes which are found within the intersection of all the 'dominate' lists from this node's successors 
	 * @return a list of nodes which are found within the intersection of all the 'dominate' lists from this node's successors
	 */
	public LinkedList<Node> intersectionDom(){
		LinkedList<Node> res = new LinkedList<Node>();
		LinkedList<Node> aux;
		if (this.getSuccessors().size() != 0){
			if(this.getSuccessors().size()==1){
				res = this.getSuccessors().get(0).getDom();
			}else{
				aux = this.getSuccessors().get(0).getDom();
				for (int i = 1; i < this.getSuccessors().size(); i++){
					aux = intersection(aux,this.getSuccessors().get(i).getDom());
				}
			res = aux;
			}
		}
		return res;
	}
	
	/**
	 * Takes out from this node's 'dominate' list the node passed as a parameter 
	 * @param n a node to be taken out from this node's 'dominate' list
	 */
	public void undominate(Node n){
		for(int i=0; i<this.dominate.size(); i++){
			if(this.dominate.get(i).getID().equalsIgnoreCase(n.getID())) // if they match, the node must be taken out from the list
				this.dominate.remove(i);
		}
	}
	
	/**
	 * Checks if node passed as a parameter's ID is equal to a node's ID within this node's list called 'dominate'
	 * @param m a node to be checked as equal to a node within this node's list called 'dominate' 
	 * @return <ul>
	 *          <li>true: node n's ID is equal to a node's ID within this node's list called 'dominate'</li>
	 *          <li>false: node n's ID is not equal to a node's ID within this node's list called 'dominate'</li>
	 *          </ul>
	 */
	public boolean dominateContains(Node m){
		boolean res = false;
		for(int i=0; i<this.dominate.size(); i++){
			if(this.dominate.get(i).getID().equalsIgnoreCase(m.getID()))
				res = true;
		}
		return res;
	}
	
	/**
	 * Checks if this node's 'dominate' list is empty
	 * @return <ul>
	 *          <li>true: this node's 'dominate' list is empty</li>
	 *          <li>false: this node's 'dominate' list is not empty</li>
	 *          </ul>
	 */
	public boolean dominateIsEmpty(){
		return this.dominate.isEmpty();
	}
	
	/**
	 * Checks if node passed as a parameter's ID is equal to a node's ID within this node's list of successors
	 * @param n a node to be checked as equal to a node within this node's list of successors 
	 * @return <ul>
	 *          <li>true: node n's ID is equal to a node's ID within this node's list of successors</li>
	 *          <li>false: node n's ID is not equal to a node's ID within this node's list of successors</li>
	 *          </ul>
	 */
	public boolean isSuccesor(Node n){
		boolean res = false;
		for(int i=0; i<this.successor.size(); i++){
			if(this.successor.get(i).getID().equalsIgnoreCase(n.getID()))
				res = true;
		}
		return res;
	}
	
	/**
	 * Checks if node passed as a parameter's ID is equal to a node's ID within this node's list of predecessors
	 * @param n a node to be checked as equal to a node within this node's list of predecessors 
	 * @return <ul>
	 *          <li>true: node n's ID is equal to a node's ID within this node's list of predecessors</li>
	 *          <li>false: node n's ID is not equal to a node's ID within this node's list of predecessors</li>
	 *          </ul>
	 */
	public boolean isPredecessor(Node n){
		boolean res = false;
		for(int i=0; i<this.predecessor.size(); i++){
			if(this.predecessor.get(i).getID().equalsIgnoreCase(n.getID()))
				res = true;
		}
		return res;
	}
	
	/**
	 * Outputs this node's ID
	 * @return a string describing this node's ID
	 */
	public String toString(){
		return this.id.toUpperCase();
	}
	
	/**
	 * Outputs this node's parameters all together
	 * @return a string describing this node's parameters all together  
	 */
	private String parametersToString(){
		String res = "";
		for(int i=0; i<this.parameter.size(); i++){
			res += this.parameter.get(i);
			if(i!=(this.parameter.size()-1)) // no es el ultimo, va la coma
				res += ", ";
		}
		return res;
	} 
	
	/**
	 * Outputs this node's ID,its action and its parameters all together
	 * @return a string describing this node's ID,its action and its parameters all together
	 */
	public String nodeToString(){
		String res = "";
		res += this.id.toUpperCase();
		res += "   ";	
		res += this.action;
		res += "\n";
		res += this.parametersToString();
		return res;
	}
}