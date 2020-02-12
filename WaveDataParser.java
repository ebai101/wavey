package ebai.javamusic.wavey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WaveDataParser {
	
	 /*
	 * chord A amplitude: swell height
	 * chord A arp period: swell period
	 * chord A pitch/quality: swell direction + main wind direction
	 * 
	 * chord B amplitude: wind wave height
	 * chord B arp period: wind wave period
	 * chord B pitch/quality: wind wave direction + main wind direction
	 * 
	 * bass pitch: lower of chord A/B
	 * bass filter: wave steepness
	 * 
	 * random seed: main wind direction
	 * delay amount: average wave height
	 * mod period: average wave period
	 */
	
	private DataList<ChordNode> chordA;
	private DataList<ChordNode> chordB;
	private DataList<BassNode> bass;
	private DataList<ModNode> modulation;
	
	@SuppressWarnings("serial")
	private static final Map<String, Integer> directionMap = new HashMap<String, Integer>() {{
		put("N", 0);
		put("NNE", 1);
		put("NE", 2);
		put("ENE", 3);
		put("E", 4);
		put("ESE", 5);
		put("SE", 6);
		put("SSE", 7);
		put("S", 8);
		put("SSW", 9);
		put("SW", 10);
		put("WSW", 11);
		put("W", 12);
		put("WNW", 13);
		put("NW", 14);
		put("NNW", 15);
	}};
	
	@SuppressWarnings("serial")
	private static final Map<String, Integer> steepnessMap = new HashMap<String, Integer>() {{
		put("N/A", 0);
		put("SWELL", 1);
		put("AVERAGE", 2);
		put("STEEP", 3);
		put("VERY_STEEP", 4);
	}};
	
	public WaveDataParser(ArrayList<WaveDataPoint> waveData, boolean proc)
	{
		// applies all processing to waveData and assembles the DataLists
		// pass false to proc to skip processing
		
		System.out.print("Parsing wave data");
		
		chordA = new DataList<ChordNode>();
		chordB = new DataList<ChordNode>();
		bass = new DataList<BassNode>();
		modulation = new DataList<ModNode>();
		
		// populate arrays
		populateArrays(waveData);
		
		if (!proc)
		{
			System.out.print(", skipping processing...");
		}
		else
		{
			System.out.print("...");
			interpolateAll();
			normalizeAll();
		}

		// done parsing
		System.out.println("success.");
	}

	private void populateArrays(ArrayList<WaveDataPoint> waveData)
	{
		// procedurally populates each DataList
		
		double chordAAmplitude;
		double chordAArpPeriod;
		int chordAPitch;
		int chordAQuality;
		
		double chordBAmplitude;
		double chordBArpPeriod;
		int chordBPitch;
		int chordBQuality;
		
		int bassPitch;
		double bassFilter;
		
		double reverb;
		double modPeriod;
		int randomSeed;
		
		for (int i = 0; i < waveData.size(); i++)
		{
			// chord A
			chordAAmplitude = waveData.get(i).swellHeight;
			chordAArpPeriod = waveData.get(i).swellPeriod;
			int[] pqA = calcPitchQuality(waveData.get(i).swellDirection, waveData.get(i).mainWindDirection);
			chordAPitch = pqA[0];
			chordAQuality = pqA[1];
			
			// chord B
			chordBAmplitude = waveData.get(i).windWaveHeight;
			chordBArpPeriod = waveData.get(i).windWavePeriod;
			int[] pqB = calcPitchQuality(waveData.get(i).windWaveDirection, waveData.get(i).mainWindDirection);
			chordBPitch = pqB[0];
			chordBQuality = pqB[1];
			
			// bass voice
			bassPitch = Math.min(chordAPitch, chordBPitch) + 24;
			bassFilter = steepnessMap.get(waveData.get(i).steepness);
			
			// mod data
			reverb = waveData.get(i).waveHeight;
			modPeriod = waveData.get(i).averageWavePeriod;
			randomSeed = waveData.get(i).mainWindDirection;
			
			// insert into arrays
			chordA.add(new ChordNode (chordAAmplitude, chordAArpPeriod, chordAPitch, chordAQuality));
			chordB.add(new ChordNode (chordBAmplitude, chordBArpPeriod, chordBPitch, chordBQuality));
			bass.add(new BassNode (bassPitch, bassFilter));
			modulation.add(new ModNode(reverb, modPeriod, randomSeed));
		}
	}
	
	private void normalizeAll()
	{
		// runs multiple normalization processes on various parameters
		
		chordA.normalize(
				(i) -> chordA.get(i).getAmplitude(),
				(j, k) -> chordA.get(j).setAmplitude(k),
				0.1, 1.0);
		chordB.normalize(
				(i) -> chordB.get(i).getAmplitude(),
				(j, k) -> chordB.get(j).setAmplitude(k),
				0.1, 1.0);
		chordA.normalizePositive(
				(i) -> chordA.get(i).getArpPeriod(),
				(j, k) -> chordA.get(j).setArpPeriod(k));
		chordB.normalizePositive(
				(i) -> chordB.get(i).getArpPeriod(),
				(j, k) -> chordB.get(j).setArpPeriod(k));
		modulation.normalize(
				(i) -> modulation.get(i).getReverb(),
				(j, k) -> modulation.get(j).setReverb(k),
				0.4, 0.9);
		bass.normalize(
				(i) -> bass.get(i).getFilterFreq(),
				(j, k) -> bass.get(j).setFilterFreq(k),
				40.0, 500.0);
	}

	private void interpolateAll()
	{
		// runs multiple interpolation/smoothing processes on various parameters
		
		chordA.interpolate(
				(i) -> chordA.get(i).getAmplitude(),
				(j, k) -> chordA.get(j).setAmplitude(k));
		chordA.interpolate(
				(i) -> chordA.get(i).getArpPeriod(),
				(j, k) -> chordA.get(j).setArpPeriod(k));
		chordB.interpolate(
				(i) -> chordB.get(i).getAmplitude(),
				(j, k) -> chordB.get(j).setAmplitude(k));
		chordB.interpolate(
				(i) -> chordB.get(i).getArpPeriod(),
				(j, k) -> chordB.get(j).setArpPeriod(k));
		modulation.interpolate(
				(i) -> modulation.get(i).getReverb(),
				(j, k) -> modulation.get(j).setReverb(k));
		modulation.interpolate(
				(i) -> modulation.get(i).getModPeriod(),
				(j, k) -> modulation.get(j).setModPeriod(k));
		bass.interpolate(
				(i) -> bass.get(i).getFilterFreq(),
				(j, k) -> bass.get(j).setFilterFreq(k));
	}
	
	private static int calcPitch (String dir) throws NullPointerException
	{
		// converts compass directions into note names
		// returns an offset in semitones from C
		
		/*
		 * Pitch Chart
		 * position on circle of fifths, note name, semitone offset
		 * 0 C 0
		 * 1 G 7
		 * 2 D 2
		 * 3 A 9
		 * 4 E 4
		 * 5 B 11
		 * 6 F# 6
		 * 7 C# 1
		 * 8 Ab 8
		 * 9 Eb 3
		 * 10 Bb 10
		 * 11 F 5
		 */
		
		int inp = directionMap.get(dir);
		
		// scale to 12 tone 
		int twelveTone = (int) (inp - Math.floor(inp/4.0));
		
		if (twelveTone % 2 == 0)
		{
			return twelveTone;
		}
		else
		{
			twelveTone += 6;
			if (twelveTone > 12) twelveTone -= 12;
			return twelveTone;
		}
	}
	
	public static int[] calcPitchQuality (String waveDir, int windDir) {
		// factors in wind direction to get the quality
		// returns an array [pitch, quality]
		// pitch is a semitone offset from 0-11
		// quality is 0 for dominant, 1/2 for major, 3 for minor
		
		int[] arr = new int[2];
		double pitchScaled = calcPitch(waveDir) * 22.5; // scale up to 360 degrees
		double mean = (((double) windDir) + pitchScaled) / 2;
		
		arr[0] = (int) (mean / 30);
		arr[1] = (int) (((mean / 7.5) - (int) (mean / 7.5)) * 4);
		
		//System.out.println(arr[0] + " " + arr[1]);
		
		return arr;
	}
	
	public DataList<ChordNode> getChordA() {
		return chordA;
	}

	public DataList<ChordNode> getChordB() {
		return chordB;
	}
	
	public DataList<BassNode> getBass() {
		return bass;
	}

	public DataList<ModNode> getModulation() {
		return modulation;
	}
}