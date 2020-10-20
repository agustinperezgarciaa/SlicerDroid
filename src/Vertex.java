/*
	Vertex is part of SlicerDroid.

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
 * Class Vertex
 *
 * Contains all the information concerning to a vertex
 *
 * @author Agustin & Facundo
 * @version 1.0
 */
public class Vertex {
	// vertex's first node 
	private Node first;
	// vertex's second node
	private Node second;

	/**
	* Constructor with 2 parameters
	* @param first the first node forming the vertex 
	* @param second the second node forming the vertex
	*/
	public Vertex(Node first,Node second){
		this.first = first;
		this.second = second;
	}
		
	/**
	 * Sets the first node forming the vertex
	 * @param newFirst node passed as a parameter meant to update the first node forming the vertex
	 */
	public void setFirst(Node newFirst){
		this.first = newFirst;
	}
	
	/**
	 * Sets the second node forming the vertex
	 * @param newSecond node passed as a parameter meant to update the second node forming the vertex
	 */
	public void setSecond(Node newSecond){
		this.second = newSecond;
	}
		
	/**
	 * Outputs the first element forming this vertex
	 * @return first node forming this vertex
	 */
	public Node getFirst(){
		return this.first;
	}
		
	/**
	 * Outputs the second element forming this vertex
	 * @return second node forming this vertex
	 */
	public Node getSecond(){
		return this.second;
	}
}
