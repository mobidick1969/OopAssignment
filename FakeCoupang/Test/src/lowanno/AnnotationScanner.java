package lowanno;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.URL;
import java.util.Enumeration;
import java.util.Stack;

/**
 * Created by Coupang on 2016. 1. 1..
 */
public class AnnotationScanner {
	public static void componentScan(String packageName, Class<?> annotationClass, ScanningAction scanningAction)
		throws IOException, ClassNotFoundException {
		packageName = packageName.trim();
		String urlPackage = packageName.replaceAll("\\.", "/").trim();

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> urlEnumeration = classLoader.getResources(urlPackage);

		//URL to FILE transformation
		Stack<File> dirStack = new Stack<File>();
		while(urlEnumeration.hasMoreElements() ) {
			URL url = urlEnumeration.nextElement();
			File dir = new File(url.getFile() );
			if( dir.isDirectory() ) {
				dirStack.push(dir);
			}
		}

		//Actual class loading and check
		while( !dirStack.isEmpty() ) {
			File dir = dirStack.pop();
			File[] listFiles = dir.listFiles();

			if( listFiles == null && ( listFiles != null && listFiles.length <= 0 ) ) {
				continue;
			}

			for(File f : listFiles ) {
				if( f.isDirectory() ) {
					dirStack.push(f);
				} else if( f.getName().endsWith(".class") ) {

					String canonicalPath = f.getCanonicalPath();
					int startIdx = canonicalPath.indexOf(urlPackage);
					String classNameIncludePack = canonicalPath.substring(startIdx)
							.replace("/", ".")
							.replace(".class", "").trim();

					Class gotClass = Class.forName(classNameIncludePack);
					if( hasAnnotation(gotClass, annotationClass) ) {
						scanningAction.action(gotClass, annotationClass);
					}
				}
			}
		}
	}

	public static boolean hasAnnotation(Class<?> clazz, Class<?> annotationClass) {

		if( clazz == null || annotationClass == null ) {
			return false;
		}

		if( clazz.getAnnotation((Class<? extends Annotation>)annotationClass) != null ) {
			return true;
		}

		Stack<Class<?> > annoStack = new Stack<Class<?> >();

		for(Annotation anno : clazz.getAnnotations() ) {
			Class tmp = anno.annotationType();
			if( tmp.isAssignableFrom(Target.class) || tmp.isAssignableFrom(Retention.class) ) {
				continue;
			}

			annoStack.push(tmp);
		}

		while(!annoStack.isEmpty() ) {
			Class annoGot = annoStack.pop();

			if( annoGot.getAnnotation((Class<? extends Annotation>)annotationClass) != null ) {
				return true;
			}

			for(Annotation anno : annoGot.getAnnotations() ) {
				Class tmp = anno.annotationType();
				if( tmp.isAssignableFrom(Target.class) || tmp.isAssignableFrom(Retention.class) ) {
					continue;
				}

				annoStack.push(tmp);
			}
		}

		return false;
	}
}
