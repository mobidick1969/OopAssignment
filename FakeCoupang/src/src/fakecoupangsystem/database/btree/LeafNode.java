package fakecoupangsystem.database.btree;


/**
 * Created by Coupang on 2015. 12. 28..
 */
public class LeafNode<KEY extends Comparable<KEY>> extends Node<KEY> {

	public LeafNode(int id, int papaId, int maxLen) {
		super(id, papaId, maxLen);
	}

	public Page find(KEY key) {
		for(Pair pair : vector ) {
			LeafPair<KEY> leafPair = (LeafPair<KEY>)pair;
			if( leafPair.getKey().compareTo(key) == 0 ) {
				return leafPair.getPage();
			}
		}

		return null;
	}

}