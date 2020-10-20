/*
	Pair is part of SlicerDroid.

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
 * Class Pair
 *
 * Contains all the information concerning to a pair
 *
 * @author Agustin & Facundo 
 * @version 1.0
 */
public class Pair<F, S> {
	//first member of the pair
	private F first; 
	//second member of pair
	private S second; 

	/**
	 * Constructor with 2 parameters
	 * @param first the first element forming the pair
	 * @param second the second element forming the pair
	 */
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Sets a new element as the first member of this pair
	 * @param first new element to be seted as the first member of this pair
	 */
	public void setFirst(F first) {
		this.first = first;
	}

	/**
	 * Sets a new element as the second member of this pair
	 * @param second new element to be seted as the second member of this pair
	 */
	public void setSecond(S second) {
		this.second = second;
	}

	/**
	 * Outputs the first member of this pair
	 * @return an element of type F
	 */
	public F getFirst() {
		return first;
	}

	/**
	 * Outputs the second member of this pair
	 * @return an element of type S
	 */
	public S getSecond() {
		return second;
	}
}
