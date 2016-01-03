package fakecoupangsystem.database.table.row;

import fakecoupangsystem.database.btree.Page;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coupang on 2016. 1. 2..
 */
public class FileRowReader implements RowReader {

	private Translator translator;

	public FileRowReader(Translator translator) {
		this.translator = translator;
	}

	@Override
	public <T> T readRow(Page page, Class<T> clazz) {
		T entity = null;
		List<T> list = readRows(page, 1, clazz);
		if( list != null ) {
			entity = list.get(0);
		}

		return entity;
	}

	@Override
	public <T> List<T> readRows(Page page, int howmuch, Class<T> clazz) {
		howmuch = ( howmuch <= 0 ? 1 : howmuch );
		List<T> ret = new ArrayList<T>(howmuch);
		RandomAccessFile f = null;

		try {
			f = new RandomAccessFile(page.getFile(), "r");
			f.seek(page.getPosition());

			for(int i = 0 ; i<howmuch ; i++ ) {
				String line = f.readLine();
				if( line == null ) {
					break;
				}
				T entity = (T)translator.translateToObject(line, clazz);
				ret.add(entity);
			}

			return ret;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if( f != null ) {
				try { f.close(); } catch(Exception ignore) { }
			}
		}

		return null;
	}

	@Override
	public <T> List<T> readRows(Page page, Class<T> clazz) {
		List<T> list = new ArrayList<>();

		Page it = page;
		while(it != null ) {
			list.add(readRow(page, clazz) );
			it = it.getNextPage();
		}

		return list;
	}
}
