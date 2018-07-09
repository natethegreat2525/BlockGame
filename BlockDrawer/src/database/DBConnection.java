package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import com.nshirley.engine3d.math.Vector3i;

import world.ChunkData;

public class DBConnection {
	
	public String url;
	public Connection conn;
	public DBConnection(String saveFile) {
		url = "jdbc:sqlite:" + saveFile;
	}
	
	public boolean connect() {
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.err.println(e);
			return false;
		}
		return true;
	}
	
	public boolean initializeTable() {
		Statement stmt;
		try {
			stmt = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS chunks ( x integer, y integer, z integer, realm integer, data BLOB, PRIMARY KEY (x, y, z, realm) )";
			stmt.execute(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean saveChunk(ChunkData cd, int x, int y, int z, int realm) {
		try {
			short[] chunkArray = cd.getData();
			
			String sql = "INSERT OR REPLACE INTO chunks(x, y, z, realm, data) VALUES(?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, x);
			pstmt.setInt(2, y);
			pstmt.setInt(3, z);
			pstmt.setInt(4, realm);
	
			ByteBuffer byteBuf = ByteBuffer.allocate(2*chunkArray.length);
			for (int i = 0; i < chunkArray.length; i++) {
				byteBuf.put((byte) (chunkArray[i] & 0x000000ff));
				byteBuf.put((byte) ((chunkArray[i] & 0x0000ff00) >>> 8));
			}
	
			pstmt.setBytes(5, byteBuf.array());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public ChunkData getChunk(int x, int y, int z, int realm) {
		try {
			String sql = "SELECT data FROM chunks WHERE x=? and y=? and z=? and realm=?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, x);
			pstmt.setInt(2, y);
			pstmt.setInt(3, z);
			pstmt.setInt(4, realm);
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				InputStream is = rs.getBinaryStream("data");
				byte[] barr = new byte[ChunkData.DATA_LENGTH * 2];
				is.read(barr);
				is.close();
				short[] retData = new short[ChunkData.DATA_LENGTH];
				for (int i = 0; i < barr.length; i+= 2) {
					retData[i >> 1] = (short) (barr[i] & 0x000000ff | ((barr[i + 1] << 24) >> 16));
				}
				rs.close();
				pstmt.close();
				return new ChunkData(new Vector3i(x, y, z), retData);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean disconnect() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		String url = "jdbc:sqlite:database.db";
		try (Connection conn = DriverManager.getConnection(url)) {
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				System.out.println("Driver Name: " + meta.getDriverName());
				System.out.println("New Db created");
				
				Statement stmt = conn.createStatement();
				String sql = "CREATE TABLE IF NOT EXISTS chunks ( x integer, y integer, z integer, realm integer, data BLOB, PRIMARY KEY (x, y, z, realm) )";
				stmt.execute(sql);
				
				System.out.println("Table created");
				
				String insSql = "INSERT OR REPLACE INTO chunks(x, y, z, realm, data) VALUES(?,?,?,?,?)";
				PreparedStatement pstmt = conn.prepareStatement(insSql);
				pstmt.setInt(1, 23);
				pstmt.setInt(2, -399);
				pstmt.setInt(3, 98);
				pstmt.setInt(4, 2);
				
				System.out.println("Creating blob");

				System.out.println("Blob created");
				int len = 16*16*16;
				short[] data = new short[len];
				for (int i = 0; i < len; i++) {
					data[i] = (short) ((Math.random() * 2 - 1) * Short.MAX_VALUE);
				}
				System.out.println("Byte buffer");
				System.out.println(Arrays.toString(data));

				ByteBuffer byteBuf = ByteBuffer.allocate(2*len);
				for (int i = 0; i < len; i++) {
					byteBuf.put((byte) (data[i] & 0x000000ff));
					byteBuf.put((byte) ((data[i] & 0x0000ff00) >>> 8));
				}
				System.out.println("Setting blob");

				pstmt.setBytes(5, byteBuf.array());
				System.out.println("About to insert");
				System.out.println(pstmt.executeUpdate());
				
				System.out.println("Data inserted");
				
				
				String selectSql = "SELECT data FROM chunks WHERE x=? and y=? and z=? and realm=?";
				
				pstmt = conn.prepareStatement(selectSql);
				pstmt.setInt(1, 23);
				pstmt.setInt(2, -399);
				pstmt.setInt(3, 98);
				pstmt.setInt(4, 2);
				
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					System.out.println("Getting blob");
					InputStream is = rs.getBinaryStream("data");
					byte[] barr = new byte[len * 2];
					is.read(barr);
					is.close();
					short[] retData = new short[len];
					for (int i = 0; i < barr.length; i+= 2) {
						retData[i >> 1] = (short) (barr[i] & 0x000000ff | ((barr[i + 1] << 24) >> 16));
					}
					System.out.println(Arrays.toString(retData));
				}
				
				

			}
		} catch (SQLException | IOException e) {
			System.err.println(e.getMessage());
			System.err.println(e);
		}
	}

}
