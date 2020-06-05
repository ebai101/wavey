package ebai.javamusic.wavey;

import java.util.Random;

import com.jsyn.Synthesizer;
import com.softsynth.shared.time.TimeStamp;

public class VoiceTimer {

	protected Synthesizer synth;
	protected DataList<ChordNode> dataList;
	protected DataList<ModNode> modList;
	protected Random rand;
	private PlayableUnitVoice voice;
	
	public VoiceTimer(Synthesizer s, PlayableUnitVoice v, DataList<ChordNode> dl, DataList<ModNode> ml) {
		synth = s;
		voice = v;
		dataList = dl;
		modList = ml;
		rand = new Random();
	}
	
	protected VoiceTimer(Synthesizer s, DataList<ModNode> ml) {
		synth = s;
		modList = ml;
		rand = new Random();
	}
	
	public void build() {
		System.out.printf("Starting VoiceTimer...");
		
		double timeOffset = synth.getCurrentTime();
		double amplitude, nextAmplitude, duration, nextDuration, totalDuration;
		int pitchSet[];
		double modFreq, nextModFreq;
		
		for (int i = dataList.size()-1; i > 0; i--)
		{
			rand.setSeed(modList.get(i).getRandomSeed());
			
			// duration points
			duration = 0.7 / dataList.get(i).getArpPeriod();
			nextDuration = 0.7 / dataList.get(i-1).getArpPeriod();
			totalDuration = 0.0;
			
			// amplitude points
			amplitude = 0.7 * dataList.get(i).getAmplitude();
			nextAmplitude = dataList.get(i-1).getAmplitude();
			
			// frequency points
			pitchSet = getArpeggioPitchSet(dataList.get(i).getPitch(), dataList.get(i).getQuality());
			
			// mod points
			modFreq = modList.get(i).getModPeriod();
			nextModFreq = modList.get(i-1).getModPeriod();
			
			while (totalDuration < 4.0)
			{
				// note durations
				double thisDur = linear(duration, nextDuration, totalDuration / 4.0);
				if (totalDuration + thisDur > 4.0) thisDur = 4.0 - totalDuration;
				if (thisDur < 0.001) break;
				
				// set amplitudes
				voice.getAmp().set(
						linear(amplitude, nextAmplitude, totalDuration / 4.0),
						new TimeStamp(timeOffset + totalDuration + thisDur)
						);
				
				// set frequencies
				voice.getFreq().set(
						mtof(pitchSet[rand.nextInt(pitchSet.length)]),
						new TimeStamp(timeOffset + totalDuration + thisDur)	
						);
				
				// set mod frequencies
				voice.getModFreq().set(
						linear(modFreq, nextModFreq, totalDuration / 4.0),
						new TimeStamp(timeOffset + totalDuration + thisDur)
						);
				
				totalDuration += thisDur;
			}
			
			timeOffset += 4.0;
		}
		System.out.println("done.");
	}
	
	private static double linear(double y1, double y2, double mu)
	{
		// linear interpolation
	   return(y1*(1-mu)+y2*mu);
	}
	
	protected static double mtof(int note) {
		// midi to freq
	    return (double) (440.0 * Math.pow(2, (note - 69) / 12d));
	}
	
	private int[] getArpeggioPitchSet (int pitch, int quality)
	{
		// given a root pitch and a chord quality, return an
		// array of MIDI notes for the chord voices to choose from
		
		int[] minor = {0, 3, 7, 10, 12, 15, 19, 22};
		int[] major = {0, 4, 7, 11, 12, 16, 19, 23};
		int[] dominant = {0, 4, 7, 10, 12, 16, 19, 22};
		int[] result = new int[8];
		
		for (int i = 0; i < result.length; i++)
		{
			if (quality == 3)
			{
				// minor
				result[i] = (48 + pitch) + minor[i];
			}
			else if (quality == 1 || quality == 2)
			{
				// major
				result[i] = (48 + pitch) + major[i];
			}
			else if (quality == 0)
			{
				// dominant
				result[i] = (48 + pitch) + dominant[i];
			}
			else
			{
				System.err.println("Invalid quality: "+quality);
			}
		}

		return result;
	}
}
