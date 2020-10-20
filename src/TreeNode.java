import java.util.ArrayList;
import java.util.List;

/*
	TreeNode is part of SlicerDroid.
	
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
 * Class TreeNode
 *
 * Contains all the information concerning to an abstract tree node
 *
 * @author Agustin & Facundo
 * @version 1.0
 */
public class TreeNode<T> {
	private T data;
	private List<TreeNode<T>> children;
	private TreeNode<T> parent;

	/**
	 * Constructor with 1 parameter
	 * @param data information to be seted as this node's data
	 */
	public TreeNode(T data) {
		this.data = data;
		this.children = new ArrayList<TreeNode<T>>();
	}

	/**
	 * Constructor with 1 parameter
	 * @param TreeNode a tree to be seted as a list of nodes of type T
	 */
	public TreeNode(TreeNode<T> TreeNode) {
		this.data = (T) TreeNode.getData();
		children = new ArrayList<TreeNode<T>>();
	}

	/**
	 * Adds a new child to this tree node
	 * @param child a tree node of type T to be seted as this node's new child
	 */
	public void addChild(TreeNode<T> child) {
		child.setParent(this);
		children.add(child);
	}

	/**
	 * Outputs this tree node's data	
	 * @return information of type T
	 */
	public T getData() {
		return this.data;
	}

	/**
	 * Outputs this tree node's parent
	 * @return a tree node of type T
	 */
	public TreeNode<T> getParent() {
		return this.parent;
	}
	
	/**
	 * Sets this tree node's parent
	 * @param parent tree node to be seted as this tree node's parent
	 */
	public void setParent(TreeNode<T> parent) {
		this.parent = parent;
	}

	/**
	 * Outputs all of this tree node's children
	 * @return a list of tree nodes of type T
	 */
	public List<TreeNode<T>> getChildren() {
		return this.children;
	}

	/**
	 * Checks if the object passed as a parameter's data is equal to this tree node's data
	 * @return <ul>
	 *          <li>true: the object passed as a parameter's data is equal to this tree node's data</li>
	 *          <li>false: the object passed as a parameter's data is not equal to this tree node's data</li>
	 *          </ul>
	 */
	@Override
	public boolean equals(Object obj) {
		if (null == obj) 
			return false;
		if (obj instanceof TreeNode) {
			if (((TreeNode<?>) obj).getData().equals(this.data))
				return true;
		}
		return false;
	}
	
	/**
	 * Outputs this tree node's data
	 * @return a string describing this tree node's data
	 */
	@Override
	public String toString() {
		return this.data.toString();
	}
}