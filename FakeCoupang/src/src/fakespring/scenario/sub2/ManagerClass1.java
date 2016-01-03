package fakespring.scenario.sub2;

import fakespring.annotation.*;
import fakespring.scenario.sub.MetaClass1;
import fakespring.scenario.sub.subsub.InjectBean;

/**
 * Created by Coupang on 2016. 1. 1..
 */
@Manager(MetaClass1.class)
public class ManagerClass1 {

	@Autowired
	private InjectBean injectBean;

	public InjectBean getInjectBean() {
		return injectBean;
	}

	@Meta
	public void injectMeta(MetaClass1 metaClass1) {
		System.out.println("Manager Class " + this + " | Meta injected " + metaClass1);
	}

	@PostConstruct
	public void preConstruct() {
		System.out.println("Manager Class " + this + " | Post Construct");
	}

	@PreDestroy
	public void preDestroy() {
		System.out.println("Manager Class " + this + " | Pre Destroy");
	}
}
