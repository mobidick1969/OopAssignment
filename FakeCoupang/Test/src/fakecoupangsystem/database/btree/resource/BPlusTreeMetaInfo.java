package fakecoupangsystem.database.btree.resource;

import fakecoupangsystem.database.resources.FileKeyGeneratorImpl;

import java.io.*;
import java.util.Map;

/**
 * Created by Coupang on 2016. 1. 3..
 * grid file map
 */
public class BPlusTreeMetaInfo extends FileKeyGeneratorImpl {

	public BPlusTreeMetaInfo(File dir, String name) {
		super(dir, name);
	}

	public void setPapaId(long nodeId, long papaId) {
		properties.setProperty("" + nodeId, "" + papaId);
	}

	public Integer getPapaId(long nodeId) {
		String papaIdStr = properties.getProperty("" + nodeId);
		if( papaIdStr != null ) {
			return Integer.parseInt(papaIdStr);
		}
		return null;
	}
}
