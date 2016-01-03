package fakespring.scenario.sub1.interfacebean;

import fakespring.scenario.sub.subsub.subsubsub.SomeInterface;
import fakespring.annotation.Autowired;
import fakespring.annotation.Bean;

/**
 * Created by Coupang on 2016. 1. 1..
 */
@Bean
public class InterfaceInjectBean {
	@Autowired
	private SomeInterface someInterface;

	public SomeInterface getSomeInterface() {
		return someInterface;
	}
}
