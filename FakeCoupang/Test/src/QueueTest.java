import fakecoupangsystem.dao.impl.TestOrderDaoImpl;
import fakecoupangsystem.datastructure.Queue;
import fakecoupangsystem.datastructure.impl.CircularPendingQueue;
import fakecoupangsystem.datastructure.impl.FixedCachingQueue;
import fakecoupangsystem.domain.Order;
import fakecoupangsystem.exception.UnderFlowException;
import org.junit.Test;

import java.nio.BufferUnderflowException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Coupang on 2015. 12. 27..
 */
public class QueueTest {
	//@Test
	public void test1() {
		Random rand = new Random();
		Queue queue = new CircularPendingQueue(100);

		List<Long> list = new LinkedList<Long>();

		for(int i = 0 ; i<10000000 ; i++ ) {
			Order order = TestOrderDaoImpl.makeFakeOrder(i);
			if( rand.nextBoolean() ) {
				list.add(order.getOrderId());
				queue.enqueue(order);
			} else {
				try {
					Order got = queue.dequeue();
					long num = list.get(0); list.remove(0);
					assertThat(got.getOrderId(), is(num));
				} catch(BufferUnderflowException e) {
				}
			}
		}
	}

	@Test
	public void cacheQueueTest() {
		Queue queue = new FixedCachingQueue(20);
		for(int i = 0 ; i<100 ; i++ ) {
			Order order = new Order();
			order.setOrderId((long)i);
			queue.enqueue(order);
		}

		assertThat(queue.size(), is(20));

		while(true) {
			try {
				Order order = queue.dequeue();
			} catch(UnderFlowException e) {
				break;
			}
		}

		assertThat(queue.size(), is(0));
	}
}
