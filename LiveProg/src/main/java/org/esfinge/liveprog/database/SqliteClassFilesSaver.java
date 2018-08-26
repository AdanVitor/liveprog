package org.esfinge.liveprog.database;

import java.io.File;

public class SqliteClassFilesSaver extends ClassFilesSaverDao{


	private SqliteConnector sqliteConnector = new SqliteConnector();
	
	public SqliteClassFilesSaver(String binDirectory) {
		super(binDirectory);
	}

	public File restoreClassFile(Class<?> liveObjectClass) {
		return sqliteConnector.getFile(liveObjectClass.getCanonicalName());
	}
	
	public void saveClassFile(Class<?> liveObjectClass) {
		File classFile = new File(getBinDirectory().toAbsolutePath().
				toString() + "/" + liveObjectClass.getCanonicalName().replace(".", "/") + ".class");
		sqliteConnector.insertFile(classFile, liveObjectClass.getCanonicalName());
	}

}
