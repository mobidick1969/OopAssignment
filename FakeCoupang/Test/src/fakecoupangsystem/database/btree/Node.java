package fakecoupangsystem.database.btree;


import fakecoupangsystem.database.exception.DuplicateEntryException;
import fakecoupangsystem.exception.UnderFlowException;

import java.io.*;
import java.util.Vector;

/**
 * Created by Coupang on 2015. 12. 28..
 */
public abstract class Node<KEY extends Comparable<KEY>> implements Serializable {

	public static final int ROOT_ID = -1;

	private int papaId = ROOT_ID;
	private int id;
	Vector<Pair> vector;
	private int maxLen;

	public Node() {
	}

	public Node(int id, int papaId, int maxLen) {
		super();
		this.id = id;
		this.papaId = papaId;
		this.maxLen = maxLen;

		vector = new Vector<Pair>(maxLen, 1);
		vector.ensureCapacity(maxLen);
	}

	public int getId() {
		return id;
	}

	public void setPapaId(int papaId) {
		this.papaId = papaId;
	}

	public boolean isLeaf() {
		return this instanceof LeafNode;
	}

	public Node loadParent(File indexArea) {
		return Node.load(indexArea, papaId);
	}

	public boolean isOverflow() {
		return vector.size() >= maxLen;
	}

	public static Node load(File indexArea, int nodeId) {
		ObjectInputStream in = null;
		String name = null;

		if( nodeId == ROOT_ID ) {
			name = "root.index";
		} else {
			name = nodeId + ".index";
		}

		try {
			in = new ObjectInputStream(new FileInputStream(new File(indexArea, name)));
			Node node = (Node)in.readObject();
			return node;
		} catch (FileNotFoundException e) {
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if( in != null ) {
				try { in.close(); } catch (Exception e) { }
			}
		}
		return null;
	}

	public void store(File indexArea) {
		ObjectOutputStream out = null;

		String name = null;

		if( id == ROOT_ID ) {
			name = "root.index";
		} else {
			name = id + ".index";
		}

		try {
			out = new ObjectOutputStream(new FileOutputStream(new File(indexArea, name)));
			out.writeObject(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}
	}

	protected int findInsertPosition(KEY key) {
		int idx = 0;
		if( vector.isEmpty() ) {
			return idx;
		} else if( vector.size() <= 100 ) {
			for( ; idx<vector.size() ; idx++ ) {
				if( vector.get(idx).getKey().compareTo(key) >= 0 ) {
					break;
				}
			}
		} else {
			int startIdx = 0;
			int endIdx = vector.size() - 1;
			int midIdx = -1;

			while( startIdx<endIdx ) {
				midIdx = (endIdx + startIdx)/2;

				if( vector.get(midIdx).getKey().compareTo(key) < 0 ) {
					startIdx = midIdx + 1;
				} else if( vector.get(midIdx).getKey().compareTo(key) > 0) {
					endIdx = midIdx - 1;
				} else {
					break;
				}
			}

			midIdx = ( midIdx < 0 ? 0 : midIdx);
			idx = -1;

			if( vector.get(midIdx).getKey().compareTo(key) < 0 ) {
				for(int i = midIdx ; i<vector.size() ; i++ ) {
					if( vector.get(i).getKey().compareTo(key) >= 0 ) {
						idx = i;
						break;
					}
				}

				if( idx == -1 ) {
					idx = vector.size();
				}

			} else {
				for(int i = midIdx ; i>0 ; i--) {
					if( vector.get(i-1).getKey().compareTo(key) < 0 ) {
						idx = i;
						break;
					}
				}

				if( idx == -1 ) {
					//min value
					idx = 0;
				}
			}
		}

		return idx;
	}

	public int getMaxLen() {
		return maxLen;
	}

	public SplitResult split() {
		if( !isOverflow() ) {
			throw new UnderFlowException();
		}

		SplitResult splitResult = new SplitResult();

		int midIdx = vector.size()/2;
		Pair<KEY> midPair = vector.get(midIdx);
		PlainPair<KEY> copyPair = new PlainPair<KEY>(id, midPair.getKey());
		Node<KEY> newNode = splitSub(midIdx, isLeaf());

		//if not leaf change spare node id
		if( !isLeaf() ) {
			((PlainNode<KEY>)newNode).setSpareNodeId(((PlainNode<KEY>)this).getSpareNodeId());
			((PlainNode<KEY>)this).setSpareNodeId(((PlainPair<KEY>) midPair).getNodeId());
		}

		splitResult.setPair(copyPair);
		splitResult.setNode(newNode);

		return splitResult;
	}

	public Node<KEY> splitSub(int idx, boolean include) {
		Node<KEY> newNode = null;

		if( isLeaf() ) {
			newNode = new LeafNode<KEY>(Node.ROOT_ID, papaId, maxLen);
		} else {
			newNode = new PlainNode<KEY>(Node.ROOT_ID, papaId, maxLen);
		}

		for(int i = idx ; i<vector.size() ; i++ ) {
			Pair<KEY> pair = vector.get(i);
			if( i == idx ) {
				if( include ) {
					newNode.vector.add(pair);
				}
			} else {
				newNode.vector.add(pair);
			}
		}

		for(int i = vector.size()-1 ; i>=idx ; --i ) {
			vector.remove(i);
		}

		return newNode;
	}

	public void add(Pair<KEY> pair, boolean duplicate) {
		int idx = findInsertPosition(pair.getKey());

		try {
			Pair<KEY> gotPair = null;

			gotPair = vector.get(idx);
			if( gotPair.getKey().compareTo(pair.getKey()) == 0 ) {
				if( !duplicate ) {
					throw new DuplicateEntryException();
				} else {
					if( this instanceof LeafNode ) {
						LeafPair<KEY> leafGotPair = (LeafPair<KEY>)gotPair;
						LeafPair<KEY> paramPair = (LeafPair<KEY>)pair;

						paramPair.getPage().setNextPage(leafGotPair.getPage());
						leafGotPair.setPage(paramPair.getPage());

						return;
					}
				}
			}
		} catch(Exception ignore) {
		}

		vector.add(idx, pair);
	}

	public int getPapaId() {
		return papaId;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(" id : " + id + "   ");
		buffer.append(" papaId : " + papaId + "   ");
		for(Pair<KEY> pair : vector ) {
			buffer.append(pair.toString() + "   ");
		}

		buffer.append("]");
		return buffer.toString();
	}
}
