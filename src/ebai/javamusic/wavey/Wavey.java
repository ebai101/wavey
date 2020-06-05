package ebai.javamusic.wavey;

import java.io.IOException;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.MixerStereoRamped;

public class Wavey {

	private static final boolean printMode = false; // change this to true to just output data to a file and exit
	private static final boolean procMode = true; // change this to false to disable processing
	private static WaveDataFetcher wdf;
	private static WaveDataParser parser;
	
	DataList<ChordNode> aList;
	DataList<ChordNode> bList;
	DataList<BassNode> bsList;
	DataList<ModNode> modList;
	
	Synthesizer synth;
	PWMVoice voiceA;
	SawVoice voiceB;
	BassVoice bassVoice;
	BetterDelayLineCircuit delay;
	MixerStereoRamped mix;
	LineOut lineOut;
	
	VoiceTimer aTimer;
	VoiceTimer bTimer;
	VoiceTimer bassTimer;
	
	private void startSynth() {
		synth = JSyn.createSynthesizer();
		synth.start();
		
		// line out, mixer
		synth.add(lineOut = new LineOut());
		synth.add(mix = new MixerStereoRamped(5));
		mix.output.connect(0, lineOut.input, 0);
		mix.output.connect(1, lineOut.input, 1);
		
		// delay send (channels 3/4)
		synth.add(delay = new BetterDelayLineCircuit());
		delay.aOutput2.connect(0, mix.input, 3);
		delay.aOutput2.connect(1, mix.input, 4);
		mix.gain.set(3, 0.5);
		mix.gain.set(4, 0.5);
		mix.pan.set(3, -1.0);
		mix.pan.set(4, 1.0);
		
		// voice A (channel 0)
		synth.add(voiceA = new PWMVoice());
		voiceA.output.connect(0, mix.input, 0);
		voiceA.output.connect(0, delay.input, 0);
		mix.gain.set(0, 0.5);
		mix.pan.set(0, -0.5);
		
		// voice B (channel 1)
		synth.add(voiceB = new SawVoice());
		voiceB.output.connect(0, mix.input, 1);
		voiceB.output.connect(0, delay.input, 0);
		mix.gain.set(1, 0.5);
		mix.pan.set(1, 0.5);
		
		// bass voice (channel 2, no delay send)
		synth.add(bassVoice = new BassVoice());
		bassVoice.output.connect(0, mix.input, 2);
		mix.gain.set(2, 0.5);
		mix.pan.set(2, 0.0);
		
		// build sequences
		aTimer = new VoiceTimer(synth, voiceA, aList, modList);
		aTimer.build();
		bTimer = new VoiceTimer(synth, voiceB, bList, modList);
		bTimer.build();
		bassTimer = new BassVoiceTimer(synth, bassVoice, bsList, modList);
		bassTimer.build();
		
		// all done
		System.out.println("starting synth");
		lineOut.start();
	}
	
	private void run() {
		aList = parser.getChordA();
		bList = parser.getChordB();
		bsList = parser.getBass();
		modList = parser.getModulation();
		
		startSynth();
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
		parser = new WaveDataParser(wdf.getWaveData(), procMode, printMode);
		
		new Wavey().run();
	}
}