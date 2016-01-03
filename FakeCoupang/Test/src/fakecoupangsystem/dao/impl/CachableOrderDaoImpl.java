package fakecoupangsystem.dao.impl;

import fakecoupangsystem.dao.LogisticsDao;
import fakecoupangsystem.dao.OrderDao;
import fakecoupangsystem.database.FakeDatabase;
import fakecoupangsystem.database.resources.FileKeyGeneratorImpl;
import fakecoupangsystem.database.resources.KeyGenerator;
import fakecoupangsystem.datastructure.Indexing;
import fakecoupangsystem.datastructure.Queue;
import fakecoupangsystem.datastructure.impl.CircularPendingQueue;
import fakecoupangsystem.datastructure.impl.FixedCachingQueue;
import fakecoupangsystem.domain.Order;
import fakecoupangsystem.launch.FakeCoupangConfig;
import fakespring.annotation.Autowired;
import fakespring.annotation.Bean;
import fakespring.annotation.PostConstruct;
import fakespring.annotation.PreDestroy;
import javafx.util.Pair;

import java.util.*;

/**
 * Created by Buzz on 2015. 12. 26..
 *
 * Added caching functionality
 * Because FileOrderDaoImpl io frequently leads to low performance!!
 * Low IO!! High Performance!!
 *
 * Accomplish Basic of Basic, Peace!
 */
@Bean
public class CachableOrderDaoImpl implements LogisticsDao, OrderDao {

	@Autowired
	private FakeDatabase fakeDatabase;

	@Autowired
	private FakeCoupangConfig fakeCoupangConfig;

	private int CACHING_SIZE;
	private int INSERT_PENDING_SIZE;
	private Queue cachingQueue;
	private Queue insertPending;
	private KeyGenerator keyGenerator;

	public CachableOrderDaoImpl() {
		this(20);
	}

	public CachableOrderDaoImpl(int cachingSize) {
		CACHING_SIZE = INSERT_PENDING_SIZE = cachingSize;
		cachingQueue = new FixedCachingQueue(CACHING_SIZE);
		insertPending = new CircularPendingQueue(INSERT_PENDING_SIZE);
	}

	private void flushIf() {
		if( insertPending.size() >= INSERT_PENDING_SIZE ) {
			List<Order> orders = new ArrayList<>(insertPending.size());
			for(int i = 0 ; i<insertPending.size() ; i++ ) {
				Order order = insertPending.dequeue();

				System.out.println(order.getCoupangId());
				orders.add(order);
			}

			fakeDatabase.insert(orders, Order.class);
		}
	}

	private void pending(Order order) {
		insertPending.enqueue(order);
		cachingQueue.enqueue(order);
	}

	@Override
	public void updateStatus(long orderId, Order.DeliverStatus deliverStatus) {
		Order order = new Order();
		order.setOrderId(orderId);

		fakeDatabase.update(order, "deliverStatus", deliverStatus);
	}

	@Override
	public void insertOrder(Order order) {
		//pending(order);
		//flushIf();
		fakeDatabase.insert(order);
	}

	@Override
	public long nextOrderId() {
		return keyGenerator.nextLongId();
	}

	@Override
	public List<Order> getOrdersByCoupangId(String coupangId) {
		List<Order> list =
		fakeDatabase.getList("coupangId", coupangId, Order.class);
		if( list == null ) {
			return Collections.emptyList();
		}

		return list;
	}

	@Override
	public Order getOrderById(long orderId) {

		//first find from caching queue
		//second find from pending queue
//		List<Order> list = cachingQueue.find(Indexing.ORDER_ID, orderId);
//		if( list == null || list.isEmpty() ) {
//			list = insertPending.find(Indexing.ORDER_ID, orderId);
//			if( list != null && !list.isEmpty() ) {
//				return list.get(0);
//			}
//		}
//

		Order order = new Order();
		order.setOrderId(orderId);

		return
		fakeDatabase.getOne(order, Order.class);
	}

	@PostConstruct
	private void loadKeyGenerator() {
		keyGenerator = new FileKeyGeneratorImpl(fakeCoupangConfig.getEtcDir(), "orderId");
		keyGenerator.load();
	}

	@PreDestroy
	private void storeKeyGenerator() {
		keyGenerator.flush();
	}
}
