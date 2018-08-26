package org.esfinge.liveprog.database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SqliteConnector {

	private String tableName = "ClassFiles";
	private String databaseName = "classFilesDatabase";
	private String url;
	

	public SqliteConnector() {
		
		url = "jdbc:sqlite:"
				+ Paths.get("sqlite").toAbsolutePath().toString()
				+ "/" + databaseName;
		createTable();
	}


	private void createTable() {
		Connection conn = connect();
		String sqlTable = "CREATE TABLE IF NOT EXISTS " + tableName + 
				" (\n" + "	id integer PRIMARY KEY,\n"
				+ "	name text NOT NULL,\n" + "	commitDate text NOT NULL,\n"   + "	classFile blob\n" + ");";
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.execute(sqlTable);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private Connection connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public void insertFile(File file, String classFileCanonicalName) {
		String sql = "INSERT INTO " + tableName + "(name,commitDate,classFile) VALUES(?,?,?)";
		// Saving date
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, classFileCanonicalName);
			pstmt.setString(2, timeStamp);
			pstmt.setBytes(3, readFile(file));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public File getFile(String classFileCanonicalName) throws NullPointerException{
		String sql = "SELECT id, name, classFile FROM " + tableName 
				+ " WHERE name = '" + classFileCanonicalName + "'" +
				" " + "ORDER BY commitDate DESC";

		
		FileOutputStream fos = null;

		try (Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			if(rs.next()) {
				File file = new File(rs.getString("name"));
				fos = new FileOutputStream(file);
				InputStream input = rs.getBinaryStream("classFile");
				byte[] buffer = new byte[1024];
				while (input.read(buffer) > 0) {
					fos.write(buffer);
				}
				
				// removing this file from database
				String sqlDelete = "DELETE FROM " + tableName + " WHERE id = '" + rs.getString("id") + " '";
				PreparedStatement pstmt = conn.prepareStatement(sqlDelete);
				pstmt.executeUpdate();
				return file;
			} else {
				return null;
			}
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new NullPointerException("There is no class file to rollback");
		

	}

	private byte[] readFile(File file) {
		ByteArrayOutputStream bos = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			bos = new ByteArrayOutputStream();
			for (int len; (len = fis.read(buffer)) != -1;) {
				bos.write(buffer, 0, len);
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return bos != null ? bos.toByteArray() : null;
	}

}
