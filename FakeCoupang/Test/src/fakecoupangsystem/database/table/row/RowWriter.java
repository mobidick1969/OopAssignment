package fakecoupangsystem.database.table.row;


import fakecoupangsystem.database.btree.Page;

import java.util.List;

/**
 * Created by Coupang on 2016. 1. 2..
 */
public interface RowWriter {
	char Delimiter = ';';
	char SecondDelimiter = ',';
	char ListOpener = '_';

	<T> List<Page> write(List<T> entitys);
	<T> Page write(T entity);
	void update(Page page, String data, int field, boolean variable);
}
