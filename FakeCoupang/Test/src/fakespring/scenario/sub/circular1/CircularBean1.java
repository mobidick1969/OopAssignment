package fakespring.scenario.sub.circular1;

import fakespring.scenario.sub.circular.CircularBean;
import fakespring.annotation.Autowired;
import fakespring.annotation.Bean;

/**
 * Created by Coupang on 2016. 1. 1..
 */
@Bean
public class CircularBean1 {
	@Autowired
	private CircularBean circularBean;

	public CircularBean getCircularBean() {
		return circularBean;
	}
}
