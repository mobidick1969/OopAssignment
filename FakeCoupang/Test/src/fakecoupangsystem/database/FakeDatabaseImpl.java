package fakecoupangsystem.database;

import fakecoupangsystem.database.annotation.Data;
import fakecoupangsystem.database.config.Catalog;
import fakecoupangsystem.database.config.DatabaseConfig;
import fakecoupangsystem.database.table.BplustreeFileSystemTableManager;
import fakecoupangsystem.database.table.TableManager;
import fakespring.annotation.Manager;
import fakespring.annotation.Meta;
import fakespring.annotation.PostConstruct;
import fakespring.annotation.PreDestroy;
import lowanno.AnnotationScanner;
import lowanno.ScanningAction;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Coupang on 2016. 1. 2..
 */
@Manager(DatabaseConfig.class)
public class FakeDatabaseImpl implements FakeDatabase {

	private DatabaseConfig databaseConfig;
	private Map<Class, TableManager> tableMap;

	public FakeDatabaseImpl() {
		tableMap = new HashMap<>();
	}

	@Meta
	private void setDatabaseConfig(DatabaseConfig databaseConfig) {
		this.databaseConfig = databaseConfig;
	}

	@PostConstruct
	private void init() throws IOException, ClassNotFoundException {
		//make database root
		makeRoot();
		//comonent scan
		componentScan();
	}

	private void componentScan() throws IOException, ClassNotFoundException {
		String packName = databaseConfig.getBasePackage();
		AnnotationScanner.componentScan(packName, Data.class, new ScanningAction() {
			@Override
			public void action(Class<?> targetClass, Class<?> annotationClass) {
				try {
					//make table manager
					File tableRoot = new File(databaseConfig.getRootDir(), targetClass.getSimpleName());
					Catalog catalog = Catalog.createCatalog(targetClass, tableRoot);
					TableManager tableManager = new BplustreeFileSystemTableManager(catalog);
					tableMap.put(targetClass, tableManager);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void makeRoot() {
		File root = databaseConfig.getRootDir();
		root.mkdirs();
	}

	@PreDestroy
	private void destroy() {
		for( Map.Entry<Class, TableManager> entry : tableMap.entrySet() ) {
			entry.getValue().flush();
		}
	}

	@Override
	public <T> T getOne(T object, Class<T> clazz) {
		TableManager tableManager = tableMap.get(clazz);
		return (T)tableManager.getByKey(object);
	}

	@Override
	public <T> T getOne(String fieldName, String value, Class<T> clazz) {
		TableManager tableManager = tableMap.get(clazz);
		List list = tableManager.getByCondition(fieldName, value);
		if( list == null || list.isEmpty() ) {
			return null;
		}

		return (T)list.get(0);
	}

	@Override
	public <T> List<T> getList(String fieldName, String value, Class<T> clazz) {
		TableManager tableManager = tableMap.get(clazz);

		List list =
		tableManager.getByCondition(fieldName, value);
		if( list == null ) {
			return Collections.emptyList();
		}

		return list;
	}

	@Override
	public void insert(Object obj) {
		TableManager tableManager = tableMap.get(obj.getClass());
		tableManager.insert(obj);
	}

	@Override
	public void update(Object object, String field, Object fieldValue) {
		TableManager tableManager = tableMap.get(object.getClass());
		tableManager.update(object, field, fieldValue);
	}

	@Override
	public <T> void insert(List list, Class<T> clazz) {
		TableManager tableManager = tableMap.get(clazz);
		tableManager.insert(list);
	}
}
