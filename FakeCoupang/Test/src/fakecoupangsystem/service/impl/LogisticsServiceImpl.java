package fakecoupangsystem.service.impl;

import fakecoupangsystem.dao.LogisticsDao;
import fakespring.annotation.Autowired;
import fakecoupangsystem.service.LogisticsService;
import fakecoupangsystem.service.OrderService;
import fakespring.annotation.Bean;
import fakecoupangsystem.domain.Order;

import java.util.Collections;
import java.util.List;

/**
 * Created by Coupang on 2015. 12. 26..
 */
@Bean
public class LogisticsServiceImpl implements LogisticsService {

	@Autowired
	private LogisticsDao logisticsDao;

	public LogisticsServiceImpl() {	}

	@Override
	public void updateLogisticsStatus(Order order) {
		logisticsDao.updateStatus(order.getOrderId(), order.getDeliverStatus());
	}
}
