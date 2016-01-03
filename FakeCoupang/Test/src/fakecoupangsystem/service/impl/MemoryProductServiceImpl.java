package fakecoupangsystem.service.impl;

import fakecoupangsystem.domain.Product;
import fakecoupangsystem.service.ProductService;
import fakespring.annotation.Bean;
import fakespring.annotation.PostConstruct;

import java.util.Date;

/**
 * Created by Coupang on 2015. 12. 26..
 * Make Only 100 products in memory
 */
@Bean
public final class MemoryProductServiceImpl implements ProductService {

	private Product products[];

	public MemoryProductServiceImpl() {
		products = new Product[100];
		for(int i = 0 ; i<products.length ; i++ ) {
			products[i] = getRandomProduct(i);
		}
	}

	private Product getRandomProduct(final int id) {
		Product product = new Product();
		product.setName("name" + id);
		product.setMakeDate(new Date());
		product.setDescription("This Product is Goooooood!" + id);
		product.setDeliverCost(100*id);
		product.setCompany("KDY Company Sub " + id);
		product.setCost(10000*id);
		product.setId(id);

		return product;
	}

	@Override
	public Product getProduct(int id) {
		if( id >= 0 && id < 100 ) {
			return products[id];
		}
		return null;
	}

	@Override public Product newProduct(Product product) {
		throw new UnsupportedOperationException("상품 쪽 보다는 주문 처리에 신경 쓰기 위하여 과감히 생략하였습니다.");
	}

}
