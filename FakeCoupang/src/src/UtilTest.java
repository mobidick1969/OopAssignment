import fakecoupangsystem.domain.Order;
import javafx.util.Pair;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Created by Coupang on 2015. 12. 27..
 */
public class UtilTest {
	//@Test
	public void propertiesTest() throws IOException {
		File target = new File("/tmp/kdytest");
		Properties properties = new Properties();
		properties.setProperty("a", "b");
		properties.setProperty("c", "d");

		properties.store(new FileOutputStream(target), "comment");


		properties = new Properties();
		properties.load(new FileInputStream(target));
		properties.setProperty("a", "bbb");
		properties.setProperty("e", "f");
		properties.store(new FileOutputStream(target), "comment");
	}

	//@Test
	public void pairTest() {
		Order order = new Order();
		order.setOrderId(1);
		String neo = "new";
		String update = "update";
		Set<Pair<Order, String>> set = new HashSet<Pair<Order, String>>();

		Pair<Order, String> pair = new Pair<Order, String>(order, neo);
		set.add(pair);

		order = new Order();
		order.setOrderId(1);

		pair = new Pair<Order, String>(order, update);

		System.out.println(set.contains(pair));
	}

	//@Test
	public void vectorTest() {
		Vector<String> vector = new Vector(100, -10);
		//vector.ensureCapacity(100);

		for(int i = 0 ; i<101 ; i++ ) {
			vector.add("" + i);
			if(vector.capacity() > 100 ) {
				System.out.println(i + " " + vector.capacity());
			}
		}

		vector.clear();

		System.out.println(vector.capacity() + " " + vector.size());

		vector.add(0, "hwhw");
		vector.add(1, "hwhw");

		assertThat(vector.size(), is(2));

	}

	//@Test
	public void stringTest() {
		String listStr = "_a,b,c_";
		listStr = listStr.replaceAll("_", "").trim();
		System.out.println(listStr);

		List<String> strList;
		List objList = null;
		strList = objList;
	}

	//@Test
	public void searchBinary() {
		int[] arr = new int[1024];
		Random rand = new Random();
		for(int i = 0 ; i<arr.length ; i++ ) {
			arr[i] = Math.abs(rand.nextInt(2000));
		}

		Arrays.sort(arr);

		int insert = 100000000;


		int startIdx = 0;
		int endIdx = arr.length - 1;
		int midIdx = -1;

		while( startIdx<endIdx ) {
			midIdx = (endIdx + startIdx)/2;

			System.out.println(startIdx + " " + endIdx + " " + midIdx);

			if( arr[midIdx] < insert ) {
				startIdx = midIdx + 1;
			} else if( arr[midIdx] > insert) {
				endIdx = midIdx - 1;
			} else {
				break;
			}

		}

		System.out.println("arr[midIdx] : " + arr[midIdx] + " , insert : " + insert);
		int pos = -1;

		if( arr[midIdx] < insert ) {
			for(int i = midIdx ; i<arr.length ; i++ ) {
				if( arr[i] >= insert ) {
					pos = i;
					break;
				}
			}

			if( pos == -1 ) {
				System.out.println("max value");
				return;
			}
		} else {
			for(int i = midIdx ; i>0 ; i--) {
				if( insert > arr[i-1] ) {
					pos = i;
					break;
				}
			}

			if( pos == -1 ) {
				System.out.println("min value");
				return;
			}
		}

		for(int i = pos - 3 ; i<pos + 3 ; i++ ) {
			if( i >= 0 && i<arr.length ) {
				System.out.println("arr[" + i + "] = " + arr[i]);
			}
		}
		System.out.println("found pos : " + pos + " , insert : " + insert + " , arr[pos] : " + arr[pos]);
	}

	@Test
	public void propertiesTest1() {
		Properties properties = new Properties();
		properties.setProperty("a", "b");
		properties.setProperty("c", "d");

		assertThat(properties.get("a").toString(), is("b"));
		assertThat(properties.get("c").toString(), is("d"));
	}
}
