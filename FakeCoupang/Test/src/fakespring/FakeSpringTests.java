package fakespring;

import fakespring.hook.FakeSpringHook;
import fakespring.hook.HookType;
import fakespring.scenario.sub.circular.CircularBean;
import fakespring.scenario.sub.circular1.CircularBean1;
import fakespring.scenario.sub1.ManagerClass;
import fakespring.scenario.sub1.interfacebean.InterfaceInjectBean;
import fakespring.scenario.sub2.ManagerClass1;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Coupang on 2016. 1. 1..
 */
public class FakeSpringTests {
	@Test
	public void beanScanTest() throws IOException, ClassNotFoundException {
		FakeSpring fakeSpring =
			new AnnotationFakeSpring();

		for(HookType type : HookType.values() ) {
			final String typeStr = type.toString();
			fakeSpring.registerHook(new FakeSpringHook() {
				@Override
				public void act(FakeSpring fakeSpring) {
					System.out.println(typeStr + " " + fakeSpring);
				}
			}, type);
		}

		fakeSpring.assemble("fakespring.scenario");
		ManagerClass managerClass = fakeSpring.getBean(ManagerClass.class);
		assertThat(managerClass, notNullValue());

		ManagerClass1 managerClass1 = fakeSpring.getBean(ManagerClass1.class);
		assertThat(managerClass1, notNullValue());

		MatcherAssert.assertThat(managerClass1.getInjectBean(), notNullValue());

		//circular inject test
		CircularBean circularBean = fakeSpring.getBean(CircularBean.class);
		CircularBean1 circularBean1 = fakeSpring.getBean(CircularBean1.class);

		MatcherAssert.assertThat(circularBean.getCircularBean1(), notNullValue());
		MatcherAssert.assertThat(circularBean1.getCircularBean(), notNullValue());

		//interface inject test
		InterfaceInjectBean interfaceInjectBean = fakeSpring.getBean(InterfaceInjectBean.class);
		MatcherAssert.assertThat(interfaceInjectBean.getSomeInterface(), notNullValue());

		fakeSpring.shutdown();
	}

	@Test
	public void shutdownHookTest() throws IOException {
		FakeSpring fakeSpring =
			new AnnotationFakeSpring();

		for(HookType type : HookType.values() ) {
			final String typeStr = type.toString();
			fakeSpring.registerHook(new FakeSpringHook() {
				@Override
				public void act(FakeSpring fakeSpring) {
					System.out.println(typeStr + " " + fakeSpring);
				}
			}, type);
		}

		System.out.println("Press Ctrl + c ");
		System.in.read();
	}
}
