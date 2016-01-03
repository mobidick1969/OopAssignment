package fakecoupangsystem.service;


import fakecoupangsystem.domain.Order;
import fakecoupangsystem.domain.dto.OrderDto;

import java.util.List;

/**
 * Created by Coupang on 2015. 12. 26..
 */
public interface LookupService {
	List<OrderDto> lookupByCoupangId(String coupangId);
	OrderDto lookupByOrderId(long orderId);
}
