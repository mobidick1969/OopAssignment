package fakecoupangsystem.database.btree;

/**
 * Created by Coupang on 2015. 12. 28..
 */
public class PlainNode<KEY extends Comparable<KEY>> extends Node<KEY>  {
	private int spareNodeId;

	public PlainNode(int id, int papaId, int maxLen) {
		super(id, papaId, maxLen);
	}

	public int getSpareNodeId() {
		return spareNodeId;
	}

	public void setSpareNodeId(int spareNodeId) {
		this.spareNodeId = spareNodeId;
	}

	//fetch next node id suitable for key
	public int findPlausibleNodeId(KEY key) {
		for(int i = 0 ; i<vector.size() ; i++) {
			PlainPair<KEY> got = (PlainPair<KEY>)vector.get(i);

			if( got.getKey().compareTo(key) > 0 ) {
				return got.getNodeId();
			}
		}

		return spareNodeId;
	}

	public void updateNodeIdInside(KEY key, int nodeId) {
		int idx = findInsertPosition(key) + 1;

		if( idx >= vector.size() ) {
			spareNodeId = nodeId;
		} else {
			PlainPair<KEY> pair = (PlainPair<KEY>)vector.get(idx);
			pair.setNodeId(nodeId);
			vector.set(idx, pair);
		}
	}
}
