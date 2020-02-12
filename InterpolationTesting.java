package ebai.javamusic.wavey;

public class InterpolationTesting {
	// debugging interpolation methodss
	
	public static void main(String args[])
	{
		DataList<ChordNode> dl = new DataList<ChordNode>();
		double list[] = {1,3,4,4,4,4,4,7,9,3,7,3,4};
		
		for (double d : list)
			dl.add(new ChordNode(d, 0, 0, 0));
		
		System.out.println("pre interpolation: "+dl);
		
		dl.interpolate(
			(i) -> dl.get(i).getAmplitude(),
			(j, k) -> dl.get(j).setAmplitude(k));
		
		System.out.println("pst interpolation: "+dl);
	}
}
