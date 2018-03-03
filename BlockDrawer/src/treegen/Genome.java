package treegen;

public class Genome {
	public static final int LEN = 15;

	public static final int TYPE = 0;
	public static final int SPLIT_LOW = 1;
	public static final int SPLIT_HIGH = 2;
	public static final int SPLIT_ID_0 = 3;
	public static final int SPLIT_PROB_0 = 7;
	public static final int SPLIT_ANG_0 = 11;
	
	//type (wood/leaf)
	//dist splitlow range
	//dist splithigh range
	//split1
	//split2
	//split3
	//split4
	//splitprobeach1
	//2
	//3
	//4
	//max split angle each (from normalized dir vec)1
	//2
	//3
	//4
	//
	private int[] dna;
	
	public Genome(int[] dna) {
		this.dna = dna;
	}
	
	public Genome(int len) {
		dna = new int[len];
		for (int i = 0; i < len; i++)
			dna[i] = (int) (Math.random() * 1000);
	}
	
	public int getValRel(int gene, int pos) {
		return dna[(gene * LEN + pos) % dna.length];
	}
}
