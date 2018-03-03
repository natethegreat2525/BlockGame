package drawtest;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.glfw.GLFW;

import terraingenerators.SimplexLandBuilder;
import world.ChunkBuilderThread;
import world.Player;
import world.Raycast;
import world.World;
import chunks.ChunkViewport;

import com.nshirley.engine3d.N3D;
import com.nshirley.engine3d.entities.Camera3d;
import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.entities.shapes.Shape;
import com.nshirley.engine3d.graphics.Texture;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;
import com.nshirley.engine3d.window.Input;
import com.nshirley.engine3d.window.Mouse;
import com.nshirley.engine3d.window.Window;

import drawentity.ChunkEntity;

public class Automata {

	public static int WIDTH = 1024, HEIGHT = 768;

	public static void main(String[] args) {
		Window win = new Window(WIDTH, HEIGHT, "Player Test");
		win.setCursorMode(GLFW.GLFW_CURSOR_DISABLED);

		N3D.init();
		ChunkEntity.loadShader();

		Texture tx = new Texture("res/blocks_a.png");

		Camera3d c = new Camera3d((float) Math.toRadians(100), WIDTH, HEIGHT,
				.1f, 1000);
		
		World world = new World(new SimplexLandBuilder());
		
		ChunkViewport cv = new ChunkViewport(new Vector3i(), new Vector3i(10, 6, 10), world, tx);
		
		Entity box = new Entity(Shape.cube(), tx);
		
		ChunkBuilderThread builder = new ChunkBuilderThread(cv);
		Thread builderThread = new Thread(builder);
		builderThread.start();
		
		Vector3f camPos = new Vector3f();

		Player player = new Player(box, new Vector3f(), new Vector3f(.5f, 1.5f, .5f));
		 
		long time = System.currentTimeMillis();
		int count = 0;
		long deltaTime = System.currentTimeMillis();
		double delta = 1;
		
		Plants_a p = new Plants_a();
		
		while (!win.shouldClose()) {
			long newDelta = System.currentTimeMillis();
			delta = (newDelta - deltaTime) / (1000 / 60.0);
			deltaTime = newDelta;
			delta = Math.min(delta, 4);
			
			count++;
			if (count == 100) {
				System.out.println(p.plants.size());
				count = 0;
				System.out.println(100.0 / ((System.currentTimeMillis() - time) / 1000.0));
				time = System.currentTimeMillis();
			}
			
			if (cv.getNumToTriangulate() < 15 || world.hasUpdates()) {
				//triggers another thread to build more
				builder.loadMore();
			}
			for (int i = 0; i < 15; i++) {
				cv.triangulateNextChunk();
			}
			if (cv.getNumToTriangulate() < 15) {
				//triggers another thread to build more
				builder.loadMore();
			}
			
			win.clear();
			win.pollEvents();
			
			float rotH = (float) Mouse.X * .3f;
			float rotV = (float) Mouse.Y * .3f;
			
			c.setRotation(new Vector3f((float) rotV, (float) rotH, 0));
			c.setPosition(camPos);
			player.setAngle(rotH);
			
			Vector3f lookNorm = c.getLookDir().normalize();
			Vector3f lookSpd = lookNorm.mult(0.3f);

			N3D.pushMatrix();
			N3D.multMatrix(c.getTotalMatrix());

			cv.render(c.getPosition(), lookNorm);			

			Vector3f startRay = camPos.clone();
			Vector3f rayDir = lookSpd;

			Raycast rc = world.raycast(startRay, rayDir, 40);
			if (rc != null) {
				box.setModelMatrix(Matrix4f.translate(rc.position).multiply(Matrix4f.scale(new Vector3f(.1f, .1f, .1f))));
				box.render();
				//System.out.println(cv.getLightValue(Vector3i.add(rc.blockPosition, new Vector3i(0, 1, 0))));
				if (Input.isKeyDown(GLFW.GLFW_KEY_P)) {
					p.plants.add(new Plant_a(rc.position));
					//world.setBlockValue(rc.blockPosition.x, rc.blockPosition.y, rc.blockPosition.z, (short) 0);
				}
			}
			if (Input.isKeyHit(GLFW.GLFW_KEY_L)) {
				int lsize = 10;
				for (int i = -lsize; i < lsize; i++) {
					for (int j = -lsize; j < lsize; j++) {
						Raycast r = world.raycast(new Vector3f(i * 5.1f + camPos.x, 10.1f, j * 5.1f + camPos.z), new Vector3f(0.0001f, -1, 0.0002f), 100);
						if (r != null) {
							p.plants.add(new Plant_a(r.position));
						}
					}
				}
			}
			p.tick(world, cv);
			
			float plSpeed = Input.isKeyDown(GLFW.GLFW_KEY_N) ? .2f : .05f;
			float jump = .2f;
			float xspd = 0, zspd = 0;
			if (Input.isKeyDown(GLFW.GLFW_KEY_W)) {
				zspd -= 1;
			}
			if (Input.isKeyDown(GLFW.GLFW_KEY_S)) {
				zspd += 1;
			}
			if (Input.isKeyDown(GLFW.GLFW_KEY_A)) {
				xspd -= 1;
			}
			if (Input.isKeyDown(GLFW.GLFW_KEY_D)) {
				xspd += 1;
			}
			xspd *= plSpeed;
			zspd *= plSpeed;
			
			if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
				if (player.isGrounded() || Input.isKeyDown(GLFW.GLFW_KEY_N))
					player.setVelocityY(jump);
			}
			
			player.render();

			player.setVelocityXZRel(xspd, zspd);
			
			player.update(world, (float) delta);
			camPos = player.getPosition().add(new Vector3f(0, .7f, 0));
			int i = glGetError();
			if (i != GL_NO_ERROR) {
				System.out.println(i);
			}

			N3D.popMatrix();

			win.flip();
			if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
				break;
		}
		builder.finish();
	}
}

class Plants_a {
	public ArrayList<Plant_a> plants;
	
	public Plants_a() {
		plants = new ArrayList<Plant_a>();
	}
	
	public void tick(World w, ChunkViewport cv) {
		ArrayList<PlantState_a> seeds = new ArrayList<PlantState_a>();
		Iterator<Plant_a> itr = plants.iterator();
		System.out.println(plants.size());
		while (itr.hasNext()) {
			Plant_a p = itr.next();
			PlantState_a ps = p.tick(w, cv);
			if (!ps.alive) {
				itr.remove();
			}
			if (ps.seeded) {
				seeds.add(ps);
			}
		}
		for (PlantState_a ps : seeds) {
			if (Float.isFinite(ps.seed.x) && Float.isFinite(ps.seed.y) && Float.isFinite(ps.seed.z)) {
				Plant_a p = new Plant_a(ps.seed, ps.genome.clone().mutate());
				System.out.println("split " + ps.seed);
				this.plants.add(p);
			}
		}
	}
}

class PlantState_a {
	public boolean alive;
	public boolean seeded;
	public Vector3f seed;
	public Genome_a genome;
	
	public PlantState_a(boolean alive, boolean seeded, Vector3f seed, Genome_a g) {
		this.alive = alive;
		this.seeded = seeded;
		this.seed = seed;
		this.genome = g;
	}
}

class Plant_a {
	public static final int MAX_ENERGY = 1200;
	public static final int START_ENERGY = 800;
	public static final int ENERGY_LOSS_PER_BLOCK = 10;
	public static final int ENERGY_COST_BLOCK = 100;
	public static final int STORAGE_PER_WOOD = 100;
	
	private int energy;
	private Genome_a genes;
	private ArrayList<Cell_a> cells;
	private ArrayList<Vector3i> photoCells;
	private ArrayList<Vector3i> blockList;	
	
	public Plant_a(Vector3f pos) {
		this(pos, new Genome_a());
	}
	
	public Plant_a(Vector3f pos, Genome_a g) {
		cells = new ArrayList<Cell_a>();
		photoCells = new ArrayList<Vector3i>();
		blockList = new ArrayList<Vector3i>();
		Cell_a seed = new Cell_a(pos, new Vector3f(0, 1, 0), 0);
		cells.add(seed);
		energy = START_ENERGY;
		genes = g;
	}
	
	public PlantState_a tick(World w, ChunkViewport cv) {
		energy -= ENERGY_LOSS_PER_BLOCK * blockList.size();
		if (energy <= 0) {
			return new PlantState_a(false, false, null, null);
		}
		int lightVal = 0;
		//calculate energy gain
		for (Vector3i pos : photoCells) {
			Vector3i top = pos.clone();
			Vector3i bot = pos.clone();
			Vector3i left = pos.clone();
			Vector3i right = pos.clone();
			Vector3i fwd = pos.clone();
			Vector3i back = pos.clone();
			top.y++;
			bot.y--;
			left.x--;
			right.x++;
			fwd.z++;
			back.z--;			
			int topv = cv.getLightValue(top);
			int botv = cv.getLightValue(bot);
			int leftv = cv.getLightValue(left);
			int rightv = cv.getLightValue(right);
			int fwdv = cv.getLightValue(fwd);
			int backv = cv.getLightValue(back);
			lightVal += Math.max(Math.max(topv, botv), Math.max(Math.max(leftv, rightv), Math.max(fwdv, backv)));
		}
		energy += lightVal;
		energy = Math.min(energy, MAX_ENERGY + (blockList.size() - photoCells.size()) * STORAGE_PER_WOOD);
		//only do anything if enough energy is left
		if (energy > ENERGY_COST_BLOCK && cells.size() > 0) {
			int idx = (int) (Math.random() * cells.size());
			Cell_a c = cells.get(idx);
			Gene_a g = genes.getGene(c.type);
			
			if (g.getCellType() == 2) {
				//seed
				if (energy > START_ENERGY) {
					Raycast rc = w.raycast(c.pos.add(c.dir.normalize().mult(2)), c.dir, 10);
					if (rc != null) {
						short s = w.getBlockValue(rc.blockPosition.x, rc.blockPosition.y, rc.blockPosition.z);
						if (s == 1) {
							w.setBlockValue(rc.blockPosition, (short) 8);
							energy -= START_ENERGY;
							return new PlantState_a(energy > 0, true, rc.position, genes);
						}
					}
				}
			} else {
				Vector3f originPos = c.pos.clone();
				Vector3i startPos = c.pos.floor();
				Vector3i endPos;
				Vector3f dirN = c.dir.normalize();
				do {
					c.pos = c.pos.add(dirN.mult(.1f));
					endPos = c.pos.floor();
				} while (startPos.equals(endPos));
				//check if is in air
				if (w.getBlockValue(endPos.x, endPos.y, endPos.z) != 0) {
					c.pos = originPos;
				} else {
					energy -= ENERGY_COST_BLOCK;
					w.setBlockValue(endPos, (short) (g.getCellType() + 9));
					blockList.add(endPos);
					if (g.getCellType() == 1) {
						photoCells.add(endPos);
						cells.remove(idx); // remove plant cells after they make a block
					}
				}
				//only split non plant cells
				if (g.getCellType() != 1 && Math.random() < g.splitProb()) {
					//split
					cells.remove(idx);
					Vector3f newDir = calcNewDir(c.dir, g);
					cells.add(new Cell_a(c.pos, c.dir, g.getSplitOutSelf()));
					cells.add(new Cell_a(c.pos, newDir, g.getSplitOutOther()));
				}
			}
		}
		return new PlantState_a(energy > 0, false, null, null);
	}
	
	private Vector3f calcNewDir(Vector3f in, Gene_a g) {
		Vector3f out = new Vector3f(
				in.x * g.getDirMtx(0) + in.y * g.getDirMtx(1) + in.z * g.getDirMtx(2) + g.getDirMtx(3),
				in.x * g.getDirMtx(4) + in.y * g.getDirMtx(5) + in.z * g.getDirMtx(6) + g.getDirMtx(7),
				in.x * g.getDirMtx(8) + in.y * g.getDirMtx(9) + in.z * g.getDirMtx(10) + g.getDirMtx(11)
			);
		if (out.x == 0) {
			out.x = .001f;
		}
		if (out.y == 0) {
			out.y = .001f;
		}
		if (out.z == 0) {
			out.z = .001f;
		}
		return out;
	}
}

class Cell_a {
	public Vector3f pos;
	public Vector3f dir;
	public int type; //cell gene index
	
	public Cell_a(Vector3f pos, Vector3f dir, int type) {
		this.pos = pos;
		this.dir = dir;
		this.type = type;
	}
}

class Genome_a {
	private static final int genes_per_genome = 16;
	private Gene_a[] genes;
	
	public Genome_a() {
		//init random genome
		genes = new Gene_a[genes_per_genome];
		for (int i = 0; i < genes.length; i++) {
			genes[i] = new Gene_a();
		}
	}
	
	public Gene_a getGene(int idx) {
		return genes[idx];
	}
	
	public Genome_a clone() {
		Genome_a c = new Genome_a();
		for (int i = 0; i < genes.length; i++) {
			c.genes[i] = genes[i].clone();
		}
		return c;
	}
	
	public Genome_a mutate() {
		for (Gene_a g : genes) {
			g.mutate(.01);
		}
		return this;
	}
}

class Gene_a {
	
	public static final int DIR_MTX_START = 4;
	/*
	splitOutSelf; //self change gene id %16
	splitOutOther; //other gene id %16
	cellType; //type of cell %3 (photo, boundary, reproduce)
	splitProb; //probability a cell splits each iteration %100
	dir_matrix; //12 values %100 (x/50.0 - 1)  concat 1 onto vec before multiplying
	*/
	
	public int getSplitOutSelf() {
		return vals[0];
	}
	
	public int getSplitOutOther() {
		return vals[1];
	}
	
	public int getCellType() {
		return vals[2];
	}
	
	public double splitProb() {
		return vals[3] / 100.0;
	}
	
	public float getDirMtx(int idx) {
		return (vals[DIR_MTX_START + idx] / 50.0f) - 1;
	}
	
	private static int[] mods = {16, 16, 3, 100,
		100, 100, 100, 100,
		100, 100, 100, 100,
		100, 100, 100, 100,
		};
	
	public int[] vals;
	
	public Gene_a clone() {
		Gene_a g = new Gene_a();
		g.vals = vals.clone();
		return g;
	}
	
	public Gene_a() {
		vals = new int[mods.length];
		for (int i = 0; i < vals.length; i++) {
			vals[i] = (int) (Math.random() * 10000);
		}
		this.fixMods();
	}
	
	public void fixMods() {
		for (int i = 0; i < vals.length; i++) {
			vals[i] = vals[i] % mods[i];
		}
	}
	
	public void mutate(double prob) {
		for (int i = 0; i < vals.length; i++) {
			if (Math.random() < prob) {
				double mt = Math.random();
				if (mt < .333) {
					vals[i] = (vals[i] + 1) % mods[i];
				} else if (mt < .666) {
					vals[i] = ((vals[i] - 1) + mods[i]) % mods[i];
				} else {
					vals[i] = ((int) (Math.random() * 10000)) % mods[i];
				}
			}
		}
	}
}
