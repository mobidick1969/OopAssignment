package fakecoupangsystem.database.btree;

/**
 * Created by Coupang on 2015. 12. 28..
 */
public class SplitResult<KEY extends Comparable<KEY> > {
	private PlainPair<KEY> pair;
	private Node<KEY> node;

	public PlainPair<KEY> getPair() {
		return pair;
	}

	public void setPair(PlainPair<KEY> pair) {
		this.pair = pair;
	}

	public Node<KEY> getNode() {
		return node;
	}

	public void setNode(Node<KEY> node) {
		this.node = node;
	}
}
