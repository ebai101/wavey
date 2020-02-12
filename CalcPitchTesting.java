package ebai.javamusic.wavey;

public class CalcPitchTesting {
	// debugging WaveDataParser.calcPitchQuality()

	static String[] strangs = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S"};
	static int[] thangs = {0, 22, 45, 67, 90, 112, 135, 157, 180};
	
	public static void main(String args[]) {
		for (int i = 0; i < 8; i++)
			print(WaveDataParser.calcPitchQuality(strangs[i], thangs[i]));
	}
	
	static void print (int[] input)
	{
		System.out.printf("%d, %d\n", input[0], input[1]);
	}
}
