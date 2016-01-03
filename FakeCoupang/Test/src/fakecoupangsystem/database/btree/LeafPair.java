package fakecoupangsystem.database.btree;


/**
 * Created by Coupang on 2015. 12. 28..
 */
public class LeafPair<KEY extends Comparable<KEY>> extends Pair<KEY> {
	private Page page;

	public LeafPair() {
	}

	public LeafPair(KEY key, Page page) {
		super(key);
		this.page = page;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	@Override
	public String toString() {
		return "{ key : " + getKey() + ", page : " + page + "} ";
	}
}
