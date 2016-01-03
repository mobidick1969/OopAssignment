package fakecoupangsystem.database.config;

import fakecoupangsystem.database.annotation.Column;
import fakecoupangsystem.database.annotation.Data;
import fakecoupangsystem.database.annotation.Index;
import fakecoupangsystem.database.annotation.Key;
import fakecoupangsystem.database.exception.WrongDataDefinitionException;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Coupang on 2015. 12. 30..
 */
public class Catalog implements Serializable {

	private Class tableType;
	private String tableName;
	private String keyColumn;
	private List<String> indexColumns;
	private String[] columns;
	private File tableRoot;
	private Map<String, Class> columnTypeMap;
	private Map<String, Class> listColumnTypeMap;

	private Catalog() {
		indexColumns = new ArrayList<>(5);
		columnTypeMap = new TreeMap<>();
		listColumnTypeMap = new TreeMap<>();
	}

	public static Catalog createCatalog(Class clazz, File tableRoot) {
		Catalog catalog = new Catalog();
		Data dataAnno = (Data)clazz.getAnnotation(Data.class);
		catalog.tableName = dataAnno.name();
		catalog.tableType = clazz;

		readyInsertColumn(clazz, catalog);
		addKeyAndIndex(clazz, catalog);
		addFieldType(clazz, catalog);

		catalog.tableRoot = tableRoot;

		if( !valid(catalog) ) {
			throw new WrongDataDefinitionException();
		}

		return catalog;
	}

	private static void addFieldType(Class clazz, Catalog catalog) {
		for(Field field : clazz.getDeclaredFields() ) {
			catalog.columnTypeMap.put(field.getName(), field.getType());
			if( field.getType().equals(List.class) ) {
				ParameterizedType paramType = (ParameterizedType) field.getGenericType();
				Class<?> paramClass = (Class<?>) paramType.getActualTypeArguments()[0];
				catalog.listColumnTypeMap.put(field.getName(), paramClass);
			}
		}
	}

	private static boolean valid(Catalog catalog) {
		if( catalog.keyColumn == null ) {
			return false;
		}

		if( catalog.tableRoot == null ) {
			return false;
		}

		return true;
	}

	private static void addKeyAndIndex(Class clazz, Catalog catalog) {
		for(Field field : clazz.getDeclaredFields() ) {
			String fieldName = field.getName();

			Column column = field.getAnnotation(Column.class);
			if( column != null ) {
				//duplicate column order
				if( column.order() >= catalog.columns.length || catalog.columns[column.order()] != null ) {
					throw new WrongDataDefinitionException();
				}
				catalog.columns[column.order()] = fieldName;
			}

			Index index = field.getAnnotation(Index.class);
			if( index != null ) {
				catalog.indexColumns.add(fieldName);
			}

			Key key = field.getAnnotation(Key.class);
			if( key != null ) {
				catalog.keyColumn = fieldName;
				catalog.indexColumns.add(fieldName);
			}
		}
	}

	private static void readyInsertColumn(Class clazz, Catalog catalog) {
		int columnCount = 0;

		for(Field field : clazz.getDeclaredFields() ) {
			if( field.getAnnotation(Column.class) != null ) {
				columnCount++;
			}
		}

		catalog.columns = new String[columnCount];
	}

	public String getTableName() {
		return tableName;
	}

	public String getKeyColumn() {
		return keyColumn;
	}

	public List<String> getIndexColumns() {
		return indexColumns;
	}

	public String[] getColumns() {
		return columns;
	}

	public File getTableRoot() {
		return tableRoot;
	}

	public Map<String, Class> getColumnTypeMap() {
		return columnTypeMap;
	}

	public Map<String, Class> getListColumnTypeMap() {
		return listColumnTypeMap;
	}

	public Class getTableType() {
		return tableType;
	}
}
