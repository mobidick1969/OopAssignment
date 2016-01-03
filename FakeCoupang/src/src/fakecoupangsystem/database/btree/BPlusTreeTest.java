package fakecoupangsystem.database.btree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Coupang on 2015. 12. 29..
 */
public class BPlusTreeTest {

	File indexArea;

	//@Before
	public void before() throws IOException {
		indexArea = new File("/tmp/btreetest");

		File list[] = indexArea.listFiles();

		if( list == null || list.length == 0 ) {
			indexArea.mkdir();
			return;
		}

		Stack<File> stack = new Stack<File>();
		for(File f : list ) {
			if( f.isDirectory() ) {
				stack.push(f);
				File[] tmpList = f.listFiles();
				if( tmpList == null ) {
					f.delete();
				} else {
					for(File ff : tmpList) {
						stack.push(ff);
					}
				}
			} else {
				f.delete();
			}
		}
	}

	//@After
	public void after() throws IOException {
		File list[] = indexArea.listFiles();

		if( list == null || list.length == 0 ) {
			return;
		}

		Stack<File> stack = new Stack<File>();
		for(File f : list ) {
			if( f.isDirectory() ) {
				stack.push(f);
				for(File ff : f.listFiles()) {
					stack.push(ff);
				}
			} else {
				f.delete();
			}
		}
	}

	@Test
	public void basicTest() throws IOException, InterruptedException {
		int nodeSize = 1024;
		int itemNum = 100000;
//		BPlusTree<Long> tree = new BPlusTree<Long>(indexArea, "test", nodeSize);
		PapaKeyCacheBPlusTree<Long> tree = new PapaKeyCacheBPlusTree<Long>(indexArea, "test3", nodeSize, false);

		long[] arr = new long[itemNum];
		Random random = new Random();

		for(long i = 0 ; i<arr.length ; i++ ) {
			arr[(int)i] = i;
		}

		for(int i = 0 ; i<arr.length ; i++ ) {
			int j = random.nextInt(arr.length);

			long tmp = arr[i];
			arr[i] = arr[j];
			arr[j] = tmp;
		}

		long start, end, total;

		total = 0;
		for(int i = 0 ; i<itemNum ; i++ ) {
			long key = arr[i];

			if( i != 0 && i%100 == 0 )
				System.out.println("insert : " + i);

			Page page = new Page(new File("" + key), key);
			start = System.currentTimeMillis();
			tree.add(key, page);
			end = System.currentTimeMillis();
			total += end-start;
		}


		System.out.println("insert end!! " + total);


		total = 0;
		for(int i = 0 ; i<itemNum ; i++ ) {
			long key = arr[i];
			start = System.currentTimeMillis();
			try {
				Page page = tree.get(key);
				end = System.currentTimeMillis();
				total += end - start;
				assertThat(page.getPosition(), is(key));
				assertThat(page.getFile().getName(), is("" + key));
			} catch(Exception e) {
				System.out.println("Index : " + i + " , Key : " + key);
				e.printStackTrace();
			}
		}

		System.out.println("get end !! " + total);
	}

	//@Test
	public void fixedArrayInsertTest() throws IOException {
		int nodeSize = 10;
		int itemNum = 20;
		BPlusTree<Long> tree = new BPlusTree<Long>(indexArea, "test", nodeSize, false);

		long[] arr = {7, 17, 11, 19, 2, 12, 3, 8, 1, 10, 6, 14, 16, 0, 9, 13, 18, 15, 4, 5};

		long start, end, total;

		total = 0;
		for(int i = 0 ; i<itemNum ; i++ ) {
			long key = arr[i];

			if( i != 0 && i%100 == 0 )
				System.out.println("insert : " + i);

			Page page = new Page(new File("" + key), key);
			start = System.currentTimeMillis();
			tree.add(key, page);
			end = System.currentTimeMillis();
			total += end-start;
		}


		System.out.println("insert end!! " + total);

		for(long num : arr ) {
			System.out.print(num + " ");
		}
		System.out.println();

		total = 0;
		for(int i = 0 ; i<itemNum ; i++ ) {
			long key = arr[i];
			System.out.println("get : " + key);
			start = System.currentTimeMillis();
			Page page = tree.get(key);
			end = System.currentTimeMillis();
			total += end - start;
			System.out.println("got it !! " + (end-start));
			assertThat(page.getPosition(), is(key));
			assertThat(page.getFile().getName(), is("" + key));
		}
	}

	//@Test
	public void searchTwoWayNodeTest() {
		Node<Integer> node = new LeafNode<>(0, 1, 1200);

		int[] arr = new int[1024];
		Random rand = new Random();
		for(int i = 0 ; i<arr.length ; i++ ) {
			arr[i] = Math.abs(rand.nextInt(2000));
		}
		Arrays.sort(arr);

		for(int num : arr ) {
			LeafPair<Integer> pair = new LeafPair<>();
			pair.setKey(num);
			node.add(pair, false);
		}

		int insert = 223;

		int idx = node.findInsertPosition(insert);

		System.out.println(idx);

	}
}
