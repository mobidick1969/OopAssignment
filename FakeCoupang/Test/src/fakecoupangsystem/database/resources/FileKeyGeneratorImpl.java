package fakecoupangsystem.database.resources;

import java.io.*;
import java.util.Properties;

/**
 * Created by Coupang on 2016. 1. 3..
 */
public class FileKeyGeneratorImpl implements KeyGenerator {

	private static final String KEY = "KEY";

	protected File dir;
	protected File keyFile;
	protected Properties properties;
	protected String name;

	public FileKeyGeneratorImpl(File dir, String name) {
		this.dir = dir;
		this.name = name;
	}

	@Override
	public void load() {
		keyFile = new File(dir, name + ".key");
		InputStream in = null;
		try {
			if( !keyFile.exists() ) {
				properties = new Properties();
				properties.setProperty(KEY, "" + 0);
				keyFile.createNewFile();
			} else {
				System.out.println("Have key File!!");
				in = new FileInputStream(keyFile);
				properties = new Properties();
				properties.load(in);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public long nextLongId() {
		String keyStr = properties.getProperty(KEY);
		long ret = Long.parseLong(keyStr);
		properties.setProperty(KEY, "" + (ret + 1));
		return ret;
	}

	@Override
	public int nextIntId() {
		String keyStr = properties.getProperty(KEY);
		int ret = Integer.parseInt(keyStr);
		properties.setProperty(KEY, "" + (ret + 1));
		return ret;
	}

	@Override
	public String nextStringId() {
		String keyStr = properties.getProperty(KEY);
		long ret = Long.parseLong(keyStr);
		properties.setProperty(KEY, "" + (ret + 1));
		return keyStr;
	}

	@Override
	public void flush() {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(keyFile);
			properties.store(out, "");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if( out != null ) {
				try { out.close(); } catch (IOException e) { e.printStackTrace(); }
			}
		}
	}
}
