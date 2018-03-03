package treegen;

import java.util.ArrayList;

import world.Raycast;
import world.World;
import chunks.ChunkViewport;

import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;

public class Plant {
	
	private Genome genome;
	private ArrayList<PlantCell> cells;
	private ArrayList<Vector3i> photoCells;
	private ArrayList<Vector3i> blockList;	
	
	public Plant(Vector3f pos, double size) {
		this(pos, new Genome(1000), size);
	}
	
	public Plant(Vector3f pos, Genome g, double size) {
		cells = new ArrayList<PlantCell>();
		photoCells = new ArrayList<Vector3i>();
		blockList = new ArrayList<Vector3i>();
		PlantCell seed = new PlantCell(pos, new Vector3f(0, 1, 0), 0, size);
		cells.add(seed);
		genome = g;
	}
	
	public boolean tick(World w, ChunkViewport cv) {
		if (cells.size() == 0) {
			return false;
		}
		int idx = (int) (Math.random() * cells.size());
		PlantCell c = cells.get(idx);
		Vector3f dirN = c.dir.normalize();
		c.pos = c.pos.add(dirN);
		c.len++;
		c.size *= .99;
		int lsize = (int) Math.ceil(c.size);
		System.out.println(lsize);

		int cellType = genome.getValRel(c.type, Genome.TYPE) % 2;
		if (c.size < .5) {
			cellType = 1;
		}
		if (cellType == 1) {
			lsize = genome.getValRel(c.type, Genome.SPLIT_LOW) % 4;
		}
		
		for (int i = -lsize + 1; i < lsize; i++) {
			for (int j = -lsize + 1; j < lsize; j++) {
				for (int k = -lsize + 1; k < lsize; k++) {
					Vector3f p = c.pos.clone();
					p.x += i;
					p.y += j;
					p.z += k;
					Vector3i pi = p.floor();
					float dist = c.pos.add(new Vector3f(-pi.x, -pi.y, -pi.z)).mag();
					if ((cellType == 1 && dist < lsize) || (cellType != 1 && dist <= c.size) || lsize <= 1) {
						if (w.getBlockValue(pi.x, pi.y, pi.z) == 0) {
							w.setBlockValue(pi, (short) (cellType + 9));
						}
					}
				}
			}
		}
		if (cellType == 1) {
			cells.remove(idx);
			return cells.size() > 0;
		}
		
		int splitLow = genome.getValRel(c.type, Genome.SPLIT_LOW) % 10;
		int splitHigh = genome.getValRel(c.type, Genome.SPLIT_HIGH) % 5 + splitLow;
		if (c.len >= splitLow && Math.random() < 1.0 / (splitHigh - c.len)) {
			cells.remove(idx);
			for (int i = 0; i < 4; i++) {
				int splitID = genome.getValRel(c.type, Genome.SPLIT_ID_0 + i);
				double splitProb = (genome.getValRel(c.type, Genome.SPLIT_ID_0 + i) % 100) / 100.0;
				if (Math.random() < splitProb) {
					double split = (genome.getValRel(c.type, Genome.SPLIT_ANG_0 + i) % 100) / 100.0;
					Vector3f newDir = c.dir.add(Vector3f.random(-split, split)).normalize();
					cells.add(new PlantCell(c.pos, newDir, splitID, c.size * (Math.random() + Math.random()) / 2.0));
				}
			}
		}
		
		return cells.size() > 0;
	}
}