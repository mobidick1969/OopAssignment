package fakecoupangsystem.database.table.row;

import fakecoupangsystem.database.annotation.Column;
import fakecoupangsystem.database.config.Catalog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coupang on 2016. 1. 2..
 */
public class ReflectionTranslator implements Translator {

	private Catalog catalog;

	public ReflectionTranslator(Catalog catalog) {
		this.catalog = catalog;
	}

	@Override
	public String translateToString(Object target) {
		String[] values = new String[catalog.getColumns().length];
		Class clazz = target.getClass();

		for(Field f : clazz.getDeclaredFields()) {
			Column column = f.getAnnotation(Column.class);
			if( column != null ) {
				try {
					f = clazz.getDeclaredField(f.getName() );
					f.setAccessible(true);
					Object value = f.get(target);
					if( List.class.isAssignableFrom(value.getClass() ) ) {
						values[column.order()] = listToString((List)value);
					} else {
						values[column.order()] = value.toString();
					}

				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		StringBuffer buffer = new StringBuffer();
		for(int i = 0 ; i<values.length ; i++ ) {
			buffer.append(( i > 0 ? RowWriter.Delimiter : "" ) + values[i]);
		}

		return buffer.toString().trim();
	}

	private String listToString(List list) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(RowWriter.ListOpener);
		for(int i = 0 ; i<list.size() ; i++ ) {
			buffer.append(( i > 0 ? RowWriter.SecondDelimiter : "" ) + list.get(i).toString() );
		}
		buffer.append(RowWriter.ListOpener);

		return buffer.toString();
	}

	@Override
	public <T> T translateToObject(String string, Class<T> clazz) {

		T object = null;
		try {
			object = clazz.newInstance();

			String[] splited = getSplited(string, RowWriter.Delimiter);

			for(int i = 0 ; i<splited.length ; i++ ) {
				String fieldValueStr = splited[i];
				String fieldName = catalog.getColumns()[i];
				Class fieldClass = catalog.getColumnTypeMap().get(fieldName);

				setFieldValue(object, fieldName, fieldValueStr, fieldClass);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return object;
	}

	private void setFieldValue(Object target, String fieldName, String value, Class fieldClass) {
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);

			if( fieldClass.isEnum() ) {
				Object enumm = fieldClass.getEnumConstants()[Integer.parseInt(value)];
				field.set(target, enumm);
			} else if(fieldClass.isPrimitive()) {
				if( fieldClass.isAssignableFrom(int.class) ) {
					field.set(target, Integer.parseInt(value));
				} else if( fieldClass.isAssignableFrom(long.class) ) {
					field.set(target, Long.parseLong(value));
				} else if( fieldClass.isAssignableFrom(float.class) ) {
					field.set(target, Float.parseFloat(value));
				} else if( fieldClass.isAssignableFrom(double.class) ) {
					field.set(target, Double.parseDouble(value));
				} else if( fieldClass.isAssignableFrom(byte.class) ) {
					field.set(target, Byte.parseByte(value));
				}
			} else if( fieldClass.isAssignableFrom(String.class) ) {
				field.set(target, value);
			} else if( fieldClass.isAssignableFrom(List.class)) {
				value = value.replaceAll("" + RowWriter.ListOpener, "").trim();
				String[] listValues = getSplited(value, RowWriter.SecondDelimiter);
				Class paramClass = catalog.getListColumnTypeMap().get(fieldName);

				List newList = new ArrayList(listValues.length);
				for(String listValue : listValues ) {

					if( paramClass.isAssignableFrom(Integer.class) ) {
						newList.add(Integer.parseInt(listValue));
					} else if( paramClass.isAssignableFrom(Long.class) ) {
						newList.add(Long.parseLong(listValue));
					} else if( paramClass.isAssignableFrom(Float.class) ) {
						newList.add(Float.parseFloat(listValue));
					} else if( paramClass.isAssignableFrom(Double.class) ) {
						newList.add(Double.parseDouble(listValue));
					} else if( paramClass.isAssignableFrom(Byte.class) ) {
						newList.add(Byte.parseByte(listValue));
					} else if( paramClass.isAssignableFrom(String.class) ) {
						newList.add(listValue);
					}
				}

				field.set(target, newList);
			}

		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static Object stringToBasicObject(Class fieldClass, String fieldName, String fieldValue) {
		if( fieldClass.isPrimitive() ) {
			if( fieldClass.isAssignableFrom(int.class) ) {
				return Integer.parseInt(fieldValue);
			} else if( fieldClass.isAssignableFrom(long.class) ) {
				return Long.parseLong(fieldValue);
			} else if( fieldClass.isAssignableFrom(float.class) ) {
				return Float.parseFloat(fieldValue);
			} else if( fieldClass.isAssignableFrom(double.class) ) {
				return Double.parseDouble(fieldValue);
			} else if( fieldClass.isAssignableFrom(byte.class) ) {
				return Byte.parseByte(fieldValue);
			}
		} else if( fieldClass.isAssignableFrom(String.class) ) {
			return fieldValue;
		} else if( fieldClass.isAssignableFrom(Integer.class) ) {
			return Integer.parseInt(fieldValue);
		} else if( fieldClass.isAssignableFrom(Long.class) ) {
			return Long.parseLong(fieldValue);
		} else if( fieldClass.isAssignableFrom(Float.class) ) {
			return Float.parseFloat(fieldValue);
		} else if( fieldClass.isAssignableFrom(Double.class) ) {
			return Double.parseDouble(fieldValue);
		} else if( fieldClass.isAssignableFrom(Byte.class) ) {
			return Byte.parseByte(fieldValue);
		}
		throw new RuntimeException("Field type is not primitive or wrapper primitive or string!");
	}

	public static String basicObjectToString(Object value) {
		Class clazz = value.getClass();

		if( clazz.isEnum() ) {
			Object[] constants = clazz.getEnumConstants();
			for(int i = 0 ; i<constants.length ; i++ ) {
				if( constants[i].equals(value) ) {
					return "" + i;
				}
			}
		}

		return value.toString();
	}

	private String[] getSplited(String string, char delimiter) {
		String[] tmp = string.split("" + delimiter);
		List<String> list = new ArrayList<>();
		for(String val : tmp ) {
			if( !val.isEmpty() ) {
				list.add(val);
			}
		}
		return list.toArray(new String[list.size()]);
	}
}
