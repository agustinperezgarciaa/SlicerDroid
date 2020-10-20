import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

/*
 	Graph is part of SlicerDroid.

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
 * Class Graph
 *
 * Contains all the information concerning to the graph
 *
 * @author Agustin & Facundo
 * @version 1.0
 */
public class Graph {
	
	// nodes from this graph
	private LinkedList<Node> nodes;
	private Tree<Node> tn;
	private LinkedList<Vertex> vertexes;
	LinkedList<Vertex> s_list;
	LinkedList<Node> l_list;
	Slicer sl;
	// to create file CFGDot
	static FileWriter fwCFG = null;
	static PrintWriter pwCFG = null;
	// to create file Slice
	static FileWriter fwSlice = null;
	static PrintWriter pwSlice = null;
	// to store all of the nodes involved in Slicing process
	private LinkedList<Node> slice_nodes;
	private String slicing_point;	// it will be passed as a parameter a hexadecimal number representing the  
									// line number that will be the starting point for slicing
	private LinkedList<String> list;	// list of variables involved in Slicing process
	
	/**
	 * Constructor by default
	 */
	public Graph(){
		vertexes = new LinkedList<Vertex>();
		s_list = new LinkedList<Vertex>();
		l_list = new LinkedList<Node>();
		tn = null;
		nodes = new LinkedList<Node>();
		sl = new Slicer();
		slice_nodes = new LinkedList<Node>();
		this.slicing_point = "0";
		this.list = new LinkedList<String>();
	}
	
	/**
	 * Constructor with 2 parameters
	 * @param slicing_point a string which indicates the point where slicing should be started
	 * @param list list of variables involved in slicing process
	 */
	public Graph(String slicing_point, LinkedList<String> list){
		vertexes = new LinkedList<Vertex>();
		s_list = new LinkedList<Vertex>();
		l_list = new LinkedList<Node>();
		tn = null;
		nodes = new LinkedList<Node>();
		sl = new Slicer();
		slice_nodes = new LinkedList<Node>();
		this.slicing_point = slicing_point;
		this.list = list;
	}
	
	/**
	 * Adds a new vertex into graph's list of vertexes 
	 * @param vertex new vertex to be added
	 */
	public void addVertex(Vertex vertex){
		this.vertexes.add(vertex);
	}
	
	/**
	 * Checks if graph's list of vertexes is empty
	 * @return <ul>
	 *          <li>true: graph's list of vertexes is empty</li>
	 *          <li>false: graph's list of vertexes is not empty</li>
	 *          </ul>
	 */
	public boolean isEmpty(){
		return vertexes.isEmpty();
	}
	
	/**
	 * Outputs this graph's vertexes
	 * @return linked list made of vertexes
	 */
	public LinkedList<Vertex> getVertexes(){
		return this.vertexes;
	}
	
	/**
	 * Builds graph and calls all necessary methods to give user what he wants 
	 * @return a string representing the slicing point, in case that this line number does not exist and user has to be warned
	 */
	public String makeGraph(){
		String res = "";
		Slicer sl = new Slicer();
		String block8 = "\u2588";
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print("\033[32m"+block8+block8+block8+block8+block8+block8);	// \033[32m green
		nodes =	Slicer.parse();
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print(block8+block8+block8+block8+block8+block8);
		sl.makeGenAndKill();
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print(block8+block8+block8+block8+block8+block8);
		sl.makeKill();
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print(block8+block8+block8+block8+block8+block8);
		sl.reachingDefs();
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print("\033[33m"+block8+block8+block8+block8+block8+block8);	// \033[33m yellow
		sl.computeDefUsePairs();
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print(block8+block8+block8+block8+block8+block8);
		sl.addEntryAndExit();
		sl.addStart();
		nodes = sl.cleanPayload();	// this is the list we need, without payload
		sl.computeDom();
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print(block8+block8+block8+block8+block8+block8);
		tn = sl.buildTree();
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print(block8+block8+block8+block8+block8+block8);
		sl.computeDom();
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print(block8+block8+block8+block8+block8+block8);
		sl.dominatorTreeToDot();
		LinkedList<Node> successors;
		for(int i=0; i<nodes.size(); i++){
			successors = nodes.get(i).getSuccessors(); 
			for(int j=0; j<successors.size(); j++){
				vertexes.add(new Vertex(nodes.get(i),successors.get(j)));
			}	
		}
		// S generator
		LinkedList<Vertex> resS = new LinkedList<Vertex>();
		TreeNode<Node> raiz = tn.getRoot();
		for (int i = 0; i<vertexes.size();i++){
			if ((!(isAncestor(vertexes.get(i)))) && (!(vertexes.get(i).getSecond().getID().equalsIgnoreCase(raiz.getData().getID())))){
				resS.add(vertexes.get(i));
			}
		}
		s_list = resS;
		for(int i =0; i<s_list.size();i++){
			l_list.add(Slicer.getNodeById(leastCommonAncestor(s_list.get(i))));
		}
		for(int i =0; i<s_list.size();i++){
			markLCA(s_list.get(i),l_list.get(i));
		}
		changeExitForStart();
		// CDG will be shown only with node's ID because it is too wide and DOT cannot scale it
		sl.CDGTreeToDot();
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print("\033[31m"+block8+block8+block8+block8+block8+block8);	// \033[31m red 
		// PDG will be shown only with node's ID because it is too wide and DOT cannot scale it
		sl.buildPDG();
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print(block8+block8+block8+block8+block8+block8);
		if(Slicer.getNodeById(slicing_point)==null)
			res = slicing_point; // If this line is returned, it means that it does not exist and user must be warned in main class
		else
			slice_nodes = sl.slicing(slicing_point,list);
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.print(block8+block8+block8+block8+block8+block8);
		// this procedure is repeated for each node within slice_nodes
		String paths = "";
		ArrayList<Pair<Node, Node>> pairs = new ArrayList<Pair<Node,Node>>();
		ArrayList<Pair<Node, Node>> allPairs = new ArrayList<Pair<Node,Node>>();
		resetVisited();
		for(int i=0; i<slice_nodes.size(); i++){
			paths += dfs(slice_nodes.get(i),"",0);
			resetVisited();
			// added to look for alternative paths otherwise
			paths += dfs2(slice_nodes.get(i),"",0);
			resetVisited();
			writePath(paths);
			pairs = searchForPairsInCFG(slice_nodes.get(i),readAllPaths());
			for(int j=0; j<pairs.size(); j++){
				if(!(isAlreadyIn(allPairs, pairs.get(j))))
					allPairs.add(pairs.get(j));
			}
			paths = "";
		}
		Slice(allPairs);
		// Shows six unicode characters on screen after making an important step within the program, so the user sees the progress when running SlicerDroid
		System.out.println(block8+block8+block8+block8+block8+block8+"\033[0m");	// \033[0m clear all formatting
		return res; // end MakeGraph()	
	}
	
	/**
	 * Creates file named '.paths.txt' and writes string 'path' into it
	 * @param path a string containing all paths within this graph
	 */
	public void writePath(String path){
		try {
			FileWriter fw = new FileWriter(".paths.txt");
			PrintWriter pw = new PrintWriter(fw);
			pw.print(path);
			pw.close();
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Given a pair and a list of pairs, it returns true if the pair exists in the list
	 * @param list an array list made of pairs containing nodes
	 * @param p a pair of nodes
	 * @return <ul>
	 *          <li>true: pair 'p' exists within list named 'list'</li>
	 *          <li>false: pair 'p' does not exist within list named 'list'</li>
	 *          </ul>
	 */
	private boolean isAlreadyIn(ArrayList<Pair<Node, Node>> list, Pair<Node,Node> p){
		boolean res = false;
		Pair<Node,Node> pair;
		for(int i=0; i<list.size(); i++){
			pair = list.get(i);
			if((p.getFirst().getID().equalsIgnoreCase(pair.getFirst().getID())) && ((p.getSecond().getID().equalsIgnoreCase(pair.getSecond().getID()))))
				res = true;
		}
		return res;
	}
	
	/**
	 * Builds a DOT file called 'Slice.dot' within .output_intermediate folder representing the output sliced graph 
	 * @param pairs an array list made of pairs containing nodes
	 */
	public void Slice(ArrayList<Pair<Node,Node>> pairs){
		String Slice = "digraph Tree {\n";
		String name = "";
		try // CFG may be null
		{
			fwSlice = new FileWriter(".output_intermediate/Slice.dot");
			pwSlice = new PrintWriter(fwSlice);
			for (int i = 0; i<slice_nodes.size(); i++){
				name = slice_nodes.get(i).nodeToString();
				Slice = Slice.concat("\""+name+"\"[label=\""+name+"\",style=filled,fillcolor=olivedrab3];");
				Slice = Slice.concat("\n");
			}
			for(int i = 0; i< pairs.size(); i++){
				Slice = Slice.concat("\""+pairs.get(i).getFirst().nodeToString()+"\" -> "+"\""+pairs.get(i).getSecond().nodeToString()+"\"");
				Slice = Slice.concat(" [style=bold];\n");     
			}
			Slice = Slice.concat("}");
			pwSlice.println(Slice);
		} catch (Exception e){};
		pwSlice.close();
	}
	
	/**
	 * Returns pairs of nodes which are found in the same path among all possible paths within this graph,
	 * where pair's first element is always 'node1' passed as a parameter  
	 * @param node1 a node of this graph
	 * @param paths an array list made of array list containing nodes
	 * @return an array list made of pairs containing nodes
	 */
	private ArrayList<Pair<Node,Node>> searchForPairsInCFG(Node node1, ArrayList<ArrayList<Node>> paths){
		ArrayList<Pair<Node,Node>> res = new ArrayList<Pair<Node, Node>>();
		Node node2;
		for(int i = 0; i< slice_nodes.size(); i++){
			for(int j = 0; j< paths.size(); j++){
				node2 = slice_nodes.get(i);
				if (!(node1.getID().equalsIgnoreCase(node2.getID()))){	// If they are not the same node
					if(inTheSamePath(node1, node2, paths.get(j))){
						res.add(new Pair<Node, Node>(node1, node2));
					}
				}
			}
		}
		for(int i = 0; i< slice_nodes.size(); i++){
			for(int j = 0; j< node1.getSuccessors().size(); j++){
				node2 = node1.getSuccessors().get(j);
				if (!(node1.getID().equalsIgnoreCase(slice_nodes.get(i).getID()))){	// if they are not the same node, proceed to see if it's a successor
					if(node2.getID().equalsIgnoreCase(slice_nodes.get(i).getID()))	// it is a successor, it is added
						res.add(new Pair<Node, Node>(node1, node2));
				}
			}
		}
		if(node1.getPointsItself())	// if it points itself, it is added 
			res.add(new Pair<Node, Node>(node1, node1));
		return res;
	}
	
	/**
	 * Given two nodes, returns true if they both are found in the same path among all possible paths
	 * within this graph
	 * @param fstNode a node
	 * @param sndNode a node
	 * @param path an array list made of nodes
	 * @return <ul>
	 *          <li>true: 'fstNode' and 'sndNode' are both found in the same path</li>
	 *          <li>false: 'fstNode' and 'sndNode' are both not found in the same path</li>
	 *          </ul>
	 */
	private boolean inTheSamePath(Node fstNode, Node sndNode, ArrayList<Node> path){
		boolean res = false;
		boolean first = false;	// initial value is false
		boolean second = false;	// initial value is false
		int fst = 0;
		int snd = 0;
		for(int i=0; i<path.size(); i++){
			if(first){	// if true, it means that fstNode has already been found within the path 
				if(!(second)){
					if(path.get(i).getID().equalsIgnoreCase(sndNode.getID())){
						second = true;
						snd = i;
					}
				}
			} else {
				if(path.get(i).getID().equalsIgnoreCase(fstNode.getID())){
					first = true;
					fst = i;
				}
			}
		}
		if (first && second)
			res = (count(fst,snd,path))==0;	
		return res;
	}
	
	/**
	 * Sets all nodes from this graph's 'visited' field as false
	 */
	private void resetVisited(){
		for(int i=0; i<nodes.size(); i++)
			nodes.get(i).setVisited(false);
	}
	
	/**
	 * A vertex called 'ver' is given as a parameter, returns true if the second component of the vertex is
	 * an ancestor of the first component of the vertex
	 * @param ver a vertex of this graph
	 * @return <ul>
	 *          <li>true: second component of the vertex is an ancestor of first component of the vertex</li>
	 *          <li>false: second component of the vertex is not an ancestor of first component of the vertex</li>
	 *          </ul>
	 */
	public boolean isAncestor(Vertex ver){
		boolean res = false;
		TreeNode<Node> aux;
		TreeNode<Node> raiz = tn.getRoot();
		ArrayList<TreeNode<Node>> nodosArbol = tn.getPreOrderTraversal();
		for(int i=0; i<nodosArbol.size(); i++){
			if(nodosArbol.get(i).getData().getID().equalsIgnoreCase(ver.getFirst().getID())){
				if(!(ver.getFirst().getID().equalsIgnoreCase(raiz.getData().getID()))){ // if vertex's first element is different from tree's root
					aux = nodosArbol.get(i).getParent();
					while(!(aux.getData().getID().equalsIgnoreCase(raiz.getData().getID()))){
						if(aux.getData().getID().equalsIgnoreCase(ver.getSecond().getID())){
							res = true;
						}
						aux = aux.getParent();
					}
				}
			}	
		}
		return res;
	}
	
	/**
	 * Given a vertex 'v', searches for the least common ancestor of both nodes contained within the vertex 
	 * @param v a vertex made of nodes
	 * @return a string representing the least common ancestor
	 */
	public String leastCommonAncestor (Vertex v){
		String res = "";
		LinkedList<String> ancestors1 = new LinkedList<String>();
		LinkedList<String> ancestors2 = new LinkedList<String>();
		TreeNode<Node> n1,n2;
		ArrayList<TreeNode<Node>> nodesTree = tn.getPreOrderTraversal();
		int i = 0;
		while (!(nodesTree.get(i).getData().getID().equalsIgnoreCase(v.getFirst().getID()))){
			i++;
		}
		n1 = nodesTree.get(i);
		while (!(n1.getData().getID().equalsIgnoreCase(tn.getRoot().getData().getID()))){
			ancestors1.add(n1.getData().getID());
			n1 = n1.getParent();
		}
		ancestors1.add(tn.getRoot().getData().getID());
		i = 0;
		while (!(nodesTree.get(i).getData().getID().equalsIgnoreCase(v.getSecond().getID()))){
			i++;
		}
		n2 = nodesTree.get(i);
		while (!(n2.getData().getID().equalsIgnoreCase(tn.getRoot().getData().getID()))){
			ancestors2.add(n2.getData().getID());
			n2 = n2.getParent();
		}
		ancestors2.add(tn.getRoot().getData().getID());
		int index1, index2, size;
		index1 = ancestors1.size()-1;
		index2 = ancestors2.size()-1;
		if (ancestors1.size()<=ancestors2.size())
			size = ancestors1.size();
		else
			size = ancestors2.size();
		for(int j = 0; j<size; j++){
			if (ancestors1.get(index1).equalsIgnoreCase(ancestors2.get(index2)))
				res = ancestors1.get(index1);
			index1 = index1 - 1;
			index2 = index2 - 1;
		}
		return res; 
	}
	
	/**
	 * Sets node 'n' passed as a parameter to be the least common ancestor for some nodes within
	 * this graph starting from the second component of vertex 'v' passed as a parameter
	 * @param v a vertex
	 * @param n a node
	 */
	public void markLCA(Vertex v, Node n){
		LinkedList<Node> ancestors = new LinkedList<Node>();
		ArrayList<TreeNode<Node>> nodesTree = tn.getPreOrderTraversal();
		TreeNode<Node> n1;
		int i = 0;
		while (!(nodesTree.get(i).getData().getID().equalsIgnoreCase(v.getSecond().getID()))){
			i++;
		}
		n1 = nodesTree.get(i);
		while (!(n1.getData().getID().equalsIgnoreCase(tn.getRoot().getData().getID()))){
			ancestors.add(n1.getData());
			n1 = n1.getParent();
		}
		ancestors.add(tn.getRoot().getData());
		i = 0;
		while (!(ancestors.get(i).getID().equalsIgnoreCase(n.getID()))){
			ancestors.get(i).setLCA(n);
			i++;
		}
		if (v.getFirst().getID().equalsIgnoreCase(n.getID())){
			v.getFirst().setPointsItself(true);
		}
	}
	
	/**
	 * Changes the least common ancestor for those nodes whose least common ancestor is node 'exit' for node 'start' 
	 */
	public void changeExitForStart(){
		for (int i=0; i<nodes.size(); i++){
			try
			{
				if (nodes.get(i).getLCA().getID().equalsIgnoreCase("exit")){
					nodes.get(i).setLCA(Slicer.getNodeById("start"));
				}
			} catch (NullPointerException e){}
		}
	}
	
	/**
	 * Builds a DOT file called 'CFG.dot' within .output_intermediate folder representing the output CFG
	 */
	public void CFGToDot(){
		String name = "";
		String node1 = "";
		String node2 = "";
		String cfg = "digraph Tree {\n";
		try
		{
			fwCFG = new FileWriter(".output_intermediate/CFG.dot");
			pwCFG = new PrintWriter(fwCFG);
			for (int i = 0; i<nodes.size(); i++){
				name = nodes.get(i).toString();
				if((!(name.equalsIgnoreCase("start")) && (!(name.equalsIgnoreCase("exit"))) && (!(name.equalsIgnoreCase("entry"))))){
					cfg = cfg.concat("\""+name+"\"[label=\""+name+"\",style=filled,fillcolor=limegreen];");
					cfg = cfg.concat("\n");
				}
			}
			for (int i = 0; i<vertexes.size(); i++){
				node1 = vertexes.get(i).getFirst().toString();
				node2 = vertexes.get(i).getSecond().toString();
				if((!(node1.equalsIgnoreCase("start")) && (!(node1.equalsIgnoreCase("exit"))) && (!(node1.equalsIgnoreCase("entry")))) 
					&& (!(node2.equalsIgnoreCase("start")) && (!(node2.equalsIgnoreCase("exit"))) && (!(node2.equalsIgnoreCase("entry"))))){
					cfg = cfg.concat("\""+node1+"\" -> "+"\""+node2+"\"");
					cfg = cfg.concat(" [style=bold];\n");      
				}
			}
			cfg = cfg.concat("}");
			pwCFG.println(cfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pwCFG.close();
	}

	/**
	 * Reads all paths within this graph opening file .paths.txt 
	 * @return an array list made of array lists containing nodes
	 */
	public ArrayList<ArrayList<Node>> readAllPaths(){
		String line;
		String [] nodes_line;
		int i;
		ArrayList<ArrayList<Node>> res = new ArrayList<ArrayList<Node>>();
		try {
			File filePaths = new File (".paths.txt");
			FileReader frPaths = new FileReader(filePaths);
			BufferedReader brPaths = new BufferedReader(frPaths);
			line = brPaths.readLine();
			while (line != null) {
				ArrayList<Node> n = new ArrayList<Node>();
				nodes_line = line.split(" ");
				try {
					i = 0;
					while(nodes_line[i]!= null){
						i++;
						n.add(Slicer.getNodeById(nodes_line[i]));
					}
				}	
				catch (ArrayIndexOutOfBoundsException e) {
					res.add(n);
				}
				line = brPaths.readLine();
			}
			brPaths.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * Searches recursively for all different paths within the graph starting from node 'n' 
	 * @param n a node from this graph
	 * @param path a string representing the partial path
	 * @param successorNumber integer indicating the number of successors of node 'n'
	 * @return a string with all different paths together 
	 */
	public String dfs(Node n, String path, int successorNumber){
		LinkedList<Node> successors = n.getSuccessors();
		if(n.getVisited()) {	// it's been already visited
			return path+" "+n.getID();
		} 
		if(successors.size()==0)	// it has not got successors 
			return path+" "+n.getID();
		n.setVisited(true);
		if(successorNumber+1>=successors.size()){
			return dfs(successors.get(successorNumber), path+" "+n.getID(), 0);
		} else {	// successorNumber+1 is even less than the total number of successors
			return dfs(n, path, successorNumber+1) +"\n"+ dfs(successors.get(successorNumber), path+" "+n.getID(), 0);
		}
	}
	
	/**
	 * Searches recursively for all different paths within the graph starting from node 'n' 
	 * @param n a node from this graph
	 * @param path a string representing the partial path
	 * @param successorNumber integer indicating the number of successors of node 'n'
	 * @return a string with all different paths together 
	 */
	public String dfs2(Node n, String path, int successorNumber){
		LinkedList<Node> successors = n.getSuccessors();
		if(n.getVisited()){ // it's been already visited
			return path+" "+n.getID();
		} 
		if(successors.size()==0) // it has not got successors 
			return path+" "+n.getID();
		n.setVisited(true);
		if(successorNumber+1>=successors.size()){
			successors.get(successorNumber).setVisited(false);
			return dfs2(successors.get(successorNumber), path+" "+n.getID(), 0);
		} else {	// successorNumber+1 is even less than the total number of successors	
			n.setVisited(false);
			return dfs2(successors.get(successorNumber), path+" "+n.getID(), 0)  +"\n"+ dfs2(n, path, successorNumber+1);
		}
	}
	
	/**
	 * Given two nodes' positions, checks that there are none of the remaining nodes from the slice between them 
	 * @param first an integer indicating the position of the first node within the path
	 * @param second an integer indicating the position of the second node within the path
	 * @param path an array list made of nodes
	 * @return number of the remaining nodes from the slice between them
	 */
	private int count(int first, int second, ArrayList<Node> path){
		int res = 0;
		for(int j=0;j<slice_nodes.size();j++){
			for(int i=first+1;i<second;i++){
				if(slice_nodes.get(j).getID().equalsIgnoreCase(path.get(i).getID()))
					res++;
			}
		}
		return res;
	}
}