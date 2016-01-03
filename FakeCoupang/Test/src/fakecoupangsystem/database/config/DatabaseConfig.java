package fakecoupangsystem.database.config;

import fakespring.annotation.Meta;

import java.io.File;

/**
 * Created by Coupang on 2015. 12. 30..
 */
@Meta
public class DatabaseConfig {
	private String basePackage;
	private File rootDir;

	public DatabaseConfig() {
		basePackage = "fakecoupangsystem.domain";
		rootDir = new File("/tmp/fakecoupang");
		if ( !rootDir.exists() ) {
			rootDir.mkdir();
		}
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public File getRootDir() {
		return rootDir;
	}

	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
	}
}
