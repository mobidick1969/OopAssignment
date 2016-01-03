package fakecoupangsystem.database;

import java.util.List;

/**
 * Created by Coupang on 2015. 12. 30..
 */
public interface FakeDatabase {
	<T> T getOne(T object, Class<T> clazz);
	<T> T getOne(String fieldName, String value, Class<T> clazz);
	<T> List<T> getList(String fieldName, String value, Class<T> clazz);
	<T> void insert(List list, Class<T> clazz);
	void insert(Object obj);
	void update(Object object, String field, Object fieldValue);
}
