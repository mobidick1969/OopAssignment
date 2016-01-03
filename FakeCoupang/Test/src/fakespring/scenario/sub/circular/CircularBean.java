package fakespring.scenario.sub.circular;

import fakespring.annotation.Autowired;
import fakespring.annotation.Bean;
import fakespring.scenario.sub.circular1.CircularBean1;

/**
 * Created by Coupang on 2016. 1. 1..
 */
@Bean
public class CircularBean {
	@Autowired
	private CircularBean1 circularBean1;

	public CircularBean1 getCircularBean1() {
		return circularBean1;
	}
}
