package fakecoupangsystem.database.btree;

import fakecoupangsystem.database.btree.resource.BPlusTreeMetaInfo;
import fakecoupangsystem.database.resources.KeyGenerator;
import fakecoupangsystem.database.resources.FileKeyGeneratorImpl;
import org.junit.Test;

import java.io.*;
import java.util.Vector;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Coupang on 2015. 12. 29..
 */
public class NodeTest {
	File testArea = new File("/tmp/btreetest");
	KeyGenerator keyGenerator;

	//@Before
	public void before() throws IOException {
		testArea.mkdir();
		keyGenerator = new FileKeyGeneratorImpl(testArea, "test");
		keyGenerator.load();
	}

	//@After
	public void after() {
		File files[] = testArea.listFiles();
		if( files != null ) {
			for(File f : files ) {
				f.delete();
			}
		}
	}

	//@Test
	public void nodeTest() throws IOException, ClassNotFoundException {
		PlainNode<Long> node = new PlainNode<Long>(keyGenerator.nextIntId(), Node.ROOT_ID, 100);
		node.setSpareNodeId(13);

		Vector<Pair> vector = null;

		for(int i = 0 ; i<10 ; i++ ) {
			Pair<Long> pair = new PlainPair<Long>(i, (long)i);
			node.add(pair, false);
		}

		node.store(testArea);

		Node<Long> got = Node.load(testArea, 0);

		assertThat(got instanceof PlainNode, is(true));
		assertThat(got.getId(), is(0));
		assertThat(got.isLeaf(), is(false));
		assertThat(got.getMaxLen(), is(100));
		assertThat(((PlainNode<Long>)got).getSpareNodeId(), is(13));

		node = ((PlainNode<Long>)node);
		vector = node.vector;
		for(int i = 0 ; i<10 ; i++) {
			PlainPair<Long> pair = (PlainPair)vector.get(i);
			assertThat(pair.getKey(), is((long)i));
			assertThat(pair.getNodeId(), is(i));
		}

		LeafNode<Long> node1 = new LeafNode<Long>(keyGenerator.nextIntId(), 0, 100);

		for(int i = 0 ; i<10 ; i++ ) {
			Page page = new Page();
			page.setPosition(i);
			page.setFile(new File("" + i));
			page.setNextPage(null);

			Pair<Long> pair = new LeafPair<Long>((long)i, page);
			node1.add(pair, false);
		}

		node1.store(testArea);
		node1 = (LeafNode<Long>) Node.load(testArea, 1);

		assertThat(node1 instanceof LeafNode, is(true));
		assertThat(node1.getId(), is(1));
		assertThat(node1.isLeaf(), is(true));
		assertThat(node1.getMaxLen(), is(100));

		vector = node1.vector;

		for(int i = 0 ; i<10 ; i++ ) {
			LeafPair<Long> pair = (LeafPair)vector.get(i);

			assertThat(pair.getKey(), is((long)i));

			Page page = pair.getPage();

			assertThat(page.getPosition(), is((long)i));
			assertThat(page.getFile().getName().trim(), is("" + i));
			assertThat(page.getNextPage(), nullValue());
		}

		got = node1.loadParent(testArea);

		assertThat(got instanceof PlainNode, is(true));
		assertThat(got.getId(), is(0));
		assertThat(got.isLeaf(), is(false));
		assertThat(got.getMaxLen(), is(100));
		assertThat(((PlainNode<Long>)got).getSpareNodeId(), is(13));
	}

	//@Test
	public void leafSplitTest() {
		int maxLen = 6;
		LeafNode<Long> leafNode = new LeafNode<Long>(keyGenerator.nextIntId(), Node.ROOT_ID, maxLen);
		for(int i = 0 ; i<maxLen ; i++ ) {
			Page page = new Page(new File("" + i), i);
			LeafPair<Long> leafPair = new LeafPair<Long>((long)i, page);
			leafNode.add(leafPair, false);
		}

		assertThat(leafNode.isOverflow(), is(true));

		SplitResult<Long> splitResult = leafNode.split();
		Pair<Long> pair = splitResult.getPair();
		Node<Long> newNode = splitResult.getNode();

		int midLen = maxLen/2;

		assertThat(pair.getKey(), is((long)midLen));

		Vector<Pair> vector = leafNode.vector;
		assertThat(vector.size(), is(midLen));
		for(int i = 0 ; i<vector.size() ; i++ ) {
			Pair<Long> tmp = vector.get(i);
			assertThat(tmp.getKey(), is((long)i));
		}

		vector = splitResult.getNode().vector;
		assertThat(vector.size(), is(maxLen - midLen));
		for(int i = 0 ; i<vector.size() ; i++ ) {
			Pair<Long> tmp = vector.get(i);
			assertThat(tmp.getKey(), is((long)i + midLen));
		}
	}

	//@Test
	public void plainNodeSplitTest() {
		int maxLen = 16;
		PlainNode<Long> node = new PlainNode<Long>(Node.ROOT_ID, Node.ROOT_ID, 5);
		for(int i = 0 ; i<maxLen ; i++ ) {
			PlainPair<Long> pair = new PlainPair<Long>(i, (long)i);
			node.add(pair, false);
		}

		assertThat(node.isOverflow(), is(true));

		int midLen = maxLen/2;

		SplitResult<Long> splitResult = node.split();
		PlainPair<Long> pair = (PlainPair<Long>) splitResult.getPair();
		Node<Long> newNode = splitResult.getNode();

		Vector<Pair> vector = node.vector;
		assertThat(vector.size(), is(midLen));
		for(int i = 0 ; i<midLen ; i++ ) {
			PlainPair<Long> tmp = (PlainPair<Long>)vector.get(i);
			assertThat(tmp.getKey(), is((long)i));
			assertThat(tmp.getNodeId(), is(i));
		}

		assertThat(pair.getNodeId(), is(Node.ROOT_ID));
		assertThat(node.getSpareNodeId(), is(maxLen/2));

		vector = newNode.vector;
		if( maxLen % 2 == 0 ) {
			assertThat(vector.size(), is(midLen - 1));
		} else {
			assertThat(vector.size(), is(midLen));
		}

		for(int i = midLen + 1 ; i<vector.size() ; i++ ) {
			PlainPair<Long> tmp = (PlainPair<Long>)vector.get(i);
			assertThat(tmp.getKey(), is((long)i));
			assertThat(tmp.getNodeId(), is(i));
		}
	}

	@Test
	public void papaChildKeyMapTest() {
		BPlusTreeMetaInfo papaChildKeyMap = new BPlusTreeMetaInfo(testArea, "test");
		papaChildKeyMap.load();

		papaChildKeyMap.setPapaId(0l, 1l);

		long papaId = papaChildKeyMap.getPapaId(0l);
		assertThat(papaId, is(1l));

		Integer nullPapaId = papaChildKeyMap.getPapaId(19999l);
		assertThat(nullPapaId, nullValue() );

		papaChildKeyMap.nextLongId();


		long s, e;

		s = System.currentTimeMillis();
		for(long i = 5 ; i<100000 ; i++ ) {
			papaChildKeyMap.setPapaId(i, 100000 + i);
		}
		e = System.currentTimeMillis();

		papaChildKeyMap.flush();
		System.out.println((e-s));
	}
}
