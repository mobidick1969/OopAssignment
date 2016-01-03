package fakespring.exception;

/**
 * Created by Coupang on 2016. 1. 1..
 */
public class BeanNotFoundException extends RuntimeException {
	public BeanNotFoundException(Class injectBean, Class expectedBean) {
		super("Error occured while building " + injectBean + " Bean Not Found! At least [ " + expectedBean + " ] expected.");
	}
}
