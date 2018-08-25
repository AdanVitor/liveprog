package org.esfinge.liveprog.database;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ClassFilesSaverDao {

	private Path binDirectory;

	public  ClassFilesSaverDao(String binDirectory) {
		this.binDirectory = Paths.get(binDirectory);
	}

	public abstract File restoreClassFile(Class<?> liveObjectClass);

	public abstract void saveClassFile(Class<?> liveObjectClass);


	public Path getBinDirectory() {
		return binDirectory;
	}

	public void setBinDirectory(Path binDirectory) {
		this.binDirectory = binDirectory;
	}

}
