package fakespring.exception;

/**
 * Created by Coupang on 2016. 1. 1..
 */
public class MetaInfoNotFoundException extends RuntimeException {
	public MetaInfoNotFoundException() {
		super("Meta info class not found. Do you added @Bean to meta class?? You have to add @Bean in order to manged by FakeSpring");
	}
}
