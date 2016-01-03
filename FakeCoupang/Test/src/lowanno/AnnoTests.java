package lowanno;

import fakecoupangsystem.dao.LogisticsDao;
import fakecoupangsystem.dao.impl.CachableOrderDaoImpl;
import fakecoupangsystem.domain.Order;
import fakespring.annotation.Manager;
import fakespring.scenario.sub1.ManagerClass;
import fakespring.scenario.sub2.ManagerClass1;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Coupang on 2016. 1. 1..
 */
public class AnnoTests {

	/*
		어노테이션 끝까지 찾아가 해당 어노테이션이 부여 되었는지를 판단하는 테스트
	 */
	//@Test
	public void findPapaAnnotationTest() {

		assertThat(AnnotationScanner.hasAnnotation(TestTarget.class, Papa.class),
			is(true));

	}

	//@Test
	public void componentScanTest() throws IOException, ClassNotFoundException {
		ScanningAction printScanningAct = new PrintScanningAction();
		String packName = AnnoTests.class.getPackage().getName();

		AnnotationScanner.componentScan(packName, Papa.class, printScanningAct);
	}

	//@Test
	public void executeConstructorTest() throws IllegalAccessException, InvocationTargetException, InstantiationException {
		Class clazz = HavingConstructorClass.class;
		Constructor constructors[] = clazz.getConstructors();
		for(Constructor con : constructors ) {
			con.newInstance(13);
		}
	}

	//@Test
	public void inferValueFromAnnotationTest() {
		Class mngClass = ManagerClass.class;
		Class mngClass1 = ManagerClass1.class;

		Manager mngAnno = (Manager) mngClass.getAnnotation(Manager.class);
		System.out.println(mngAnno.value());

		mngAnno = (Manager)mngClass1.getAnnotation(Manager.class);
		System.out.println(mngAnno.value());

	}

	//@Test
	public void accessPrivateTest() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		ManagerClass managerClass = new ManagerClass();
		String injectString = "Hello World!";

		java.lang.reflect.Field[] fileds = ManagerClass.class.getDeclaredFields();
		for(Field field : fileds) {
			String fieldName = field.getName().trim();
			Field f = ManagerClass.class.getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(managerClass, injectString);
		}

		assertThat(managerClass.getName(), is(injectString));

		Method[] methods = ManagerClass.class.getDeclaredMethods();
		for(Method method : methods ) {
			String methodName = method.getName();
			System.out.println(methodName);

			Class[] paramClasses = method.getParameterTypes();

			if (paramClasses.length <= 0) {
				Method m = ManagerClass.class.getDeclaredMethod(methodName, paramClasses);
				m.setAccessible(true);
				m.invoke(managerClass, null);
			}
		}
	}

	//@Test
	public void typeInferTest() {
		Class intClass = int.class;
		System.out.println(intClass.isAssignableFrom(long.class));

	}

	//@Test
	public void genericTpyeInferTest() throws NoSuchMethodException, ClassNotFoundException {
		Class clazz = Order.class;
		for(Field field : clazz.getDeclaredFields() ) {
			if( field.getType().equals(List.class) ) {
				ParameterizedType paramType = (ParameterizedType) field.getGenericType();
				Class<?> paramClass = (Class<?>) paramType.getActualTypeArguments()[0];
				System.out.println(paramClass); // class java.lang.String.
			}
		}
	}

	@Test
	public void interfaceGetTypeTest() {
		Class interfaceClazz = LogisticsDao.class;
		Class classClazz = CachableOrderDaoImpl.class;

		System.out.println(interfaceClazz.isAssignableFrom(classClazz));
	}

}
