package fakecoupangsystem;

import fakecoupangsystem.launch.FakeCoupangSystem;
import fakespring.AnnotationFakeSpring;
import fakespring.FakeSpring;

import java.io.IOException;

/**
 * Created by Coupang on 2016. 1. 3..
 */
public class Main {
	public static void main(String ... args) throws IOException {
		FakeSpring fakeSpring = new AnnotationFakeSpring();
		fakeSpring.assemble("fakecoupangsystem");

		FakeCoupangSystem fakeCoupangSystem = fakeSpring.getBean(FakeCoupangSystem.class);
		fakeCoupangSystem.launch();

		fakeSpring.shutdown();
	}
}
