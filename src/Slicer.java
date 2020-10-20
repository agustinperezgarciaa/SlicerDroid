import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

/*
	Slicer is part of SlicerDroid.

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
 * Class Slicer
 *
 * This class contains all the necessary algorithms to generate the initial graph
 * from the input data and generates the final sliced graph
 *
 * @author Agustin & Facundo
 * @version 1.0
 */
public class Slicer {
	//these are meant to read nodes.txt
	static File fileNodes = null;
	static FileReader frN = null;
	static BufferedReader brN = null;
	//these are meant to read predecessors.txt
	static File filePredecessors = null;
	static FileReader frP = null;
	static BufferedReader brP = null;
	//these are meant to read successors.txt
	static File fileSuccessors = null;
	static FileReader frS = null;
	static BufferedReader brS = null;
	//these are meant to write DominatorTree.dot 
	static FileWriter fileDotDomTree = null;
	static PrintWriter fwDom = null;
	//these are meant to write CDGTree.dot
	static FileWriter fileDotCDG = null;
	static PrintWriter fwCDG = null;
	//these are meant to write PDG.dot
	static FileWriter fileDotPDG = null;
	static PrintWriter fwPDG = null;
	//these are meant to write SLICING.dot
	private static LinkedList <Node> nodes = new LinkedList<Node>();
	private static Tree<Node> dominatorTree;
	private LinkedList<TreeNode<Node>> treeNodes;
	
	/**
	 * Constructor with no parameters
	 */
	public Slicer(){
		nodes = new LinkedList<Node>();
		treeNodes = new LinkedList<TreeNode<Node>>();
		for(int i=0; i<nodes.size(); i++){
			treeNodes.add(new TreeNode<Node>(nodes.get(i)));
		}
	}
	
	/**
	 * Reads input files and generates a list of nodes representing the initial graph
	 * @return list of nodes representing the initial graph. Each node with its 
	 * 		   own successors and predecessors
	 */
	public static LinkedList <Node> parse(){
		try {
			fileNodes = new File (".input/nodes.txt");
			frN = new FileReader (fileNodes);
			brN = new BufferedReader(frN);
			// read file
			String lineN;	
			lineN = brN.readLine();
			while(lineN != null){
				String lineAux = lineN;
				if (lineAux.compareTo("NAME ") == 0){
					lineAux=brN.readLine();
					Node node = new Node (lineAux);
					lineAux=brN.readLine();
					lineAux=brN.readLine();
					node.setAction(lineAux);
					lineAux=brN.readLine();
					lineAux=brN.readLine();
					while((lineAux != null) && (lineAux.compareTo("NAME ") != 0)){
						node.addParameter(lineAux);
						lineAux=brN.readLine();
					}
					nodes.add(node);
				}
				lineN = lineAux;
			}
			fileSuccessors= new File (".input/successors.txt");
			frS = new FileReader (fileSuccessors);
			brS = new BufferedReader(frS);
			Node n1;
			String lineS = brS.readLine();
			String lineAuxS2;
			while(lineS != null){
				String lineAuxS1 = lineS;
				if (lineAuxS1.compareTo("VERTEX") == 0){
					lineAuxS1=brS.readLine();
					lineAuxS2=brS.readLine();
					n1 = getNodeById(lineAuxS1);
					if (!(n1.isSuccessor(lineAuxS2))){
						n1.addSuccesor(getNodeById(lineAuxS2));
					}
				}
				lineS = brS.readLine();			
			}
			filePredecessors= new File (".input/predecessors.txt");
			frP = new FileReader (filePredecessors);
			brP = new BufferedReader(frP);
			Node n2;
			String lineP = brP.readLine();
			String lineAuxP2;
			while(lineP != null){
				String lineAuxP1 = lineP;
				if (lineAuxP1.compareTo("VERTEX") == 0){
					lineAuxP1=brP.readLine();
					lineAuxP2=brP.readLine();
					n2 =getNodeById(lineAuxP1);
					if (!(n2.isPredecessor(lineAuxP2))){
						n2.addPredecessor(getNodeById(lineAuxP2));
					}
				}
				lineP = brP.readLine();			
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return nodes;
	}
	
	/**
	 * gets a node by its id 
	 * @return a node from the list whose id is equal to the string 
	 * 		   passed as parameter
	 */
	public static Node getNodeById (String id){
		int i;
		for (i=0; i<nodes.size() ; i++){
		if (nodes.get(i).getID().equalsIgnoreCase(id)){
			return nodes.get(i);
			}
		}
		return null;
	}
	
	/**
	 * deletes from the list those nodes that are not connected in the original graph
	 * @return refined list of nodes 
	 */
	public LinkedList<Node> cleanPayload(){
		LinkedList<String> payload = new LinkedList<String>();
		Node n; //meant to travel along the block's nodes
		for(int i=0; i<Slicer.nodes.size(); i++){
			if(Slicer.nodes.get(i).getAction().equalsIgnoreCase("fill-array-data-payload")||
				Slicer.nodes.get(i).getAction().equalsIgnoreCase("sparse-switch-payload")||
				Slicer.nodes.get(i).getAction().equalsIgnoreCase("packed-switch-payload")){
				payload.add(Slicer.nodes.get(i).getID());
			}
		}
		// If payload list is not empty, we seek all its predecessors until 
		// one of them no longer have more ancestors.
		// Since these are connected only to EXIT node, when the node
		// that has no predecessors is found, it means that the
		// beginning of the block containing the payload statement was found.
		for(int j=0; j<payload.size(); j++){
			n = getNodeById(payload.get(j));
			for(int k=0; k<n.getPredecessors().size(); k++){ //Travel along predecessors and add them to payload list
				payload.add(n.getPredecessors().get(k).getID());
			}
		}
		//At this point, i have all of the nodes which should be removed from payload list
		for(int i=0; i<Slicer.nodes.size(); i++){
			for(int j=0; j<payload.size(); j++){
				if(Slicer.nodes.get(i).getID().equalsIgnoreCase(payload.get(j))){ //If they match, it's removed from the list
					Slicer.nodes.remove(i);
					i -=1; //Back one step back, to return to correct position and to evaluate the nodes well
				}
			}
		}
		// Now items in payload must be removed from successors and predecessors sets from the rest of the nodes 
		// so that no hanging references to nodes that no longer exist are left 
		for(int i=0; i<Slicer.nodes.size(); i++){
			for(int j=0; j<Slicer.nodes.get(i).getSuccessors().size(); j++){ // Delete successors
				for(int k=0; k<payload.size(); k++){
					if(Slicer.nodes.get(i).getSuccessors().get(j).getID().equalsIgnoreCase(payload.get(k)))
						Slicer.nodes.get(i).getSuccessors().remove(j);
				}
			}	
			for(int j=0; j<Slicer.nodes.get(i).getPredecessors().size(); j++){ // Delete predecessors
				for(int k=0; k<payload.size(); k++){
					if(Slicer.nodes.get(i).getPredecessors().get(j).getID().equalsIgnoreCase(payload.get(k)))
						Slicer.nodes.get(i).getPredecessors().remove(j);
				}
			}
		}
		return Slicer.nodes;
	}
	
	/**
	 * Depending on the action of the current node updates its own 
	 * list of generated variables and killed variables
	 */
	public void makeGenAndKill(){
		for (int i = 0; i<nodes.size();i++){
			switch (nodes.get(i).getAction()){
				case "nop": {}; break;
				case "move" :
				case "move/from16" :
				case "move/16" :
				case "move-wide" :
				case "move-wide/from16" :
				case "move-wide/16" :
				case "move-object" :
				case "move-object/from16" :
				case "move-object/16" :{	nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
											nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));										
											nodes.get(i).addVarUse(nodes.get(i).getParameters().get(1));};break;
				case "move-result" :
				case "move-result-wide" :
				case "move-result-object" :
				case "move-exception" : { 	nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
											nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));};break; 
				case "return-void" : {}; break;
				case "return" :
				case "return-wide" : 
				case "return-object" : {	nodes.get(i).addVarUse(nodes.get(i).getParameters().get(0));};break;
				case "const/4" :
				case "const/16" :
				case "const" :
				case "const/high16" :
				case "const-wide/16" :
				case "const-wide/32" :
				case "const-wide" :
				case "const-wide/high16" :
				case "const-string" :
				case "const-string/jumbo" :
				case "const-class" :  { 	nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
											nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));};break; 				
				case "monitor-enter" :{		nodes.get(i).addVarUse(nodes.get(i).getParameters().get(0));};break;
				case "monitor-exit" :{		nodes.get(i).addVarUse(nodes.get(i).getParameters().get(0));};break;
				case "check-cast" : {		nodes.get(i).addVarUse(nodes.get(i).getParameters().get(0));};break;
				case "instance-of" : {		nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
											nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));;
											nodes.get(i).addVarUse(nodes.get(i).getParameters().get(1));};break; 
				case "array-length" : {		nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
											nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));  
											nodes.get(i).addVarUse(nodes.get(i).getParameters().get(1));};break;
				case "new-instance" : {		nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
											nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));}; break;
				case "new-array" : {		nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
											nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));}; break;
				// filled-new-array, filled-new-array/range and fill-array-data are not supported instructions			
				case "filled-new-array" : {}; break; 
				case "filled-new-array/range" : {}; break; 
				case "fill-array-data" : {}; break; 
				case "throw" : {nodes.get(i).addVarUse(nodes.get(i).getParameters().get(0));};break; 
				case "goto" : 
				case "goto/16" :
				case "goto/32" : {}; break; 
				case "packed-switch" : {}; break;
				case "sparse-switch" : {}; break;
				case "cmpl-float" :
				case "cmpg-float" :
				case "cmpl-double" :
				case "cmpg-double" :
				case "cmp-long" : {	nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
									nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0)); 
									nodes.get(i).addVarUse(nodes.get(i).getParameters().get(1)); 
									nodes.get(i).addVarUse(nodes.get(i).getParameters().get(2));} break;
				case "if-eq" :
				case "if-ne" :
				case "if-lt" :
				case "if-ge" :
				case "if-gt" :
				case "if-le" : {	nodes.get(i).addVarUse(nodes.get(i).getParameters().get(0)); 
									nodes.get(i).addVarUse(nodes.get(i).getParameters().get(1));} break; 
				case "if-eqz" :
				case "if-nez" :
				case "if-ltz" :
				case "if-gez" :
				case "if-gtz" :
				case "if-lez" : {	nodes.get(i).addVarUse(nodes.get(i).getParameters().get(0));} break;
				case "aget" :
				case "aget-wide" :
				case "aget-object" :
				case "aget-boolean" :
				case "aget-byte" :
				case "aget-char" :
				case "aget-short" : 
				case "iget" :
				case "iget-wide" :
				case "iget-object" :
				case "iget-boolean" :
				case "iget-byte" :
				case "iget-char" :
				case "iget-short" :
				case "sget" :
				case "sget-wide" :
				case "sget-object" :
				case "sget-boolean" :
				case "sget-byte" :
				case "sget-char" :
				case "sget-short" : {	nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
										nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));
										nodes.get(i).addVarUse(nodes.get(i).getParameters().get(1));};  break; 
				/* get: x = A[i]
				 * put: A[i] = x 
				 */
				case "aput" :
				case "aput-wide" :
				case "aput-object" :										
				case "aput-boolean" :
				case "aput-byte" :
				case "aput-char" :
				case "aput-short" : 
				case "iput" :
				case "iput-wide" :
				case "iput-object" :
				case "iput-boolean" :
				case "iput-byte" :
				case "iput-char" :
				case "iput-short" :
				case "sput" :
				case "sput-wide" :
				case "sput-object" :
				case "sput-boolean" :
				case "sput-byte" :
				case "sput-char" :
				case "sput-short" : {	nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(1));
										nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(1));
										nodes.get(i).addVarUse(nodes.get(i).getParameters().get(0));}; break; 
				case "invoke-virtual" :
				case "invoke-super" :
				case "invoke-direct" :
				case "invoke-static" :
				case "invoke-interface" :
				case "invoke-virtual/range" :
				case "invoke-super/range" :
				case "invoke-direct/range" :
				case "invoke-static/range" :
				case "invoke-interface/range" :	{ 	
												// As the last parameter in the argument list is the method invocation. 
												// so we loop until list size minus one, so as to only store the variables
												// used in the method.
													for ( int j = 0; j < nodes.get(i).getParameters().size()-1; j++){
														nodes.get(i).addVarUse(nodes.get(i).getParameters().get(j));
													}
												};break;
				case "neg-int" :
				case "not-int" :
				case "neg-long" :
				case "not-long" :
				case "neg-float" :
				case "neg-double" : 
				case "int-to-long" :
				case "int-to-float" :
				case "int-to-double" :
				case "long-to-int" :
				case "long-to-float" :
				case "long-to-double" :
				case "float-to-int" :
				case "float-to-long" :
				case "float-to-double" :
				case "double-to-int" :
				case "double-to-long" :
				case "double-to-float" :
				case "int-to-byte" :
				case "int-to-char" :
				case "int-to-short" :   {	nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
											nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));	
											nodes.get(i).addVarUse(nodes.get(i).getParameters().get(1));} break;
				case "add-int" :
				case "sub-int" :
				case "mul-int" :
				case "div-int" :
				case "rem-int" :
				case "and-int" :
				case "or-int" :
				case "xor-int" :
				case "shl-int" :
				case "shr-int" :
				case "ushr-int" :
				case "add-long" :
				case "sub-long" :
				case "mul-long" :
				case "div-long" :
				case "rem-long" :
				case "and-long" :
				case "or-long" :
				case "xor-long" :
				case "shl-long" :
				case "shr-long" :
				case "ushr-long" :
				case "add-float" :
				case "sub-float" :
				case "mul-float" :
				case "div-float" :
				case "rem-float" :
				case "add-double" :
				case "sub-double" :
				case "mul-double" :
				case "div-double" :
				case "rem-double" :  {	nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
										nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));
										nodes.get(i).addVarUse(nodes.get(i).getParameters().get(1));
										nodes.get(i).addVarUse(nodes.get(i).getParameters().get(2));} break;
				case "add-int/2addr" :
				case "sub-int/2addr" :
				case "mul-int/2addr" :
				case "div-int/2addr" :
				case "rem-int/2addr" :
				case "and-int/2addr" :
				case "or-int/2addr" :
				case "xor-int/2addr" :
				case "shl-int/2addr" :
				case "shr-int/2addr" :
				case "ushr-int/2addr" :
				case "add-long/2addr" :
				case "sub-long/2addr" :
				case "mul-long/2addr" :
				case "div-long/2addr" :
				case "rem-long/2addr" :
				case "and-long/2addr" :
				case "or-long/2addr" :
				case "xor-long/2addr" :
				case "shl-long/2addr" :
				case "shr-long/2addr" :
				case "ushr-long/2addr" :
				case "add-float/2addr" :
				case "sub-float/2addr" :
				case "mul-float/2addr" :
				case "div-float/2addr" :
				case "rem-float/2addr" :
				case "add-double/2addr" :
				case "sub-double/2addr" :
				case "mul-double/2addr" :
				case "div-double/2addr" :
				case "rem-double/2addr" :  {	nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
												nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));
												nodes.get(i).addVarUse(nodes.get(i).getParameters().get(0));
												nodes.get(i).addVarUse(nodes.get(i).getParameters().get(1));} break;
				case "add-int/lit16" :
				case "rsub-int" :
				case "mul-int/lit16" :
				case "div-int/lit16" :
				case "rem-int/lit16" :
				case "and-int/lit16" :
				case "or-int/lit16" :
				case "xor-int/lit16" : 
				case "add-int/lit8" :
				case "rsub-int/lit8" :
				case "mul-int/lit8" :
				case "div-int/lit8" :
				case "rem-int/lit8" :
				case "and-int/lit8" :
				case "or-int/lit8" :
				case "xor-int/lit8" :
				case "shl-int/lit8" :
				case "shr-int/lit8" :
				case "ushr-int/lit8" : {	nodes.get(i).addGen(nodes.get(i),nodes.get(i).getParameters().get(0));
											nodes.get(i).addKill(nodes.get(i),nodes.get(i).getParameters().get(0));
											nodes.get(i).addVarUse(nodes.get(i).getParameters().get(1)); };  break; 
			}
		}
	}

	/**
	 * Updates the list of variables that are killed by each node
	 */
	public void makeKill(){
		String aux;
		LinkedList<Pair<Node, String>> otherOneKills;
		for (int i = 0; i<nodes.size(); i++){
			for (int j = 0; j<nodes.size(); j++){
				if (i != j){
					aux = nodes.get(i).getGenVariable();
					if (aux != null){
						otherOneKills = nodes.get(j).getKills();
						for(int k=0; k<otherOneKills.size(); k++){
							if((otherOneKills.get(k).getSecond().equalsIgnoreCase(aux)&&(!(nodes.get(i).isAlreadyInKill(otherOneKills.get(k)))))){
								nodes.get(i).addKill(otherOneKills.get(k).getFirst(), otherOneKills.get(k).getSecond());
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Calculates the IN and OUT sets for achievable definitions
	 */
	public void reachingDefs(){
		LinkedList<Pair<Node, String>> inLessKill; 
		for(int i=0; i<nodes.size(); i++){ // Step 1 in the algorithm	
			for(int j=0; j<nodes.get(i).getGen().size(); j++){ // Step 1 in the algorithm	
				nodes.get(i).addOut(nodes.get(i).getGen().get(j)); // Step 1 in the algorithm	
			} // Step 1 in the algorithm	
		} // Step 1 in the algorithm	
		for(int k=0; k<nodes.size(); k++){
			for(int i=0; i<nodes.size(); i++){ // Step 5 in the algorithm	
				for(int j=0; j<nodes.get(i).getPredecessors().size(); j++){ // Step 5 in the algorithm	
					if(!(nodes.get(i).getPredecessors().get(j).getOut().isEmpty())){
						nodes.get(i).unionIN(nodes.get(i).getPredecessors().get(j).getOut());
					}
				}
				inLessKill = nodes.get(i).difference();
				nodes.get(i).union(nodes.get(i).getGen());
				nodes.get(i).union(inLessKill);
			}
		}
	}
	
	/**
	 * Calculates the definition-use pairs.
	 * A pair definition-use of the variable v is an ordered pair (D, U) 
	 * where D is a statement containing a definition of v and U is a sentence 
	 * that contains a use of v - so that there is an internal path in the CFG,
	 * D to U in which, all along 'D' is not dead
	 */
	public void computeDefUsePairs(){
		LinkedList<String> usedVars;
		LinkedList<Pair<Node, String>> in;
		Pair<Node,String> first,second;
		for(int i=0; i<nodes.size(); i++){
			usedVars = nodes.get(i).getVarUse();
			in = nodes.get(i).getIn();
			for(int j=0; j<usedVars.size(); j++){
				for(int k=0; k<in.size(); k++){
					if(usedVars.get(j).equalsIgnoreCase(in.get(k).getSecond())) {// Compare uses with reaching defs from IN list
						first = in.get(k);
						second = new Pair<Node, String>(nodes.get(i), usedVars.get(j));
						nodes.get(i).addUse(first, second);
					}	
				}
			}
		}
	}
	
	/**
	 * Adds two new nodes: 'Entry' and 'Exit'
	 */
	public void addEntryAndExit(){
		Node entry = new Node("entry");
		Node exit = new Node("exit");
		entry.addSuccesor(nodes.get(0));
		nodes.get(0).addPredecessor(entry);
		for(int i=0; i<nodes.size(); i++){
			if (nodes.get(i).getSuccessors().isEmpty()){
				exit.addPredecessor(nodes.get(i));
				nodes.get(i).addSuccesor(exit);
			}
		}
		nodes.add(entry); 
		nodes.add(exit);	
	}
	
	/**
	 * Adds a new node: 'Start'
	 */
	public void addStart(){
		Node start = new Node("start");		
		start.addSuccesor(getNodeById("entry"));
		start.addSuccesor(getNodeById("exit"));
		start.setAction("nop");
		nodes.add(start);
		getNodeById("entry").addPredecessor(start);
		getNodeById("exit").addPredecessor(start);
	}
	
	/**
	 *  Filters the  list of nodes.
	 *  @return list of all nodes, except 'entry' node
	 */
	public LinkedList<Node> nodesMinusEntry(){
		LinkedList<Node> res = new LinkedList<Node>();
		for (int i = 0; i < nodes.size(); i++){
			if (!(nodes.get(i).getID().equalsIgnoreCase("entry"))){
				res.add(nodes.get(i));
			}
		}
		return res;
	}
	
	/**
	 *  Filters the  list of nodes.
	 *  @return list of all nodes, except 'exit' node
	 */
	public LinkedList<Node> nodesMinusExit(){
		LinkedList<Node> res = new LinkedList<Node>();
		for (int i = 0; i < nodes.size(); i++){
			if (!(nodes.get(i).getID().equalsIgnoreCase("exit"))){
				res.add(nodes.get(i));
			}
		}
		return res;
	}
	
	/**
	 *  Filters the  list of nodes.
	 *  @return list of all nodes, except 'start' node
	 */
	public LinkedList<Node> nodesMinusStart(){
		LinkedList<Node> res = new LinkedList<Node>();
		for (int i = 0; i < nodes.size(); i++){
			if (!(nodes.get(i).getID().equalsIgnoreCase("start"))){
				res.add(nodes.get(i));
			}
		}
		return res;
	}
	
	/**
	 * Computes D(n): the set of nodes which dominate n, for each node n within the Graph.
	 */
	public void computeDom(){
		LinkedList<Node> aux = new LinkedList<Node>();
		LinkedList<Node> nodesMinusExit = nodesMinusExit();
		aux.add(getNodeById("exit"));
		getNodeById("exit").setDom(aux); // Step 1 in the algorithm
		for (int i = 0; i<nodesMinusExit.size(); i++){
			nodesMinusExit.get(i).setDom(nodes); // Step 2 in the algorithm
		}	
		for (int j = 0 ; j<nodes.size(); j++){ // Step 3 in the algorithm
			for(int k = 0 ; k<nodesMinusExit.size(); k++){	// Step 4 in the algorithm
					nodesMinusExit.get(k).setDom(nodesMinusExit.get(k).unionDom(nodesMinusExit.get(k).intersectionDom())); // Step 5 in the algorithm 
				}
		}
	}
	
	/**
	 * Given a node, searches for it within the list of nodes
	 * @return position of the node within the list of nodes. If not found, returns -1 
	 */
	public int getNodePos(Node n){  
		int pos = -1;              
		for(int i=0; i<nodes.size(); i++){
			if(nodes.get(i).getID().equalsIgnoreCase(n.getID()))
				pos = i;
		}
		return pos;
	}
	
	/**
	 * Computes postdominator tree
	 * @return postdominator tree made up of nodes 
	 */
	public Tree<Node> buildTree(){
		for(int j=0; j<nodes.size(); j++){
			nodes.get(j).undominate(nodes.get(j));	// Step 3 in the algorithm.
		}											// it's done before assembling the lists of nodes of the tree, to have the updated information
		treeNodes = new LinkedList<TreeNode<Node>>();	// All nodes are going to be loaded here as nodes of the tree  
														// and they will be found in the same position on both lists. 
														// e.g. If a node n is in the position index on the list named 'nodes'
														// it is in the same position on the list named 'treeNodes'	
		for(int i=0; i<nodes.size(); i++){
			treeNodes.add(new TreeNode<Node>(nodes.get(i)));
		}
		LinkedList<Node> queueTree = new LinkedList<Node>(); // queue used in the algorithm
		queueTree.add(getNodeById("exit")); // Step 2 in the algorithm
		Node m; // the next node on Q (remove it from Q)
		while (!(queueTree.isEmpty())){ // Step 4 in the algorithm
			m = queueTree.poll(); // Step 5 in the algorithm
			for(int k=0; k<nodes.size(); k++){ // Step 6 in the algorithm
				if(!(nodes.get(k).dominateIsEmpty())){ // Step 6 in the algorithm
					if(nodes.get(k).dominateContains(m)){ // Step 7 in the algorithm
						nodes.get(k).undominate(m); // Step 8 in the algorithm
						if(nodes.get(k).dominateIsEmpty()){ // Step 9 in the algorithm
							treeNodes.get(getNodePos(m)).addChild(treeNodes.get(k)); // Step 10 in the algorithm
							queueTree.add(nodes.get(k)); // Step 11 in the algorithm
						}
					}
				}
			}
		}
		dominatorTree = new Tree<Node>(treeNodes.get(getNodePos(getNodeById("exit"))));
		return dominatorTree;
	}
	
	/**
	 * Computes dominator tree
	 * @return dominator tree made up of nodes 
	 */
	public Tree<Node> buildTreeFromStart(){
		for(int j=0; j<nodes.size(); j++){
			nodes.get(j).undominate(nodes.get(j)); // Step 3 in the algorithm.
		}										   // it's done before assembling the lists of nodes of the tree, to have the updated information
		treeNodes = new LinkedList<TreeNode<Node>>();	// All nodes are going to be loaded here as nodes of the tree 
														// and they will be found in the same position on both lists. 
														// e.g. If a node n is in the position index on the list named 'nodes'
														// it is in the same position on the list named 'treeNodes'	
		for(int i=0; i<nodes.size(); i++){
			treeNodes.add(new TreeNode<Node>(nodes.get(i)));
		}
		LinkedList<Node> queueTree = new LinkedList<Node>(); // queue used in the algorithm
		queueTree.add(getNodeById("start")); // Step 2 in the algorithm
		Node m; // the next node on Q (remove it from Q)
		while (!(queueTree.isEmpty())){ // Step 4 in the algorithm
			m = queueTree.poll(); // Step 5 in the algorithm
			for(int k=0; k<nodes.size(); k++){ // Step 6 in the algorithm
				if(!(nodes.get(k).dominateIsEmpty())){ // Step 6 in the algorithm
					if(nodes.get(k).dominateContains(m)){ // Step 7 in the algorithm
						nodes.get(k).undominate(m); // Step 8 in the algorithm
						if(nodes.get(k).dominateIsEmpty()){ // Step 9 in the algorithm
							treeNodes.get(getNodePos(m)).addChild(treeNodes.get(k)); // Step 10 in the algorithm
							queueTree.add(nodes.get(k)); // Step 11 in the algorithm
						}
					}
				}
			}
		}
		dominatorTree = new Tree<Node>(treeNodes.get(getNodePos(getNodeById("start"))));
		return dominatorTree;
	}
	
	/**
	 * Creates an output file called 'PostdominatorTree.dot' where the PDT is represented 
	 */
	public void dominatorTreeToDot(){
		String resDot = "digraph Tree {\n";
		try{
			fileDotDomTree = new FileWriter(".output_intermediate/PostdominatorTree.dot");
			fwDom = new PrintWriter(fileDotDomTree);
			ArrayList<TreeNode<Node>> preOrderDot = dominatorTree.getPreOrderTraversal();
			String root = preOrderDot.get(0).getData().toString();
			resDot = resDot.concat("\""+root+"\"[shape=polygon,sides=9,peripheries=2,style=filled,fillcolor=chartreuse3];");
			resDot = resDot.concat("\n");
			for(int i=1; i<preOrderDot.size(); i++){
				String first = preOrderDot.get(i).getData().toString();
				String second = preOrderDot.get(i).getParent().getData().toString();
				resDot = resDot.concat("\""+second+"\" [label=\""+preOrderDot.get(i).getParent().getData().toString()+"\"];");
				resDot = resDot.concat("\""+first+"\" [label=\""+preOrderDot.get(i).getData().toString()+"\",style=filled,fillcolor=mediumturquoise]; ");
				resDot = resDot.concat("\n");
				resDot = resDot.concat("\""+second+"\"");
				resDot = resDot.concat(" -> ");
				resDot = resDot.concat("\""+first+"\"");
				resDot = resDot.concat(" [style=bold];\n");
			}
			resDot = resDot.concat("}");
			fwDom.println(resDot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fwDom.close();
	}
	
	/**
	 * Computes Control Dependence Graph 
	 * @return Control Dependence Graph as a tree made up of nodes 
	 */
	public Tree<Node> buildCDG(){
		LinkedList<TreeNode<Node>> treeNodes = new LinkedList<TreeNode<Node>>(); 
		// All nodes are going to be loaded here as nodes of the tree 
		// and they will be found in the same position on both lists. 
		// e.g. If a node n is in the position index on the list named 'nodes'
		// it is in the same position on the list named 'treeNodes'	
		for(int i=0; i<nodes.size(); i++){
			treeNodes.add(new TreeNode<Node>(nodes.get(i)));
		}
		for(int i=0; i<nodes.size(); i++){
			try{
				for(int j=0; j<nodes.size(); j++){
					if(nodes.get(j).getLCA().getID().equalsIgnoreCase(nodes.get(i).getID()))
						treeNodes.get(i).addChild(treeNodes.get(j));
				}
				if(nodes.get(i).getPointsItself()){
					treeNodes.get(i).addChild(treeNodes.get(i));
				}	
			}
			catch (NullPointerException e){}
		}
		Tree<Node> CDG = new Tree<Node>(treeNodes.get(getNodePos(getNodeById("start"))));
		return CDG;
	}
	
	/**
	 * Checks if a string passed as a parameter is equal to a member of a string list also passed as a parameter
	 * * @return <ul>
	 *          <li>true: string 'var' is equal to a member within list named 'vars'</li>
	 *          <li>false: string 'var' is not equal to a member within list named 'vars'</li>
	 *          </ul>
	 */
	public boolean exists(ArrayList<String> vars, String var){
		boolean res = false;
		for(int i=0; i<vars.size(); i++){
			if(vars.get(i).equalsIgnoreCase(var))
				res = true;
		}
		return res;
	}
	
	/**
	 * Creates an output file called 'PDG.dot' where the PDG(Program Dependence Graph) is represented 
	 */
	public void buildPDG(){
		// (*) The predecessors involved in the algorithm are in LCA (dependence control) and USE (data dependence)
		// (*) IMPORTANT: all predecessors from the data dependence subgraph in the PDG must be used.
		// (that is to say, the first node in the USE list's pairs, the first element within the first pair)
		ArrayList<String> vars = new ArrayList<String>();
		LinkedList<Pair<Pair<Node, String>, Pair<Node, String>>> use;
		String var = "";
		for(int i=0; i<nodes.size(); i++){
			use = nodes.get(i).getUse();
			for(int j=0; j<use.size(); j++){
				var = use.get(j).getFirst().getSecond(); 
				if(!(exists(vars,var))) // if it is not on the list, it is added
					vars.add(var);
				var = use.get(j).getSecond().getSecond(); // just in case, this additional check is made
				if(!(exists(vars,var))) // if it is not on the list, it is added
					vars.add(var);
			}
		}
		ArrayList<String> colours = new ArrayList<String>();
		colours.add("dodgerblue");
		colours.add("cornsilk4");
		colours.add("green4");
		colours.add("darkorchid4");
		colours.add("gold1");
		colours.add("darkseagreen4");
		colours.add("deeppink");
		colours.add("chartreuse2");
		colours.add("darkgoldenrod3");
		colours.add("firebrick1");
		colours.add("deeppink4");
		colours.add("brown2");
		colours.add("goldenrod4");
		colours.add("chocolate2");
		colours.add("cyan");
		colours.add("forestgreen");
		ArrayList<TreeNode<Node>> preOrder = buildCDG().getPreOrderTraversal();
		String resDot = "digraph Tree {\n";
		try
		{
			fileDotPDG = new FileWriter(".output_intermediate/PDG.dot");
			fwPDG = new PrintWriter(fileDotPDG);
			ArrayList<TreeNode<Node>> preOrderDot = buildCDG().getPreOrderTraversal();
			String root = preOrderDot.get(0).getData().toString();
			resDot = resDot.concat("\""+root+"\"[shape=box,peripheries=2,style=filled,fillcolor=antiquewhite4,fontcolor=black];");
			resDot = resDot.concat("\n");
			for(int i=1; i<preOrderDot.size(); i++){
				String first = preOrderDot.get(i).getData().toString();
				String second = preOrderDot.get(i).getParent().getData().toString();
				resDot = resDot.concat("\""+second+"\" [label=\""+preOrderDot.get(i).getParent().getData().toString()+"\"];\n");
				resDot = resDot.concat("\""+first+"\" [label=\""+preOrderDot.get(i).getData().toString()+"\",style=filled,fillcolor=springgreen,peripheries=2]; ");
				resDot = resDot.concat("\n");
				resDot = resDot.concat("\""+second+"\"");
				resDot = resDot.concat(" -> ");
				resDot = resDot.concat("\""+first+"\"");
				resDot = resDot.concat(" [style=bold,color=navy]");
				resDot = resDot.concat(";\n");
				if(preOrder.get(i).getData().getPointsItself()){
					resDot = resDot.concat("\""+first+"\" [label=\""+preOrderDot.get(i).getData().toString()+"\",style=filled,fillcolor=springgreen,peripheries=2];\n");
					resDot = resDot.concat("\""+first+"\" [label=\""+preOrderDot.get(i).getData().toString()+"\"];");
					resDot = resDot.concat("\n");
					resDot = resDot.concat("\""+first+"\"");
					resDot = resDot.concat(" -> ");
					resDot = resDot.concat("\""+first+"\"");
					resDot = resDot.concat(" [style=bold,color=navy]");
					resDot = resDot.concat(";\n");
				}
			}
			Node n;
			for(int i=0; i<nodes.size(); i++){
				use = nodes.get(i).getUse();
				for(int k=0; k<vars.size(); k++){	
					for(int j=0; j<use.size(); j++){
						var = use.get(j).getFirst().getSecond();
						n = use.get(j).getFirst().getFirst();
						if((var.equalsIgnoreCase(vars.get(k)))&&(!(n.toString().equalsIgnoreCase("start")))){
							resDot = resDot.concat("\""+n.toString()+"\"");
							resDot = resDot.concat(" -> ");
							resDot = resDot.concat("\""+nodes.get(i).toString()+"\"");
							// Color is consistent with the position of the variable within the arraylist VARS, with k
							resDot = resDot.concat(" [color="+colours.get(k)+",label=\""+var+ "\",fontcolor="+colours.get(k)+",style=dashed]");
							resDot = resDot.concat(";\n");
						}
					}	
				}
			}
			resDot = resDot.concat("}");
			fwPDG.println(resDot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fwPDG.close();
	}
	
	/**
	 * Creates an output file called 'CDG.dot' where the CDG(Control Dependence Graph) is represented 
	 */
	public void CDGTreeToDot(){
		// CDG will be shown only with node's ID because it is too wide and DOT cannot scale it
		ArrayList<TreeNode<Node>> preOrder = buildCDG().getPreOrderTraversal();
		String resDot = "digraph Tree {\n";
		try
		{
			fileDotCDG = new FileWriter(".output_intermediate/CDG.dot");
			fwCDG = new PrintWriter(fileDotCDG);
			ArrayList<TreeNode<Node>> preOrderDot = buildCDG().getPreOrderTraversal();
			String root = preOrderDot.get(0).getData().toString();
			resDot = resDot.concat("\""+root+"\"[shape=box,peripheries=2,style=filled,fillcolor=antiquewhite4,fontcolor=black];");
			resDot = resDot.concat("\n");
			for(int i=1; i<preOrderDot.size(); i++){
				String first = preOrderDot.get(i).getData().toString();
				String second = preOrderDot.get(i).getParent().getData().toString();
				resDot = resDot.concat("\""+second+"\" [label=\""+preOrderDot.get(i).getParent().getData().toString()+"\"];");
				resDot = resDot.concat("\""+first+"\" [label=\""+preOrderDot.get(i).getData().toString()+"\",style=filled,fillcolor=springgreen,peripheries=2]; ");
				resDot = resDot.concat("\n");
				resDot = resDot.concat("\""+second+"\"");
				resDot = resDot.concat(" -> ");
				resDot = resDot.concat("\""+first+"\"");
				resDot = resDot.concat(" [style=bold,color=navy]");
				resDot = resDot.concat(";\n");
				if(preOrder.get(i).getData().getPointsItself()){
					resDot = resDot.concat("\""+first+"\" [label=\""+preOrderDot.get(i).getData().toString()+"\",style=filled,fillcolor=springgreen,peripheries=2]; ");
					resDot = resDot.concat("\""+first+"\" [label=\""+preOrderDot.get(i).getData().toString()+"\"];");
					resDot = resDot.concat("\n");
					resDot = resDot.concat("\""+first+"\"");
					resDot = resDot.concat(" -> ");
					resDot = resDot.concat("\""+first+"\"");
					resDot = resDot.concat(" [style=bold,color=navy]");
					resDot = resDot.concat(";\n");
				}
			}
			resDot = resDot.concat("}");
			fwCDG.println(resDot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fwCDG.close();
	}
	
	/**
	 * Checks if a node passed as a parameter's ID is equal to a member's ID within a node list also passed as a parameter
	 * @return <ul>
	 *          <li>true: node N's ID is equal to a member's ID within list named 'list'</li>
	 *          <li>false: node N's ID is not equal to a member's ID within list named 'list'</li>
	 *          </ul>
	 */
	private boolean exists(LinkedList<Node> list, Node n){
		boolean res = false;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equalsIgnoreCase(n.getID()))
				res = true;
		} 
		return res; 
	}
	
	/**
	 * Given a node's ID and a list of variables, gets the subgraph of resulting slice represented by a list of nodes
	 * @return list of nodes of resulting slice
	 */
	// 'START' cannot be node's ID
	public LinkedList<Node> slicing(String id, LinkedList<String> var){
		// (*) The predecessors involved in the algorithm are in LCA (dependence control) and USE (data dependence)
		// (*) Check that they are not repeated in the set SLICE and WORKLIST to add it 
		//  *visited* field is only checked so as to not add it more than once in set WORKLIST 
		// (*) IMPORTANT: all predecessors from the data dependence subgraph in the PDG must be used.
		// (that is to say, the first node in the USE list's pairs, the first element within the first pair)
		// (*) START does not enter to any of the two lists
		LinkedList<Node> worklist = new LinkedList<Node>();
		LinkedList<Node> slice = new LinkedList<Node>();
		LinkedList <Pair<Pair<Node,String>,Pair<Node,String>>> use; 
		// We add initial node
		Node n,lca,node; // n will be current node all along the algorithm
						 // lca will be current node's least common ancestor 
						 // node will be USE list's current node
		if (!(id.equalsIgnoreCase("start"))){ // if initial node's ID is equal to 'start', nothing is done then
			worklist.add(getNodeById(id));
			while(!(worklist.isEmpty())){
				n = worklist.poll(); 
				n.setVisited(true);
				// add N's unvisited control dependence and data dependence predecessors to worklist and slice
				try{ // LCA could be NULL
					lca = n.getLCA();
					// if worklist does not have the element && it has not been visited yet && it is not equal to 'start', it is added
					if (((!(exists(worklist,lca)))&&(!(lca.getVisited())))&&(!(lca.getID().equalsIgnoreCase("start")))) 
						worklist.add(lca);
					// if slice does not have the element && it is not equal to 'start', it is added
					if((!(exists(slice,lca)))&&(!(lca.getID().equalsIgnoreCase("start"))))  
						slice.add(lca);
				} catch (NullPointerException e) {}
				use = n.getUse();
				for(int i=0; i<use.size(); i++){
					node = use.get(i).getFirst().getFirst();
					// if worklist does not have the element && it has not been visited yet && it is not equal to 'start', it is added
					if(((!(exists(worklist,node)))&&(!(node.getVisited())))&&(!(node.getID().equalsIgnoreCase("start")))) 
						worklist.add(node);
					// if slice does not have the element && it is not equal to 'start', it is added
					if((!(exists(slice,node)))&&(!(node.getID().equalsIgnoreCase("start")))) 
						slice.add(node);
				}
				if(!(exists(slice,n))) 
						slice.add(n);
			}
		}
		return slice;
	}
	
	/**
	 * Computes Control Flow Graph
	 * @return Control Flow Graph as a tree made up of nodes 
	 */
	public Tree<Node> buildCFG(){
		LinkedList<TreeNode<Node>> treeNodes = new LinkedList<TreeNode<Node>>(); 
		// All nodes are going to be loaded here as nodes of the tree 
		// and they will be found in the same position on both lists. 
		// e.g. If a node n is in the position index on the list named 'nodes'
		// it is in the same position on the list named 'treeNodes'
		for(int i=0; i<nodes.size(); i++){
			treeNodes.add(new TreeNode<Node>(nodes.get(i)));
		}
		for(int i=0; i<nodes.size(); i++){
			try{
				for(int j=0; j<nodes.size(); j++){
					if(nodes.get(i).isSuccesor(nodes.get(j))){ // if it is a successor, it is a child
						if(!(nodes.get(i).getID().equalsIgnoreCase("exit"))&&!(nodes.get(j).getID().equalsIgnoreCase("exit"))){ 
							// if none is 'EXIT'
							if(isLoop(nodes.get(i),nodes.get(j))){
								nodes.get(i).setIsEndLoop(true);
								nodes.get(i).setBeginLoop(nodes.get(j));
								nodes.get(j).setIsBeginLoop(true);
								// Successor at position 1 within successors' array (nodes.get(j)) must be
								// set as a child of current node (they are always two, because it is either a loop 
								// sentence or an IF sentence, and that is where you point the bifurcation)
								if(nodes.get(j).getSuccessors().size()==1)
									treeNodes.get(i).addChild(treeNodes.get(getNodePos(nodes.get(j).getSuccessors().get(0))));
								else // they are two successors
									treeNodes.get(i).addChild(treeNodes.get(getNodePos(nodes.get(j).getSuccessors().get(1))));
							} else {
								treeNodes.get(i).addChild(treeNodes.get(j));
							}
						}
					}
				}
			}catch (NullPointerException e){}
		}
		// Control Flow Graph always starts at node with id: 0
		Tree<Node> CFG = new Tree<Node>(treeNodes.get(getNodePos(getNodeById("0"))));
		return CFG;
	}
	
	/**
	 * Given two nodes, checks if the hexadecimal number that represents Node b's ID  
	 * is smaller than Node a's ID in hexadecimal. If this happens, there is a loop. 
	 * @return <ul>
	 *          <li>true: the hexadecimal number that represents Node b's ID is smaller than Node a's ID in hexadecimal</li>
	 *          <li>false: the hexadecimal number that represents Node b's ID is equal to or higher than Node a's ID in hexadecimal</li>
	 *          </ul>
	 */
	private boolean isLower(Node a, Node b){
		int max = nodes.size()*200; // the maximum amount to be cycled 
		int index_a = 0;
		int index_b = 0;
		for(int i = 0; i<max; i++){
			if(Integer.toHexString(i).equalsIgnoreCase(a.getID()))
				index_a = i;
			if(Integer.toHexString(i).equalsIgnoreCase(b.getID()))
				index_b = i;
		}
		return (index_b<index_a);
	}
	
	/**
	 * Checks if there is a loop between two given nodes 
	 * @return <ul>
	 *          <li>true: there is a loop between node 'a' and node 'b'</li>
	 *          <li>false: there is not a loop between node 'a' and node 'b'</li>
	 *          </ul>
	 */
	private boolean isLoop(Node a, Node b){
		boolean res = ((b.getAction().equalsIgnoreCase("if-eq"))||((b.getAction().equalsIgnoreCase("if-lt")))||(b.getAction().equalsIgnoreCase("if-ne")));
		res = res || ((b.getAction().equalsIgnoreCase("if-ge"))||((b.getAction().equalsIgnoreCase("if-gt")))||(b.getAction().equalsIgnoreCase("if-le")));
		res = res || ((b.getAction().equalsIgnoreCase("if-eqz"))||((b.getAction().equalsIgnoreCase("if-nez")))||(b.getAction().equalsIgnoreCase("if-ltz")));
		res = res || ((b.getAction().equalsIgnoreCase("if-gez"))||((b.getAction().equalsIgnoreCase("if-gtz")))||(b.getAction().equalsIgnoreCase("if-lez")));
		res = res && ((a.getAction().equalsIgnoreCase("goto"))||(a.getAction().equalsIgnoreCase("goto/16"))||(a.getAction().equalsIgnoreCase("goto/32")));
		res = res && isLower(a,b);
		return res;
	}
}