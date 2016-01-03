package fakecoupangsystem.database.btree;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Coupang on 2015. 12. 26..
 */
public class Page implements Serializable {
	private File file;
	private long position;
	private Page nextPage = null;

	public Page() { }

	public Page(File file, long position, Page nextPage) {
		this(file, position);
		this.nextPage = nextPage;
	}

	public Page(File file, long position) {
		this.file = file;
		this.position = position;
		nextPage = null;
	}

	public Page getNextPage() {
		return nextPage;
	}

	public void setNextPage(Page nextPage) {
		this.nextPage = nextPage;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public boolean hasNextPage() {
		return nextPage != null;
	}

	@Override
	public String toString() {
		return String.format("File : %s, Position : %d\n", file.getName(), position);
	}
}
