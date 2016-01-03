package fakecoupangsystem.database.btree;

import java.io.Serializable;

/**
 * Created by Coupang on 2015. 12. 28..
 */
public abstract class Pair<KEY extends Comparable<KEY>> implements Serializable, Comparable<Pair<KEY> > {
	private KEY key;

	public Pair() {
	}

	public Pair(KEY key) {
		this.key = key;
	}

	public KEY getKey() {
		return key;
	}

	public void setKey(KEY key) {
		this.key = key;
	}

	@Override
	public int compareTo(Pair<KEY> other) {
		return key.compareTo(other.key);
	}
}
