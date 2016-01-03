package fakespring;

import fakespring.annotation.*;
import fakespring.exception.MetaInfoInjectException;
import fakespring.hook.HookType;
import lowanno.AnnotationScanner;
import lowanno.ScanningAction;
import fakespring.exception.BeanNotFoundException;
import fakespring.exception.MetaInfoNotFoundException;
import fakespring.hook.FakeSpringHook;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Coupang on 2016. 1. 1..
 */
public class AnnotationFakeSpring implements FakeSpring {

	private Map<HookType, List<FakeSpringHook>> hookMap;
	private List<Object> beanList;

	public AnnotationFakeSpring() {
		beanList = new ArrayList<>();
		hookMap = new TreeMap<>();

		for(HookType type : HookType.values() ) {
			hookMap.put(type, new ArrayList<FakeSpringHook>() );
		}

		//prevent resource loss
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				super.run();
				shutdown();
			}
		});

	}

	@Override
	public void assemble(String packageName) {
		collectBean(packageName);

		executeHook(HookType.PRE_ASSEMBLE);

		handlingManagerBean();
		injectBean();

		invokeMethod(PostConstruct.class);

		executeHook(HookType.POST_ASSEMBLE);
	}

	private void injectBean() {
		for(Object bean : beanList ) {
			Class beanClass = bean.getClass();
			//field scan
			Field[] fields = beanClass.getDeclaredFields();
			try {
				for(Field field : fields ) {
					//find from beanList
					if( field.getAnnotation(Autowired.class) != null ) {
						field = beanClass.getDeclaredField(field.getName());
						field.setAccessible(true);

						//set
						Class targetBean = field.getType();
						Object injectBean = getBean(targetBean);

						if( injectBean == null ) {
							throw new BeanNotFoundException(beanClass, targetBean);
						}
						field.set(bean, injectBean);
					}


				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private void executeHook(HookType type) {
		List<FakeSpringHook> hooks =
			hookMap.get(type);

		for(FakeSpringHook hook : hooks) {
			if( type == HookType.POST_SHUTDOWN ) {
				hook.act(null);
			} else {
				hook.act(this);
			}
		}
	}

	/*
		Find all classes have @Manager and inject according meta class
	 */
	private void handlingManagerBean() {
		for(Object bean : beanList ) {
			Class beanClass = bean.getClass();
			if( AnnotationScanner.hasAnnotation(beanClass, Manager.class) ) {
				Manager mngAnno = (Manager)beanClass.getAnnotation(Manager.class);
				Class metaClass = mngAnno.value();
				Object metaObject = getBean(metaClass);

				if( metaObject == null ) {
					throw new MetaInfoNotFoundException();
				}

				injectMeta(bean, metaObject);
			}
		}
	}

	private void injectMeta(Object bean, Object metaObject) {
		Class beanClass = bean.getClass();
		Method[] methods = beanClass.getDeclaredMethods();

		boolean injected = false;
		try {
			for(Method method : methods ) {
				if( method.getAnnotation(Meta.class) != null ) {
					method = beanClass.getDeclaredMethod(method.getName(),
						method.getParameterTypes());

					method.setAccessible(true);
					method.invoke(bean, metaObject);

					injected = true;
				}
			}
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}

		if( injected == false ) {
			throw new MetaInfoInjectException();
		}
	}

	private void invokeMethod(Class annotationClass, Object ... params) {
		for( Object bean : beanList ) {
			Class beanClass = bean.getClass();

			Method[] methods = beanClass.getDeclaredMethods();

			for(Method method : methods ) {
				if( method.getAnnotation((Class<? extends Annotation>)annotationClass) != null ) {
					try {
						method = beanClass.getDeclaredMethod(method.getName(),
							method.getParameterTypes());

						method.setAccessible(true);
						method.invoke(bean, params);
					} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void collectBean(String packageName) {
		//collect bean
		ScanningAction scanningAction = new ScanningAction() {
			@Override
			public void action(Class<?> targetClass, Class<?> annotationClass) {
			try {
				if( !targetClass.isAnnotation() && !targetClass.isInterface() ) {
					Object bean = targetClass.newInstance();
					beanList.add(bean);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
			}
		};

		try {
			AnnotationScanner.componentScan(packageName, Bean.class, scanningAction);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void shutdown() {
		executeHook(HookType.PRE_SHUTDOWN);

		invokeMethod(PreDestroy.class);

		executeHook(HookType.POST_SHUTDOWN);

		//clean the bean list
		beanList = Collections.emptyList();
	}

	@Override
	public <T> T getBean(Class<T> clazz) {
		for(Object bean : beanList) {
			if( clazz.isAssignableFrom(bean.getClass() ) ) {
				return (T)bean;
			}
		}

		return null;
	}

	@Override
	public void registerHook(FakeSpringHook hook, HookType type) {
		List<FakeSpringHook> hookList =
			hookMap.get(type);
		hookList.add(hook);
	}

}