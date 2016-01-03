package fakecoupangsystem.database.table.row;

import fakecoupangsystem.database.btree.Page;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Coupang on 2016. 1. 2..
 */
public class FileRowWriter implements RowWriter {

	private static final int MAX_PAGE_SIZE = 1024*4; //4kb
	private File lastFile;
	private File dataDir;
	private Translator translator;

	public FileRowWriter(File dataDir, Translator translator) throws IOException {
		this.dataDir = dataDir;
		this.translator = translator;

		//tracking last data file to append some entity
		int maxNum = Integer.MIN_VALUE;
		File maxFile = null;

		File[] datas = dataDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".data");
			}
		});

		if( datas == null || datas.length <= 0 ) {
			lastFile = new File(dataDir, "0.data");
			lastFile.createNewFile();
		} else {
			for(File dataFile : datas ) {
				if( maxFile == null ) {
					maxFile = dataFile;
				} else {
					int dataNum = getDataFileNo(dataFile);
					if( maxNum < dataNum ) {
						maxFile = dataFile;
					}
				}
			}

			lastFile = maxFile;
		}
	}

	private int getDataFileNo(File dataFile) {
		return
			Integer.parseInt(dataFile.getName().replace(".data", "").trim());
	}

	private void makeNewDataFile() throws IOException {
		int dataFileNo = getDataFileNo(lastFile);
		dataFileNo++;
		File neoDataFile = new File(dataDir, dataFileNo + ".data");
		neoDataFile.createNewFile();
		lastFile = neoDataFile;
	}

	private <T> List<Page> appendToFile(List<T> entitys) {
		List<Page> ret = new ArrayList<Page>(entitys.size());
		long len = lastFile.length();
		FileWriter writer = null;

		try {
			writer = new FileWriter(lastFile, true);

			for(int i = 0 ; i<entitys.size() ; ++i ) {
				//make new page
				if( len > MAX_PAGE_SIZE ) {
					writer.close();
					makeNewDataFile();
					List<Page> got = appendToFile(entitys.subList(i, entitys.size()));
					ret.addAll(got);
					break;
				}

				T entity = entitys.get(i);
				String line = translator.translateToString(entity);
				writer.write(line + "\n");

				Page page = new Page();
				page.setFile(lastFile); page.setPosition(len);
				ret.add(page);

				len += line.getBytes().length + 1; //add \n
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if( writer != null ) {
				try { writer.close(); } catch (Exception ignore) { }
			}
		}

		return ret;
	}

	@Override
	public <T> List<Page> write(List<T> entitys) {
		return appendToFile(entitys);
	}

	@Override
	public <T> Page write(T entity) {
		List<T> list = new LinkedList<T>();
		list.add(entity);
		Page page = appendToFile(list).get(0);
		return page;
	}

	@Override
	public void update(Page page, String data, int field, boolean variable) {
		/*
			warning!!
			field is 1 base not 0 base!!!
		 */
		data = data.trim();
		RandomAccessFile f = null;
		int hit = 0;
		boolean lastField = false;
		long posStart, posEnd;

		try {
			f = new RandomAccessFile(page.getFile(), "rw");
			f.seek(page.getPosition());

			while(hit != field - 1 ) {
				byte ch = f.readByte();
				if( ch == Delimiter) {
					f.readByte();
					hit++;
				} else if( ch == '\n' ) {
					throw new IllegalAccessError();
				}
			}

			posStart = f.getFilePointer() - 1;
			posStart = posStart < 0 ? 0 : posStart;


			while(true) {
				byte ch = f.readByte();
				if( ch == Delimiter || ch == '\n') {
					lastField = ch == '\n';
					posEnd = f.getFilePointer();
					break;
				}
			}

			//가변 항
			if( variable ) {
				long len = posEnd - posStart;
				if( len >= data.length() ) {
					//able to rewrite
					f.seek(posStart);
					f.write(data.getBytes());

					while(f.getFilePointer() < posEnd - 1) {
						f.write(' ');
					}

					if( lastField ) {
						f.write('\n');
					}
				} else {
					//have to truncate
					File tmp = new File(page.getFile().getParent(), System.currentTimeMillis() + "_truncate.temp");
					tmp.createNewFile();
					BufferedWriter writer = null;
					try {
						f.seek(posEnd);
						writer = new BufferedWriter(new FileWriter(tmp));
						writer.write(data + (lastField ? "\n" : Delimiter));
						while(true) {
							String line = f.readLine();
							if( line == null ) break;
							writer.write(line + "\n");
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if( writer != null ) {
							try { writer.close(); } catch (IOException e) { }
						}
					}

					f.seek(posStart);
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(tmp));
						while(true) {
							String line = reader.readLine();
							if( line == null ) break;
							f.writeBytes(line + "\n");
						}

						f.setLength(f.getFilePointer());
						tmp.delete();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if( reader != null ) {
							try { reader.close(); } catch (IOException e) { }
						}
					}

				}
			} else {
				f.seek(posStart);
				f.writeBytes(data);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(IllegalAccessError e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if( f != null ) {
				try { f.close(); } catch(Exception ignore) { }
			}
		}
	}
}
