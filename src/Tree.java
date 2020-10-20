import java.util.ArrayList;

/*
	Tree is part of SlicerDroid.

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
 * Class Tree
 *
 * Contains all the information concerning to an abstract tree
 *
 * @author Agustin & Facundo
 * @version 1.0
 */
public class Tree<T> {
	private TreeNode<T> root;

	/**
	 * Constructor with 1 parameter, sets this tree's root
	 * @param root a tree node of type T
	 */
	public Tree(TreeNode<T> root) {
		this.root = root;
	}

	/**
	 * Checks if this tree is empty or not
	 * @return <ul>
	 *          <li>true: this tree is empty</li>
	 *          <li>false: this tree is not empty</li>
	 *          </ul>
	 */
	public boolean isEmpty() {
		return (root == null) ? true : false;
	}

	/**
	 * Outputs this tree's root
	 * @return a tree node of type T
	 */
	public TreeNode<T> getRoot() {
		return root;
	}

	/**
	 * Sets a new root for this tree
	 * @param root a tree node of type T
	 */
	public void setRoot(TreeNode<T> root) {
		this.root = root;
	}

	/**
	 * Checks if the 'key' information is found within the tree
	 * @param key data of type T
	 * @return <ul>
	 *          <li>true: 'key' information is found within the tree</li>
	 *          <li>false: 'key' information is not found within the tree</li>
	 *          </ul>
	 */
	public boolean exists(T key) {
		return find(root, key);
	}
	
	/**
	 * Checks if 'keyNodoArbol' (data of type T passed as a parameter) is found within
	 * tree 'TreeNode' (a tree node of type T passed as a parameter)
	 * @param TreeNode a tree node of type T
	 * @param keyNodoArbol data of type T
	 * @return <ul>
	 *          <li>true: 'keyNodoArbol' is found within 'TreeNode'</li>
	 *          <li>false: 'keyNodoArbol' is not found within 'TreeNode'</li>
	 *          </ul>
	 */
	private boolean find(TreeNode<T> TreeNode, T keyNodoArbol) {
		boolean res = false;
		if (TreeNode.getData().equals(keyNodoArbol))
			return true;
		else {
			for (TreeNode<T> child : TreeNode.getChildren())
				if (find(child, keyNodoArbol))
					res = true;
		}
		return res;
	}

	/**
	 * Outputs all nodes forming this tree in pre order
	 * @return an array list containing tree nodes of type T
	 */
	public ArrayList<TreeNode<T>> getPreOrderTraversal() {
		ArrayList<TreeNode<T>> preOrder = new ArrayList<TreeNode<T>>();
		buildPreOrder(root, preOrder);
		return preOrder;
	}

	/**
	 * Builds a list of all nodes forming this tree in pre order
	 * @param TreeNode a tree node of type T
	 * @param preOrder an array list containing tree nodes of type T
	 */
	private void buildPreOrder(TreeNode<T> TreeNode, ArrayList<TreeNode<T>> preOrder) {
		preOrder.add(TreeNode);
		for (TreeNode<T> child : TreeNode.getChildren()) {
			buildPreOrder(child, preOrder);
		}
	}

	/**
	 * Outputs a set of paths starting from this tree's root to any leaf
	 * @return an array list made of array lists containing tree nodes of type T 
	 */
	public ArrayList<ArrayList<TreeNode<T>>> getPathsFromRootToAnyLeaf() {
		ArrayList<ArrayList<TreeNode<T>>> paths = new ArrayList<ArrayList<TreeNode<T>>>();
		ArrayList<TreeNode<T>> currentPath = new ArrayList<TreeNode<T>>();
		getPath(root, currentPath, paths);
		return paths;
	}

	/**
	 * Builds a set of paths starting from this 'TreeNode' passed as a parameter to any leaf
	 * @param TreeNode a tree node of type T
	 * @param currentPath an array list containing tree nodes of type T
	 * @param paths an array list made of array lists containing tree nodes of type T
	 */
	public void getPath(TreeNode<T> TreeNode, ArrayList<TreeNode<T>> currentPath, ArrayList<ArrayList<TreeNode<T>>> paths) {
		if (currentPath == null)
			return;
		currentPath.add(TreeNode);
		if (TreeNode.getChildren().size() == 0) {
			// This is a leaf
			paths.add(clone(currentPath));
		}
		for (TreeNode<T> child : TreeNode.getChildren())
			getPath(child, currentPath, paths);
		int index = currentPath.indexOf(TreeNode);
		for (int i = index; i < currentPath.size(); i++)
			currentPath.remove(index);
	}
	
	/**
	 * Clones and outputs a list of tree nodes of type T
	 * @param list an array list containing tree nodes of type T
	 * @return an array list containing tree nodes of type T
	 */
	private ArrayList<TreeNode<T>> clone(ArrayList<TreeNode<T>> list) {
		ArrayList<TreeNode<T>> newList = new ArrayList<TreeNode<T>>();
		for (TreeNode<T> TreeNode : list)
			newList.add(new TreeNode<T>(TreeNode));
		return newList;
	}
}