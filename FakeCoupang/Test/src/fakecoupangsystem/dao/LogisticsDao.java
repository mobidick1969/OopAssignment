package fakecoupangsystem.dao;

import fakecoupangsystem.domain.Order;

/**
 * Created by Coupang on 2015. 12. 26..
 */
public interface LogisticsDao {
	void updateStatus(long orderId, Order.DeliverStatus deliverStatus);
}
