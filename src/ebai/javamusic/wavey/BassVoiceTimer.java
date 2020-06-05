package ebai.javamusic.wavey;

import com.jsyn.Synthesizer;
import com.softsynth.shared.time.TimeStamp;

public class BassVoiceTimer extends VoiceTimer {

	private BassVoice bassVoice;
	private DataList<BassNode> bassDataList;
	
	public BassVoiceTimer(Synthesizer s, BassVoice v, DataList<BassNode> dl, DataList<ModNode> ml) {
		super(s, ml);
		bassVoice = v;
		bassDataList = dl;
	}
	
	@Override
	public void build() {
		System.out.printf("Starting VoiceTimer...");
		
		double timeOffset = synth.getCurrentTime();
		
		for (int i = bassDataList.size()-1; i > 0; i--)
		{	
			bassVoice.noteOn(
					mtof(bassDataList.get(i).getPitch()), 
					0.7,
					new TimeStamp(timeOffset)
					);
			
			// filter
			bassVoice.filterFreq.set(
					bassDataList.get(i).getFilterFreq(),
					new TimeStamp(timeOffset)
					);
			
			timeOffset += 4.0;
		}
		System.out.println("done.");
	}
}
