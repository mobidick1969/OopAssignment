package fakecoupangsystem.database;

import fakecoupangsystem.database.btree.Page;
import fakecoupangsystem.database.config.Catalog;
import fakecoupangsystem.domain.Order;
import fakecoupangsystem.database.table.BplustreeFileSystemTableManager;
import fakecoupangsystem.database.table.TableManager;
import fakecoupangsystem.database.table.row.*;
import fakespring.AnnotationFakeSpring;
import fakespring.FakeSpring;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by Coupang on 2016. 1. 2..
 */
public class DatabaseTest {

	long sampleOrderId = 0;

	//@Test
	public void basicTest() {
		FakeSpring fakeSpring = new AnnotationFakeSpring();
		fakeSpring.assemble("fakecoupangsystem/database");

		FakeDatabase fakeDatabase = fakeSpring.getBean(FakeDatabase.class);
		assertThat(fakeDatabase, notNullValue() );
	}

	//@Test
	public void catalogMapTest() {
		Map<Class, String> classMap = new HashMap<Class, String>();
		classMap.put(String.class, "string");
		classMap.get(String.class);
	}


	//@Test
	public void translatorTest() {
		Catalog catalog = Catalog.createCatalog(Order.class, new File(""));
		Translator translator = new ReflectionTranslator(catalog);


		Order order = new Order();
		order.setCoupangId("coupangId");
		order.setDeliverStatus(Order.DeliverStatus.END);
		order.setOrdererName("ordererName");
		order.setOrderId(0);

		for(int i = 0 ; i<5 ; i++ ) {
			order.addProduct(i);
		}

		String translatedResult = translator.translateToString(order);
		System.out.println(translatedResult);

		Order got = translator.translateToObject(translatedResult, Order.class);
		assertThat(got.getCoupangId(), is("coupangId"));
		assertThat(got.getDeliverStatus(), is(Order.DeliverStatus.END));
		assertThat(got.getOrdererName(), is("ordererName"));
		assertThat(got.getOrderId(), is(0l));

		List<Integer> gotProducts = got.getProducts();
		for(int i = 0 ; i<gotProducts.size() ; i++ ) {
			assertThat(gotProducts.get(i), is(i));
		}
	}

	//@Test
	public void rowWriteReadTest() throws IOException {
		Catalog catalog = Catalog.createCatalog(Order.class, new File(""));
		Translator translator = new ReflectionTranslator(catalog);

		Order order = getSampleOrder();

		File dataDir = new File("/tmp/fakecoupang/SampleOrder/data");
		dataDir.mkdirs();

		RowWriter rowWriter = new FileRowWriter(dataDir, translator);
		Page page = rowWriter.write(order);

		RowReader rowReader = new FileRowReader(translator);
		order = rowReader.readRow(page, Order.class);

		validSampleOrder(order, 0l);
	}

	public void validSampleOrder(Order order, long sampleOrderId) {
		assertThat(order.getCoupangId(), is("coupangId" + sampleOrderId));
		assertThat(order.getDeliverStatus(), is(Order.DeliverStatus.END));
		assertThat(order.getOrdererName(), is("ordererName" + sampleOrderId));
		assertThat(order.getOrderId(), is(sampleOrderId));

		List<Integer> gotProducts = order.getProducts();
		for(int i = 0 ; i<gotProducts.size() ; i++ ) {
			assertThat(gotProducts.get(i), is(i));
		}
	}

	public Order getSampleOrder() {
		Order order = new Order();
		order.setCoupangId("coupangId" + sampleOrderId);
		order.setDeliverStatus(Order.DeliverStatus.END);
		order.setOrdererName("ordererName" + sampleOrderId);
		order.setOrderId(sampleOrderId);

		for(int i = 0 ; i<5 ; i++ ) {
			order.addProduct(i);
		}

		sampleOrderId++;
		return order;
	}

	//@Test
	public void tableManagerTest() throws IOException {
		Catalog catalog = Catalog.createCatalog(Order.class, new File("/tmp/fakecoupang/SampleOrder"));
		TableManager tableManager = new BplustreeFileSystemTableManager(catalog);

		long s,e;
		long in = 0;
		long t = 0;
		int elemNum = 1000;


		for(int i = 0 ; i<elemNum/2 ; i++ ) {
			if( i > 0 && i%100 == 0 ) {
				System.out.println("Insert " + i);
			}
			Order order = getSampleOrder();
			s = System.currentTimeMillis();
			tableManager.insert(order);
			e = System.currentTimeMillis();
			in += e-s;
		}

		System.out.println("Insert Single Total : " + in);

		List<Order> orders = new ArrayList<>();
		for(int i = 0 ; i<elemNum/2 ; i++ ) {
			orders.add(getSampleOrder() );
		}
		s = System.currentTimeMillis();
		tableManager.insertBulk(orders);
		e = System.currentTimeMillis();
		in += (e-s);

		System.out.println("Insert Bulk Total : " + (e-s));

		System.out.println("Insert Complete!! Time : " + in);


		for(int i = 0 ; i<elemNum ; i++ ) {
			if( i > 0 && i%100 == 0 ) {
				System.out.println("Get " + i);
			}
			Order order = new Order();
			order.setOrderId(i);
			s = System.currentTimeMillis();
			Order got = (Order) tableManager.getByKey(order);
			e = System.currentTimeMillis();
			t = e-s;
			validSampleOrder(got, (long)i);
		}

		System.out.println("Get Complete!! Total : " + t);
		System.out.println("Get Complete!! Average : " + (((double)t)/elemNum));

		Order change = new Order();
		change.setOrderId(10);

		tableManager.update(change, "deliverStatus", Order.DeliverStatus.ING);

		Order got = (Order) tableManager.getByKey(change);
		assertThat(got.getDeliverStatus(), is(Order.DeliverStatus.ING));
	}

	//@Test
	public void integratedTest() throws IOException {
		File f = new File("/tmp/testresult");
		f.createNewFile();
		FileWriter writer = new FileWriter(f);

		//spring
		FakeSpring fakeSpring = new AnnotationFakeSpring();
		fakeSpring.assemble("fakecoupangsystem.database");

		FakeDatabase fakeDatabase = fakeSpring.getBean(FakeDatabase.class);
		assertThat(fakeDatabase, notNullValue() );

		//1만
		int elemNum = 1000;
		long s,e,total;

		total = 0;
		for(int i = 0 ; i<elemNum ; i++ ) {
			if( i > 0 && i%100 == 0 ) {
				System.out.println("Insert " + i);
			}
			Order order = getSampleOrder();
			s = System.currentTimeMillis();
			fakeDatabase.insert(order);
			e = System.currentTimeMillis();
			total += e-s;
		}

		writer.write("Insert Result Total : " + (total) + "ms\n");
		writer.write("Insert Average : " + ((double)total/elemNum) + "ms\n");


		total = 0;
		for(int i = 0 ; i<elemNum ; i++ ) {
			if( i > 0 && i%100 == 0 ) {
				System.out.println("Get " + i);
			}
			Order order = new Order();
			order.setOrderId(i);
			s = System.currentTimeMillis();
			Order got = fakeDatabase.getOne(order, Order.class);
			e = System.currentTimeMillis();
			total += e-s;
			validSampleOrder(got, (long)i);
		}

		writer.write("Query Result Total : " + (total) + "ms\n");
		writer.write("Query Average : " + ((double)total/elemNum) + "ms\n");

		fakeSpring.shutdown();

		writer.close();
	}

	//@Test
	public void persistenceCheck() {
		int elemNum = 1000;

		//spring
		FakeSpring fakeSpring = new AnnotationFakeSpring();
		fakeSpring.assemble("fakecoupangsystem.database");

		FakeDatabase fakeDatabase = fakeSpring.getBean(FakeDatabase.class);
		assertThat(fakeDatabase, notNullValue() );

		for(int i = 0 ; i<elemNum ; i++ ) {
			if( i > 0 && i%100 == 0 ) {
				System.out.println("Get " + i);
			}
			Order order = new Order();
			order.setOrderId(i);
			Order got = fakeDatabase.getOne(order, Order.class);
			validSampleOrder(got, (long)i);
		}

		fakeSpring.shutdown();
	}

	@Test
	public void duplicateInsertTest() {
		//spring
		FakeSpring fakeSpring = new AnnotationFakeSpring();
		fakeSpring.assemble("fakecoupangsystem.database");

		FakeDatabase fakeDatabase = fakeSpring.getBean(FakeDatabase.class);
		assertThat(fakeDatabase, notNullValue() );

		//1만
		int elemNum = 100;
		int userCount = 20;

		for(int i = 0 ; i<userCount ; i++ ) {
			for(int j = 0 ; j<elemNum/userCount ; j++ ) {
				System.out.println("Insert coupangId" + i);
				Order order = getSampleOrder();
				order.setCoupangId("coupangId" + i);
				fakeDatabase.insert(order);
			}
		}

		for(int i = 0 ; i<userCount ; i++ ) {
			System.out.println("Get coupangId" + i);
			List<Order> orders = fakeDatabase.getList("coupangId", "coupangId" + i, Order.class);
			assertThat(orders.size(), is(elemNum / userCount));

			for( Order got : orders ) {
				assertThat(got.getCoupangId(), is("coupangId" + i));
				assertThat(got.getDeliverStatus(), is(Order.DeliverStatus.END));
				assertThat(got.getOrdererName(), is("ordererName" + got.getOrderId()));
				assertThat(got.getOrderId(), is(got.getOrderId()));

				List<Integer> gotProducts = got.getProducts();
				for(int k = 0 ; k<gotProducts.size() ; k++ ) {
					assertThat(gotProducts.get(k), is(k));
				}
			}
		}

		fakeSpring.shutdown();
	}
}

