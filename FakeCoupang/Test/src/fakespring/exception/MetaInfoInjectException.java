package fakespring.exception;

/**
 * Created by Coupang on 2016. 1. 1..
 */
public class MetaInfoInjectException extends RuntimeException {
	public MetaInfoInjectException() {
		super("Meta info inject method not found or wrong parameter type. Did you made method with @Meta annotation?");
	}
}
