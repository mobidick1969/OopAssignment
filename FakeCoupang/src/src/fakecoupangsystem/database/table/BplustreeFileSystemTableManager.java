package fakecoupangsystem.database.table;

import fakecoupangsystem.database.btree.BPlusTree;
import fakecoupangsystem.database.btree.Page;
import fakecoupangsystem.database.annotation.Index;
import fakecoupangsystem.database.annotation.Key;
import fakecoupangsystem.database.btree.PapaKeyCacheBPlusTree;
import fakecoupangsystem.database.config.Catalog;
import fakecoupangsystem.database.exception.EmptyResultSetException;
import fakecoupangsystem.database.table.row.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Coupang on 2016. 1. 2..
 */
public class BplustreeFileSystemTableManager implements TableManager {

	private Catalog catalog;
	private File tableRoot;
	private File indexRoot;
	private File dataRoot;
	private Map<String, PapaKeyCacheBPlusTree> indexMap;
	private RowWriter rowWriter;
	private RowReader rowReader;

	public BplustreeFileSystemTableManager(Catalog catalog) throws IOException {
		this.catalog = catalog;
		tableRoot = catalog.getTableRoot();
		createArea();
		createIndex();
		createRowHander();
	}

	private void createRowHander() throws IOException {
		Translator translator = new ReflectionTranslator(catalog);
		rowWriter = new FileRowWriter(dataRoot, translator);
		rowReader = new FileRowReader(translator);
	}

	private void createIndex() throws IOException {
		indexMap = new TreeMap<String, PapaKeyCacheBPlusTree>();
		for(String fieldName : catalog.getIndexColumns() ) {
			boolean duplicate = !fieldName.equals(catalog.getKeyColumn());
			PapaKeyCacheBPlusTree bPlusTree = new PapaKeyCacheBPlusTree(indexRoot, fieldName, 500, duplicate);
			indexMap.put(fieldName, bPlusTree);
		}
	}

	private void createArea() {
		if( !tableRoot.exists() ) {
			tableRoot.mkdir();
		}

		indexRoot = new File(tableRoot, "index");
		if( !indexRoot.exists() ) {
			indexRoot.mkdir();
		}

		dataRoot = new File(tableRoot, "data");
		if( !dataRoot.exists() ) {
			dataRoot.mkdir();
		}
	}

	@Override
	public void flush() {
		//closing all bplus tree
		for(Map.Entry<String, PapaKeyCacheBPlusTree> entry : indexMap.entrySet()) {
			entry.getValue().close();
		}
	}

	@Override
	public void insert(Object object) {
		List list = new ArrayList(1);
		list.add(object);
		insertBulk(list);
	}

	@Override
	public void insertBulk(List list) {
		//write first
		List<Page> pages = rowWriter.write(list);

		//indexing
		for(int i = 0 ; i<list.size() ; i++ ) {
			Object object = list.get(i);
			Class clazz = object.getClass();
			for(Field field : clazz.getDeclaredFields() ) {
				if( field.getAnnotation(Key.class) != null || field.getAnnotation(Index.class) != null  ) {
					try {
						PapaKeyCacheBPlusTree bPlusTree = indexMap.get(field.getName());
						field = clazz.getDeclaredField(field.getName() );
						field.setAccessible(true);
						bPlusTree.add((Comparable) field.get(object), pages.get(i));
					} catch (IllegalAccessException | NoSuchFieldException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private Page findPageFromIndex(String fieldName, Object value) {
		PapaKeyCacheBPlusTree bPlusTree = indexMap.get(fieldName);
		if( bPlusTree != null ) {
			return bPlusTree.get((Comparable) value);
		}

		return null;
	}

	@Override
	public Object getByKey(Object object) {
		Page page = findPageFromIndex(catalog.getKeyColumn(), getKeyValueFromEntity(object));
		if( page == null ) {
			throw new EmptyResultSetException();
		}

		return rowReader.readRow(page, catalog.getTableType());
	}

	private Object getKeyValueFromEntity(Object entity) {
		Class clazz = entity.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(Key.class) != null) {
				try {
					field = clazz.getDeclaredField(field.getName());
					field.setAccessible(true);
					return field.get(entity);
				} catch (IllegalAccessException | NoSuchFieldException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	@Override
	public List getByCondition(String fieldName, Object fieldValue) {

		PapaKeyCacheBPlusTree bPlusTree = indexMap.get(fieldName);
		if( bPlusTree == null ) {
			//have to full scan
			throw new UnsupportedOperationException("Full Scan is not supported!! Lack of time to implement that functionality...");
		}

		Page page = findPageFromIndex(fieldName, fieldValue);
		List list = Collections.emptyList();

		if( page != null ) {
			list = new ArrayList(3);
			//single column
			if(fieldName.equals(catalog.getKeyColumn() ) ) {
				Object target = rowReader.readRow(page, catalog.getTableType());
				list.add(target);
			} else {
				List got = rowReader.readRows(page, catalog.getTableType());
				list.addAll(got);
			}
		}

		return list;
	}


	private int findOrder(final String fieldName) {
		for(int i = 0 ; i<catalog.getColumns().length ; i++ ) {
			if( catalog.getColumns()[i].equals(fieldName) ) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public void update(Object object, String fieldName, Object fieldValue) {
		Page page = findPageFromIndex(catalog.getKeyColumn(), getKeyValueFromEntity(object));

		String wrap = ReflectionTranslator.basicObjectToString(fieldValue);
		rowWriter.update(page, wrap, findOrder(fieldName) + 1, false);
	}
}