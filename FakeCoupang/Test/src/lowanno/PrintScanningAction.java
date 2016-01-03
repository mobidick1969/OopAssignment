package lowanno;

/**
 * Created by Coupang on 2016. 1. 1..
 */
public class PrintScanningAction implements ScanningAction {
	@Override
	public void action(Class<?> targetClass, Class<?> annotationClass) {
		System.out.println("Target Class : " + targetClass + " Annotation Class : " + annotationClass);
	}
}
