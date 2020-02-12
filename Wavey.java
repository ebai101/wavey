package ebai.javamusic.wavey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.softsynth.jmsl.DimensionNameSpace;
import com.softsynth.jmsl.JMSL;
import com.softsynth.jmsl.JMSLRandom;
import com.softsynth.jmsl.jsyn2.JSynMusicDevice;
import com.softsynth.jmsl.jsyn2.JSynUnitVoiceInstrument;
import com.softsynth.jmsl.score.Measure;
import com.softsynth.jmsl.score.Orchestra;
import com.softsynth.jmsl.score.Score;
import com.softsynth.jmsl.score.ScoreFrame;

public class Wavey {

	private static final boolean printMode = false; // change this to true to just output data to a file and exit
	private static final boolean procMode = true; // change this to false to disable processing
	
	private static WaveDataFetcher wdf;
	private static WaveDataParser parser;
	private static Score score;
	
	ScoreFrame scoreFrame;
	JSynUnitVoiceInstrument voiceA;
	JSynUnitVoiceInstrument voiceB;
	JSynUnitVoiceInstrument bassVoice;
	JSynUnitVoiceInstrument delay;
	
	void initialize()
	{
		// performs all initialization for JSyn and JMSL devices
		
		// init score
		score = new Score(5, 800, 400, "Wavey McWave Piece");
		
		// init jsyn
		JSynMusicDevice jsynMusicDevice = JSynMusicDevice.instance();
		jsynMusicDevice.open();
		
		// init voices
		voiceA = new JSynUnitVoiceInstrument(6, ebai.javamusic.wavey.PWMVoice.class.getName());
		voiceB = new JSynUnitVoiceInstrument(6, ebai.javamusic.wavey.SawVoice.class.getName());
		bassVoice = new JSynUnitVoiceInstrument(1, ebai.javamusic.wavey.BassVoice.class.getName());
		delay = new JSynUnitVoiceInstrument(6, ebai.javamusic.wavey.BetterDelayLineCircuit.class.getName());

		// init orchestra
		Orchestra orch = new Orchestra();
		orch.addInstrument(voiceA);
		orch.addInstrument(voiceB);
		orch.addInstrument(bassVoice);
		orch.addInstrument(delay);

		// patch fx
		delay.addSignalSource(voiceA.getOutput());
		delay.addSignalSource(voiceB.getOutput());
		score.setOrchestra(orch);

		// pan stuff
		orch.getJMSLMixerContainer().panAmpChange(0, 0.0, 0.5);
		orch.getJMSLMixerContainer().panAmpChange(1, 1.0, 0.5);
		orch.getJMSLMixerContainer().panAmpChange(2, 0.5, 0.75);
		orch.getJMSLMixerContainer().panAmpChange(3, 0.0, 0.2);
		orch.getJMSLMixerContainer().panAmpChange(4, 1.0, 0.2);
	}
	
	void write(WaveDataParser parser, Score score)
	{
		// composes the piece using the DataLists of parameters
		
		DataList<ChordNode> aList = parser.getChordA();
		DataList<ChordNode> bList = parser.getChordB();
		DataList<BassNode> bsList = parser.getBass();
		DataList<ModNode> modList = parser.getModulation();
		
		double duration, nextDuration, totalDuration;
		int pitchSet[];
		
		for (int i = aList.size()-1; i > 0; i--) // iterate backwards since the data is in reverse chronological order
		{
			DimensionNameSpace nsA = voiceA.getDimensionNameSpace();
			DimensionNameSpace nsB = voiceB.getDimensionNameSpace();
			DimensionNameSpace nsX = bassVoice.getDimensionNameSpace();
			DimensionNameSpace nsM = delay.getDimensionNameSpace();
			
			JMSLRandom.setSeed(modList.get(i).getRandomSeed());
			Measure m = score.addMeasure();
			m.setTempo(15); // 30 mins to 4 seconds
			
			// voice A
			score.setCurrentStaffNumber(0);	
			
			duration = 0.7 / aList.get(i).getArpPeriod();
			nextDuration = 0.7 / aList.get(i-1).getArpPeriod();
			pitchSet = getArpeggioPitchSet(aList.get(i).getPitch(), aList.get(i).getQuality());

			totalDuration = 0.0;
			while (totalDuration < 4.0)
			{
				double thisDur = linear(duration, nextDuration, totalDuration / 4.0);
				if (totalDuration + thisDur > 4.0) thisDur = 4.0 - totalDuration;
				if (thisDur < 0.001) break;
				score.addNote(nsA, new double[] {
					thisDur,
					pitchSet[JMSLRandom.choose(0, pitchSet.length)],
					linear(aList.get(i).getAmplitude(), aList.get(i-1).getAmplitude(), totalDuration / 4.0),
					thisDur/2,
					linear(1 / modList.get(i).getModPeriod(), 1 / modList.get(i-1).getModPeriod(), totalDuration / 4.0),
					JMSLRandom.choose(0.2, 2.0)
				});
				
				totalDuration += thisDur;
			}
			
			// voice B
			score.setCurrentStaffNumber(1);	
			duration = 0.7 / bList.get(i).getArpPeriod();
			nextDuration = 0.7 / bList.get(i-1).getArpPeriod();
			pitchSet = getArpeggioPitchSet(bList.get(i).getPitch(), bList.get(i).getQuality());

			totalDuration = 0.0;
			while (totalDuration < 4.0)
			{
				double thisDur = linear(duration, nextDuration, totalDuration / 4.0);
				if (totalDuration + thisDur > 4.0) thisDur = 4.0 - totalDuration;
				if (thisDur < 0.001) break;
				score.addNote(nsB, new double[] {
					thisDur,
					pitchSet[JMSLRandom.choose(0, pitchSet.length)],
					linear(bList.get(i).getAmplitude(), bList.get(i-1).getAmplitude(), totalDuration / 4.0),
					thisDur/2,
					linear(1 / modList.get(i).getModPeriod(), 1 / modList.get(i-1).getModPeriod(), totalDuration / 4.0)
				});
				
				totalDuration += thisDur;
			}
			
			// bass
			score.setCurrentStaffNumber(2);
			score.addNote(nsX, new double[] {
				4.0, bsList.get(i).getPitch(), 1.0, 4.0, bsList.get(i).getFilterFreq()
			});
			
			// modulation
			score.setCurrentStaffNumber(3);
			score.addNote(nsM, new double[] {
					4.0, 60, 0.8, 4.0, JMSLRandom.choose(0.25, 1.0), modList.get(i).getReverb()
			});	
		
			/*
			System.out.println(String.format("%d: A amp %f, A pd %f, A root %d, B amp %f, B pd %f, B root %d, Bs root %f, Bs freq %f",
					score.getCurrentMeasureNumber(),
					aList.get(i).getAmplitude(),
					aList.get(i).getArpPeriod(),
					aList.get(i).getPitch(),
					bList.get(i).getAmplitude(),
					bList.get(i).getArpPeriod(),
					bList.get(i).getPitch(),
					bsList.get(i).getPitch(),
					bsList.get(i).getFilterFreq()
					));
					*/
		}
	}
	
	int[] getArpeggioPitchSet (int pitch, int quality)
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
	
	double linear(double y1,double y2, double mu)
	{
		// linear interpolation
	   return(y1*(1-mu)+y2*mu);
	}
	
	void display()
	{
		// render and show the ScoreFrame
		
		scoreFrame = new ScoreFrame();
		scoreFrame.addScore(score);
		scoreFrame.pack();
		scoreFrame.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		// fetch data
		wdf = new WaveDataFetcher("https://www.ndbc.noaa.gov/data/realtime2/44090.spec");
		try {
			wdf.fetch();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// parse data
		parser = new WaveDataParser(wdf.getWaveData(), procMode);
		try {
			printDataPython();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		if (printMode) System.exit(0);
		
		// assemble piece
		JMSL.clock.setAdvance(0.2);
		Wavey wavey = new Wavey();
		wavey.initialize();
		wavey.write(parser, score);
		wavey.display();
	}
	
	public static void printDataPython() throws IOException
	{
		// outputs data in matplotlib-friendly syntax
		// for debugging and fun times
		
		File f = new File("src/ebai/javamusic/wavey/chart_data.py");
		if (f.exists()) f.delete();
		FileWriter fw = new FileWriter(f);
		PrintWriter pw = new PrintWriter(fw);
		
		// amplitude a
		pw.print("amplitude_a = [");
		for (int i = 0; i < parser.getChordA().size() - 1; i++)
		{
			pw.print(parser.getChordA().get(i).getAmplitude() + ", ");
		}
		pw.println(parser.getChordA().get(parser.getChordA().size()-1).getAmplitude() + "]");
		
		// amplitude b
		pw.print("amplitude_b = [");
		for (int i = 0; i < parser.getChordB().size() - 1; i++)
		{
			pw.print(parser.getChordB().get(i).getAmplitude() + ", ");
		}
		pw.println(parser.getChordB().get(parser.getChordB().size()-1).getAmplitude() + "]");
		
		// arp period a
		pw.print("arp_period_a = [");
		for (int i = 0; i < parser.getChordA().size() - 1; i++)
		{
			pw.print(parser.getChordA().get(i).getArpPeriod() + ", ");
		}
		pw.println(parser.getChordA().get(parser.getChordA().size()-1).getArpPeriod() + "]");
		
		// arp period b
		pw.print("arp_period_b = [");
		for (int i = 0; i < parser.getChordB().size() - 1; i++)
		{
			pw.print(parser.getChordB().get(i).getArpPeriod() + ", ");
		}
		pw.println(parser.getChordB().get(parser.getChordB().size()-1).getArpPeriod() + "]");
		
		pw.close();
	}
}