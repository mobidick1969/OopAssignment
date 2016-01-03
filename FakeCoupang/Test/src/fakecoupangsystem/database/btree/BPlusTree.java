package fakecoupangsystem.database.btree;


import fakecoupangsystem.database.resources.KeyGenerator;
import fakecoupangsystem.database.resources.FileKeyGeneratorImpl;

import java.io.File;
import java.io.IOException;

/**
 * Created by Coupang on 2015. 12. 29..
 */
public class BPlusTree<KEY extends Comparable<KEY> > {

	private int NODE_SIZE;
	private File indexArea;
	private Node<KEY> root;
	private KeyGenerator keyGenerator;
	private boolean duplicate;

	public BPlusTree(File indexRoot, String name, int nodeSize, boolean duplicate) throws IOException {
		this.duplicate = duplicate;
		NODE_SIZE = nodeSize;
		indexArea = new File(indexRoot, name);
		indexArea.mkdir();

		keyGenerator = new FileKeyGeneratorImpl(indexArea, name);
		keyGenerator.load();

		root = Node.load(indexArea, Node.ROOT_ID);
		if( root == null ) {
			root = new LeafNode<KEY>(Node.ROOT_ID, Node.ROOT_ID, NODE_SIZE);
		}
	}

	public void add(KEY key, Page page) {
		LeafNode<KEY> leafNode = getValidLeafNode(key);

		Pair<KEY> pair = new LeafPair<KEY>(key, page);
		leafNode.add(pair, duplicate);
		overflowProcess(leafNode);
	}

	private void overflowProcess(Node<KEY> s1) {
		if( s1.isOverflow() ) {
			//System.out.println(" ***** overflow ! " + s1);
			SplitResult<KEY> splitResult = s1.split();
			Node<KEY> s2 = splitResult.getNode(); s2.setId(keyGenerator.nextIntId());
			Node<KEY> papa = null;

			if( s1.getId() != Node.ROOT_ID ) {
				papa = Node.load(indexArea, s1.getPapaId());
			}

			//resetting new parent node id
			if( !s1.isLeaf() ) {
				for(int i = 0 ; i<s2.vector.size() ; i++ ) {
					PlainPair<KEY> pair = (PlainPair<KEY>)s2.vector.get(i);
					int nodeId = pair.getNodeId();
					Node<KEY> child = Node.load(indexArea, nodeId);
					child.setPapaId(s2.getId());
					child.store(indexArea);
				}

				int spareNodeId = ((PlainNode<KEY>)s2).getSpareNodeId();
				Node<KEY> spareChild = Node.load(indexArea, spareNodeId);
				spareChild.setPapaId(s2.getId());
				spareChild.store(indexArea);
			}

			upPropagation(s1, s2, papa, splitResult);
		} else {
			s1.store(indexArea);
		}
	}

	private void upPropagation(Node<KEY> s1, Node<KEY> s2, Node<KEY> papa, SplitResult<KEY> sr) {
		if( papa == null ) {
			//resetting id
			if(s1.getId() == Node.ROOT_ID ) {
				s1.setId(keyGenerator.nextIntId());
				sr.getPair().setNodeId(s1.getId());

				if( !s1.isLeaf() ) {
					for(int i = 0 ; i<s1.vector.size() ; i++ ) {
						PlainPair<KEY> pair = (PlainPair<KEY>)s1.vector.get(i);
						int nodeId = pair.getNodeId();
						Node<KEY> child = Node.load(indexArea, nodeId);
						child.setPapaId(s1.getId());
						child.store(indexArea);
					}

					int spareNodeId = ((PlainNode<KEY>)s1).getSpareNodeId();
					Node<KEY> spareChild = Node.load(indexArea, spareNodeId);
					spareChild.setPapaId(s1.getId());
					spareChild.store(indexArea);
				}
			}

			//resetting root
			PlainNode<KEY> newRoot = new PlainNode<KEY>(Node.ROOT_ID, Node.ROOT_ID, NODE_SIZE);
			newRoot.add(sr.getPair(), duplicate);
			newRoot.setSpareNodeId(s2.getId());
			newRoot.store(indexArea);

			root = newRoot;
			//System.out.println(" ***** new root : " + root);

			s1.setPapaId(Node.ROOT_ID);
			s2.setPapaId(Node.ROOT_ID);
			s1.store(indexArea);
			s2.store(indexArea);
		} else {
			papa.add(sr.getPair(), duplicate);
			((PlainNode<KEY>) papa).updateNodeIdInside(sr.getPair().getKey(), s2.getId());

			//System.out.println(papa.getId());
			s1.setPapaId(papa.getId());
			s2.setPapaId(papa.getId());
			s1.store(indexArea);
			s2.store(indexArea);


			if (papa.isOverflow()) {
				overflowProcess(papa);
			} else {
				papa.store(indexArea);

				if (papa.getId() == Node.ROOT_ID) {
					root = papa;
				}
			}
		}


		//System.out.println(" ***** s1 " + s1);
		//System.out.println(" ***** s2 " + s2);

	}

	public Page get(KEY key) {
		LeafNode<KEY> leafNode = getValidLeafNode(key);

		return leafNode.find(key);
	}

	private LeafNode<KEY> getValidLeafNode(KEY key) {
		if( root.isLeaf() ) {
			//System.out.println(root);
			return (LeafNode<KEY>)root;
		}

		return deepFindLeafNode((PlainNode<KEY>)root, key);
	}

	private LeafNode<KEY> deepFindLeafNode(PlainNode<KEY> node, KEY key ) {
		//System.out.println(node);
		int nodeId = node.findPlausibleNodeId(key);
		Node<KEY> childNode = Node.load(indexArea, nodeId);

		//System.out.println(childNode);

		while(!childNode.isLeaf() ) {
			nodeId = ((PlainNode<KEY>)childNode).findPlausibleNodeId(key);
			childNode = Node.load(indexArea, nodeId);
			//System.out.println(childNode);
		}

		return (LeafNode<KEY>)childNode;
	}

	public void close() {
		keyGenerator.flush();
	}
}
