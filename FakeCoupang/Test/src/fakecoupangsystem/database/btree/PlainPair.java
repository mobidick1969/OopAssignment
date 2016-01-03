package fakecoupangsystem.database.btree;

/**
 * Created by Coupang on 2015. 12. 28..
 */
public class PlainPair<KEY extends Comparable<KEY>> extends Pair<KEY> {
	private int nodeId;

	public PlainPair() {
	}

	public PlainPair(int nodeId, KEY key) {
		super(key);
		this.nodeId = nodeId;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	@Override
	public String toString() {
		return "{ key : " + getKey() + ", " + " nodeId : " + nodeId + "} ";
	}
}
