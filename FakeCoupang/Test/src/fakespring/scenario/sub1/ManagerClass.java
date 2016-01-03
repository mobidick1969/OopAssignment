package fakespring.scenario.sub1;

import fakespring.annotation.Manager;
import fakespring.annotation.PostConstruct;
import fakespring.annotation.Meta;
import fakespring.annotation.PreDestroy;
import fakespring.scenario.sub2.MetaClass;

/**
 * Created by Coupang on 2016. 1. 1..
 */
@Manager(MetaClass.class)
public class ManagerClass {

	private String name;

	public ManagerClass() {}

	public String getName() {
		return name;
	}

	@Meta
	public void injectMeta(MetaClass metaClass) {
		System.out.println("Manager Class " + this + " | Meta injected " + metaClass);
	}

	private void privateMethod() {
		System.out.println("Manager Class " + this + " | Private Method");
	}

	@PostConstruct
	public void preConstruct() {
		System.out.println("Manager Class " + this + " | Post Construct");
	}

	@PreDestroy
	private void preDestroy() {
		System.out.println("Manager Class " + this + " | Pre Destroy");
	}
}
