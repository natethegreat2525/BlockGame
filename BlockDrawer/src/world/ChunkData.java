package world;

import java.util.ArrayList;

import com.nshirley.engine3d.math.Vector3i;

import chunks.Chunk;
import engine.SimEntity;

public class ChunkData {
	public static final int SIZE = Chunk.SIZE;
	
	public static final int SIZE2 = SIZE * SIZE;
	public static final int DATA_LENGTH = SIZE * SIZE * SIZE;
	
	/**
	 * State of chunk
	 */
	private boolean empty;
	private short[] data;
	
	private ArrayList<SimEntity> entities;
	
	private Vector3i position;
	
	public ChunkData(Vector3i pos, short[] data, ArrayList<SimEntity> ents) {
		if (data.length != DATA_LENGTH) {
			throw new IllegalArgumentException("Invalid chunk data length");
		}
		this.position = pos;
		this.data = data;
		this.empty = false;
		this.entities = ents;
	}
	
	public ChunkData(Vector3i pos) {
		this.empty = true;
		this.position = pos;
		entities = new ArrayList<SimEntity>();
	}
	
	public ArrayList<SimEntity> getEntities() {
		return entities;
	}

	public short[] getData() {
		return data;
	}
	
	public Vector3i getPosition() {
		return position;
	}
	
	public void setValueSafe(int x, int y, int z, short value) {
		if (x >= 0 && y >= 0 && z >= 0 && x < SIZE && y < SIZE && z < SIZE) {
			this.setValue(x, y, z, value);
		}
	}
	
	public void setValue(int x, int y, int z, short value) {
		if (empty) {
			if (value == 0) {
				return;
			}
			this.data = new short[DATA_LENGTH];
			empty = false;
		}
		
		data[x + y * SIZE + z * SIZE2] = value;
	}
	
	public short getValue(int x, int y, int z) {
		if (empty) {
			return 0;
		}
		
		return data[x + y * SIZE + z * SIZE2];
	}
}
